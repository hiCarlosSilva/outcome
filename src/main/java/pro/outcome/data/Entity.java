// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import pro.outcome.util.Checker;
import pro.outcome.util.IntegrityException;
import pro.outcome.util.Logger;
import pro.outcome.util.Reflection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;


public abstract class Entity<I extends Instance<?>> {

	private final DatastoreService _ds;
	private final Class<I> _instanceType;
	private final Model _model;
	private final Logger _logger;

	@SuppressWarnings("unchecked")
	protected Entity() {
		_ds = DatastoreServiceFactory.getDatastoreService();
		_instanceType = ((Class<I>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		// TODO we shouldn't need this. Need to revise the class loading mechanism
		//Reflection.load(_instanceType.getName());
		_model = (Model)Reflection.readField(_instanceType, "model", null);
		if(_model == null) {
			throw new IntegrityException();
		}
		_logger = Logger.get(_instanceType);
		Entities.register(this);
	}

	public Model getModel() {
		return _model;
	}

	public void insert(I i) {
		if(i.isPersisted()) {
			throw new IllegalArgumentException("entity has already been persisted");
		}
		_prepareInsert(i);
		_put(i);
		_logger.info("persisted with id {}", i.getId());
	}

	public boolean update(I i) {
		_checkPersisted(i);
		if(!i.hasUpdates()) {
			return false;
		}
		_prepareUpdate(i);
		_put(i);
		return true;
	}

	public boolean save(I i) {
		I existing = findSingle(i.getNaturalKeyAsArg());
		if(existing == null) {
			insert(i);
			return true;
		}
		existing.updateFrom(i);
		boolean updated = update(existing);
		if(!updated) {
			// We need to do this to carry over the primary key:
			i.setGoogleEntity(existing.getGoogleEntity());
		}
		return updated;
	}

	public void delete(Long id) {
		Checker.checkNull(id);
		// TODO this is inneficient. Use a key
		delete(find(id));
	}

	// TODO use a transaction
	public void delete(I i) {
		Checker.checkNull(i);
		_checkPersisted(i);
		_logger.info("deleting {} with id {}", i.getModel().getInstanceName(), i.getId());
		// Process dependencies:
		for(Dependency d : getModel().getDependencies()) {
			_logger.info("found dependency in {}", d.entity.getEntityName());
			@SuppressWarnings("unchecked")
			Entity<Instance<?>> facade = (Entity<Instance<?>>)Entities.getEntity(d.entity.getEntityName());
			Iterator<Instance<?>> it = facade.find(d.entity.id.toArg(i.getId())).iterate();
			while(it.hasNext()) {
				Instance<?> related = it.next();
				if(d.onDelete == Field.OnDelete.CASCADE) {
					facade.delete(related);
				}
				else if(d.onDelete == Field.OnDelete.RESTRICT) {
					// TODO onDeleteException??
					throw new RuntimeException();
				}
				else if(d.onDelete == Field.OnDelete.SET_NULL) {
					related.setValue(d.foreignKey, null);
					facade.update(related);
				}
				else {
					throw new IntegrityException(d.onDelete);
				}
			}
		}
		// Delete the instance:
		_logger.info("running query: DELETE FROM {} WHERE id = {}", getClass().getSimpleName(), i.getId());
		_ds.delete(i.getGoogleEntity().getKey());
	}

	// This method is protected to avoid making it avaiable to subclasses by default.
	// Subclasses need to explicitly expose this method for it to be used.
	protected void deleteAll() {
		_ds.delete(_getKeysFrom(find().list()));
	}

	public I find(Long id) {
		Checker.checkNull(id);
		try {
			_logger.info("running query: SELECT * FROM {} WHERE id = {}", getClass().getSimpleName(), id);
			com.google.appengine.api.datastore.Entity e = _ds.get(KeyFactory.createKey(getClass().getSimpleName(), id));
			return _createSafely(e);
		}
		catch(EntityNotFoundException enfe) {
			return null;
		}
	}
	
	public I findSingle(QueryArg ... params) {
		Checker.checkEmpty(params);
		List<Filter> filters = new ArrayList<>(params.length);
		QueryArg idArg = null;
		for(QueryArg p : params) {
			if(p.getField().getName().equals("id")) {
				idArg = p;
			}
			else {
				filters.add(p.toFilter());
			}
		}
		if(idArg != null) {
			// Retrieve entity by ID and compare parameters since querying on ID does not work:
			I i = find((Long)idArg.getValue());
			if(i == null) {
				return null;
			}
			// Check if entity matches filter values:
			for(QueryArg p : params) {
				if(p != idArg) {
					Object value = i.getValue(p.getField());
					if(value == null && p.getValue() != null) {
						return null;
					}
					if(!value.equals(p.getValue())) {
						return null;
					}
				}
			}
			return i;
		}
		else {
			// Retrieve entity based on filters:
			Filter f = filters.size() > 1 ? new CompositeFilter(CompositeFilterOperator.AND, filters) : filters.get(0);
			Query q = new Query(getClass().getSimpleName()).setFilter(f);
			_logger.info("running query: {}", q);
			return _createSafely(_ds.prepare(q).asSingleEntity());
		}
	}
	
	public pro.outcome.data.Query<I> find(QueryArg ... params) {
		return new pro.outcome.data.Query<I>(_ds.prepare(_prepareQuery(params)), _instanceType);
	}
	
	private void _checkPersisted(I i) {
		if(!i.isPersisted()) {
			throw new IllegalArgumentException("entity has not been persisted");
		}
	}

	private Query _prepareQuery(QueryArg ... params) {
		Checker.checkNullElements(params);
		Query q = new Query(getClass().getSimpleName());
		if(params.length > 0) {
			List<Filter> filters = new ArrayList<>(params.length);
			for(QueryArg p : params) {
				filters.add(p.toFilter());
			}
			Filter f = filters.size() > 1 ? new CompositeFilter(CompositeFilterOperator.AND, filters) : filters.get(0);
			q.setFilter(f);
		}
		_logger.info("running query: {}", q);
		return q;
	}

	// TODO embed into insert
	private void _prepareInsert(I i) {
    	// Validate constraints:
		// Note: the following constraints are already validated on Instance.setValue:
		// Data type, format, read-only, auto-gen. Mandatory is also validated, but we need to check for omitted fields.
		for(Field<?> f : i.getModel().getFields().values()) {
			// Skip the primary key:
			if(f == i.getModel().id) {
				continue;
			}
			Object value = i.getValue(f);
			// Automatically generate:
			if(f.isAutoGenerated()) {
				// TODO add generators
				throw new IntegrityException();
			}
			// Default value:
			if(value == null) {
				Object def = f.getDefaultValue();
				if(def != null) {
					value = def;
				}
			}
			// Mandatory:
			if(f.isMandatory()) {
				if(value == null) {
					throw new MandatoryConstraintException(f);
				}
			}
			// Unique:
			if(f.isUnique() && value != null) {
				// If the field is auto-generated, we already checked for uniqueness above.
				if(!f.isAutoGenerated()) {
					_checkUnique(i, f, value, true);
				}
			}
			// All okay, set the value. Note that we will lose some updates in case
			// one field validation fails after others have been validated successfully.
			// However, because this is an unrecoverable exception, we don't care about that.
			i.flush(f, value);
		}
		_checkUniqueConstraints(i, true);
		_logger.info("inserting instance [{}]", i);
	}
	
	// TODO we are saving updates even when the updated object has the same data as
	// the object in the data store
	// TODO embed into update
	private void _prepareUpdate(I i) {
		// Check updated fields:
		for(Map.Entry<Field<?>, Object> entry : i.getUpdates()) {
			Field<?> f = entry.getKey();
			Object value = entry.getValue();
			// On update, we only need to validate unique constraints. All others are validated on Instance.setValue.
			if(f.isUnique() && value != null) {
				_checkUnique(i, f, value, false);
			}
			i.flush(f, value);
		}
		_checkUniqueConstraints(i, false);
		i.flush(i.getModel().timeUpdated, new Date());
		_logger.info("updating instance [{}]", i);
	}
	
	private void _put(I i) {
    	// Consistency check:
		if(i.hasUpdates()) {
			throw new IntegrityException();
		}
		// Persist:
    	_ds.put(i.getGoogleEntity());
	}

	// TODO performance - we retrieve "existing" multiple times
	private void _checkUnique(Instance<?> i, Field<?> f, Object value, boolean insert) {
		Instance<?> existing = findSingle(new QueryArg(f, value));
		if(existing != null) {
			if(insert) {
				throw new UniqueConstraintException(f, value);				
			}
			if(!existing.equals(i)) {
				throw new UniqueConstraintException(f, value);
			}
		}
	}
	
	private void _checkUniqueConstraints(Instance<?> i, boolean insert) {
		System.out.println("checking unique constraints for: "+i);
		Iterator<UniqueConstraint> it = i.getModel().getUniqueConstraints();
		System.out.println("has unique constraints: "+it.hasNext());
		while(it.hasNext()) {
			UniqueConstraint uc = it.next();
			System.out.println("checking unique constraint: "+uc);
			System.out.println("args: "+Arrays.toString(uc.toArgs(i)));
			Instance<?> existing = findSingle(uc.toArgs(i));
			System.out.println("entity returned: "+existing);
			if(existing != null) {
				if(insert) {
					throw new UniqueConstraintException(uc);
				}
				if(!existing.equals(i)) {
					throw new UniqueConstraintException(uc);
				}
			}
		}
	}
	
	private I _createSafely(com.google.appengine.api.datastore.Entity e) {
		if(e == null) {
			return null;
		}
		return Instance.newFrom(_instanceType, e);
	}
	
	private Iterable<Key> _getKeysFrom(Iterable<I> it) {
		// TODO implement this with an interface wrapper for better performance
		List<Key> list = new ArrayList<Key>();
		for(I i : it) {
			list.add(i.getGoogleEntity().getKey());
		}
		return list;
	}

}
