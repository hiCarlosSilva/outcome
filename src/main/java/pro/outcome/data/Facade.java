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
import java.util.Iterator;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import pro.outcome.util.Checker;
import pro.outcome.util.ConstructorNotFoundException;
import pro.outcome.util.ConstructorNotVisibleException;
import pro.outcome.util.IntegrityException;
import pro.outcome.util.Logger;
import pro.outcome.util.Reflection;


public abstract class Facade<I extends Instance<?>> {

	private final DatastoreService _ds;
	private final Class<I> _instanceType;
	private final Model _model;
	private final Logger _logger;

	@SuppressWarnings("unchecked")
	protected Facade() {
		_ds = DatastoreServiceFactory.getDatastoreService();
		_instanceType = ((Class<I>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		_model = _newInstance().getModel();
		_logger = Logger.get(_instanceType);
		Entities.ref.load((Facade<Instance<?>>)this);
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
	}

	public void update(I i) {
		_checkPersisted(i);
		if(!i.hasUpdates()) {
			return;
		}
		_prepareUpdate(i);
		_put(i);
	}

	public void delete(Long id) {
		Checker.checkNull(id, "id");
		// TODO this is inneficient. Use a key
		delete(get(id));
	}

	// TODO use a transaction
	public void delete(I i) {
		Checker.checkNull(i, "i");
		_checkPersisted(i);
		_logger.info("deleting {} with id {}", i.getModel().getInstanceName(), i.getId());
		// Process dependencies:
		for(Dependency d : getModel().getDependencies()) {
			_logger.info("found dependency in {}", d.entity.getEntityName());
			Facade<Instance<?>> facade = Entities.ref.getEntity(d.entity.getEntityName());
			Iterator<Instance<?>> it = facade.iterate(d.foreignKey.toArg(i.getId()));
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
		_ds.delete(_getKeysFrom(list()));
	}

	public I get(Long id) {
		Checker.checkNull(id, "id");
		try {
			_logger.info("running query: SELECT * FROM {} WHERE id = {}", getClass().getSimpleName(), id);
			Entity e = _ds.get(KeyFactory.createKey(getClass().getSimpleName(), id));
			return _createFrom(e);
		}
		catch(EntityNotFoundException enfe) {
			return null;
		}
	}
	
	public I getSingle(QueryArg ... params) {
		Checker.checkEmpty(params, "params");
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
			I i = get((Long)idArg.getValue());
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
			return _createFrom(_ds.prepare(q).asSingleEntity());
		}
	}

	// TODO also needs to cater for queries where the id is specified
	protected I getSingle(Filter filter) {
		Checker.checkNull(filter, "filter");
		Query q = new Query(getClass().getSimpleName()).setFilter(filter);
		_logger.info("running query: {}", q);
		Entity e = _ds.prepare(q).asSingleEntity();
		if(e == null) {
			return null;
		}
		return _createFrom(e);
	}
	
	public List<I> list(QueryArg ... params) {
		// TODO what should the fetch options be?
		return _createFrom(_ds.prepare(_prepareQuery(params)).asList(FetchOptions.Builder.withChunkSize(10)));
	}

	public Iterator<I> iterate(QueryArg ... params) {
		// TODO what should the fetch options be?
		// We may return a data.Query object that allows people to specify how many elements to retrieve
		return new _ResultsIterator(_ds.prepare(_prepareQuery(params)).asIterator(FetchOptions.Builder.withChunkSize(10)));
	}
	
	private void _checkPersisted(I i) {
		if(!i.isPersisted()) {
			throw new IllegalArgumentException("entity has not been persisted");
		}
	}

	private Query _prepareQuery(QueryArg ... params) {
		Checker.checkNullElements(params, "params");
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
					_checkUnique(i, f, value);
				}
			}
			// All okay, set the value:
			i.flush(f, value);
		}
		_logger.info("inserting instance [{}]", i);
	}
	
	private void _prepareUpdate(I i) {
		// Check updated fields:
		for(Map.Entry<Field<?>, Object> entry : i.getUpdates()) {
			Field<?> f = entry.getKey();
			Object value = entry.getValue();
			// On update, we only need to validate unique constraints. All others are validated on Instance.setValue.
			if(f.isUnique() && value != null) {
				_checkUnique(i, f, value);
			}
			i.flush(f, value);
		}
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

	private void _checkUnique(Instance<?> i, Field<?> f, Object value) {
		Query q = new Query(i.getModel().getEntityName()).setFilter(new FilterPredicate(f.getName(), FilterOperator.EQUAL, value));
		Entity existing = _ds.prepare(q).asSingleEntity();
		if(existing != null) {
			throw new UniqueConstraintException(f, value);
		}
	}
	
	private I _createFrom(Entity e) {
		if(e == null) {
			return null;
		}
		I i = _newInstance();
		i.setGoogleEntity(e);
		return i;
	}
	
	private List<I> _createFrom(List<Entity> input) {
		// TODO implement this with an interface wrapper for better performance
		List<I> list = new ArrayList<I>();
		for(Entity e : input) {
			list.add(_createFrom(e));
		}
		return list;
	}
	
	private Iterable<Key> _getKeysFrom(Iterable<I> it) {
		// TODO implement this with an interface wrapper for better performance
		List<Key> list = new ArrayList<Key>();
		for(I i : it) {
			list.add(i.getGoogleEntity().getKey());
		}
		return list;
	}

	private I _newInstance() {
		try {
			return Reflection.createObject(_instanceType);
		}
		catch(ConstructorNotFoundException cnfe) {
			throw new IntegrityException(_instanceType.getSimpleName()+" needs to have an empty constructor");
		}
		catch(ConstructorNotVisibleException cnfe) {
			throw new IntegrityException(_instanceType.getSimpleName()+"'s empty constructor must be visible");
		}
	}
	
	// Converts from Iterator<Entity> to Iterator<I>:
	private class _ResultsIterator implements Iterator<I> {
		
		private final Iterator<Entity> _source;
		
		public _ResultsIterator(Iterator<Entity> source) {
			_source = source;
		}
		
		public boolean hasNext() {
			return _source.hasNext();
		}

		public void remove() {
			_source.remove();
		}
		
		public I next() {
			return _createFrom(_source.next());
		}
	}}
