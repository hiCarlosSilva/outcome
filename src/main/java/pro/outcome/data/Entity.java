// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.lang.reflect.ParameterizedType;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.PreparedQuery;
import pro.outcome.util.Checker;
import pro.outcome.util.ImmutableMap;
import pro.outcome.util.Logger;
import pro.outcome.util.Strings;
import pro.outcome.util.IntegrityException;
import pro.outcome.util.IllegalUsageException;
import pro.outcome.data.Field.Constraint;


public abstract class Entity<I extends Instance<?>> {

	// Data structure fields:
	public final Field<Long> id;
	public final Field<Date> timeCreated;
	public final Field<Date> timeUpdated;
	private final Class<I> _instanceType;
	private final Map<String,Field<?>> _fields;
	private final List<Dependency> _dependencies;
	private final Set<UniqueConstraint> _uConstraints;
	private boolean _naturalKeyAdded;
	// Data management fields:
	private final DatastoreService _ds;
	private final Logger _logger;
	private boolean _loaded;

	@SuppressWarnings("unchecked")
	protected Entity() {
		// Data structure:
		_instanceType = ((Class<I>)((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0]);
		_fields = new HashMap<>();
		_dependencies = new ArrayList<>();
		_uConstraints = new HashSet<>();
		_naturalKeyAdded = false;
		id = addField(Long.class, "id", true, Constraint.MANDATORY, Constraint.AUTO_GENERATED);
		// TODO add defaults "now()"
		timeCreated = addField(Date.class, "timeCreated", true, new Generators.Now(), Constraint.MANDATORY, Constraint.READ_ONLY);
		timeUpdated = addField(Date.class, "timeUpdated", true, new Generators.Now(), Constraint.MANDATORY, Constraint.READ_ONLY);		
		// Data management:
		_ds = DatastoreServiceFactory.getDatastoreService();
		_logger = Logger.get(getClass());
		_loaded = false;
	}

	protected Logger getLogger() {
		return _logger;
	}

	// Data structure methods:
	public abstract Field<?>[] getNaturalKeyFields();
	
	public String getName() {
		return getClass().getSimpleName();
	}

	public String getInstanceName() {
		return _instanceType.getSimpleName();
	}

	public Class<I> getInstanceClass() {
		return _instanceType;
	}
	
	public ImmutableMap<String,Field<?>> getFields() {
		return new ImmutableMap<String,Field<?>>(_fields);
	}

	
	protected <T> Field<T> addField(Class<T> c, String name, boolean indexed, Constraint ... constraints) {
		return _addField(c, name, indexed, (Generators.Direct<T>)null, null, constraints);
	}

	protected <T> Field<T> addField(Class<T> c, String name, boolean indexed, T def, Constraint ... constraints) {
		return _addField(c, name, indexed, new Generators.Direct<T>(def), null, constraints);
	}

	protected <T> Field<T> addField(Class<T> c, String name, boolean indexed, ValueGenerator<T> def, Constraint ... constraints) {
		return _addField(c, name, indexed, def, null, constraints);
	}

	protected <T> Field<T> addField(Class<T> c, String name, boolean indexed, Field.OnDelete onDelete, Constraint ...constraints) {
		return _addField(c, name, indexed, null, onDelete, constraints);
	}

	protected void addUniqueConstraint(Field<?> ... fields) {
		_addNaturalKeyConstraint();
		if(fields == null || fields.length==0) {
			return;
		}
		Checker.checkNullElements(fields);
		Checker.checkDuplicateElements(fields);
		if(fields.length == 1) {
			if(fields[0].isUnique()) {
				throw new IllegalArgumentException(Strings.expand("field {} is unique, do not add it as a constraint", fields[0]));
			}
			else {
				throw new IllegalArgumentException("constraints must have more than one field (use UNIQUE instead)");
			}
		}
		UniqueConstraint uc = new UniqueConstraint(fields);
		if(_uConstraints.contains(uc)) {
			throw new IllegalArgumentException(Strings.expand("a unique constraint with fields {} already exists", uc));
		}
		_uConstraints.add(uc);
	}
	
	// Data management methods:
	public void insert(I i) {
		Checker.checkNull(i);
		_checkLoaded();
		if(i.isPersisted()) {
			throw new IllegalArgumentException("entity has already been persisted");
		}
		// Get a snapshot of updates to check constraints:
		Set<Field<?>> updatedFields = i.getUpdatedFields();
    	// Validate constraints:
		// Note: the following constraints are already validated on Instance.setValue:
		// Data type, format, read-only, auto-generated. Mandatory is also validated
		// on Instance.setValue, but we need to check for omitted fields.
		for(Field<?> f : getFields().values()) {
			// Skip the primary key:
			if(f == id) {
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
		_checkUniqueConstraints(i, true, updatedFields);
		_logger.info("inserting instance [{}]", i);
		_put(i);
		_logger.info("persisted with id {}", i.getId());
	}

	public boolean update(I i) {
		Checker.checkNull(i);
		_checkLoaded();
		_checkPersisted(i);
		if(!i.hasUpdates()) {
			return false;
		}
		// Get a snapshot of updates to check constraints:
		Set<Field<?>> updatedFields = i.getUpdatedFields();
		// Check updated fields:
		for(Entry<Field<?>, Object> entry : i.getUpdates()) {
			Field<?> f = entry.getKey();
			Object value = entry.getValue();
			// On update, we only need to validate unique constraints.
			// All others are validated on Instance.setValue.
			if(f.isUnique() && value != null) {
				_checkUnique(i, f, value, false);
			}
			i.flush(f, value);
		}
		_checkUniqueConstraints(i, false, updatedFields);
		i.flush(timeUpdated, new Date());
		_logger.info("updating instance [{}]", i);
		_put(i);
		return true;
	}

	public boolean save(I i) {
		Checker.checkNull(i);
		_checkLoaded();
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

	public void delete(I i) {
		Checker.checkNull(i);
		_checkLoaded();
		_checkPersisted(i);
		_logger.info("deleting {} with id {}", getInstanceName(), i.getId());
		// Process dependencies:
		for(Dependency d : _dependencies) {
			_logger.info("found dependency in {}", d.entity.getName());
			Iterator<Instance<?>> it = d.findInstancesRelatedTo(i).iterate();
			while(it.hasNext()) {
				Instance<?> related = it.next();
				if(d.onDelete == Field.OnDelete.CASCADE) {
					d.entity.delete(related);
				}
				else if(d.onDelete == Field.OnDelete.RESTRICT) {
					// TODO onDeleteException??
					throw new RuntimeException();
				}
				else if(d.onDelete == Field.OnDelete.SET_NULL) {
					related.setValue(d.foreignKey, null);
					d.entity.update(related);
				}
				else {
					throw new IntegrityException(d.onDelete);
				}
			}
		}
		// Delete the instance:
		_logger.info("running query: DELETE FROM {} WHERE id = {}", getName(), i.getId());
		_ds.delete(i.getGoogleEntity().getKey());
	}

	// This method is protected to avoid making it available to subclasses by default.
	// Entity classes need to explicitly expose this method to allow its use.
	protected void deleteAll() {
		_logger.info("running query: DELETE FROM {}", getName());
		_ds.delete(_getKeysFrom(find().iterate()));
	}

	public I find(Long id) {
		Checker.checkNull(id);
		_checkLoaded();
		try {
			_logger.info("running query: SELECT * FROM {} WHERE id = {}", getName(), id);
			com.google.appengine.api.datastore.Entity e = _ds.get(KeyFactory.createKey(getName(), id));
			_logger.info(e == null ? "{} not found" : "{} found", getInstanceName());
			return _createSafely(e);
		}
		catch(EntityNotFoundException enfe) {
			return null;
		}
	}
	
	public I findSingle(QueryArg ... params) {
		Checker.checkEmpty(params);
		_checkLoaded();
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
						_logger.info("field {} does not match", p.getField().getFullName());
						return null;
					}
					if(!value.equals(p.getValue())) {
						_logger.info("field {} does not match", p.getField().getFullName());
						return null;
					}
				}
			}
			return i;
		}
		else {
			// Retrieve entity based on filters:
			Filter f = filters.size() > 1 ? new CompositeFilter(CompositeFilterOperator.AND, filters) : filters.get(0);
			Query q = new Query(getName()).setFilter(f);
			_logger.info("running query: {}", q);
			com.google.appengine.api.datastore.Entity e = _ds.prepare(q).asSingleEntity();
			_logger.info(e == null ? "{} not found" : "{} found", getInstanceName());
			return _createSafely(e);
		}
	}
	
	public pro.outcome.data.Query<I> find(QueryArg ... params) {
		_checkLoaded();
		PreparedQuery pq = _ds.prepare(_prepareQuery(params));
		_logger.info("running query: {}", pq);
		return new pro.outcome.data.Query<I>(pq, _instanceType);
	}
	
	// For Entities:
	void load() {
		if(_loaded) {
			throw new IllegalStateException("already loaded");
		}
		_loaded = true;
	}

	@SuppressWarnings("unchecked")
	private <T> Field<T> _addField(Class<T> c, String name, boolean indexed, ValueGenerator<T> def, Field.OnDelete onDelete, Constraint ... constraints) {
		Checker.checkNull(c);
		Checker.checkEmpty(name);
		Checker.checkNullElements(constraints);
		if(_fields.containsKey(name)) {
			throw new IllegalArgumentException("field with name '"+name+"' already exists");
		}
		Field<T> f = new Field<T>(this, c, name, indexed, def, constraints);
		if(f.isForeignKey()) {
			if(onDelete == null) {
				throw new IllegalArgumentException("foreign keys require an on-delete constraint");
			}
			// Get the foreign entity:
			Entity<?> foreignEntity = Entities.getEntityForInstance(c);
			if(foreignEntity == null) {
				throw new IntegrityException();
			}
			// Record a delete dependency:
			foreignEntity._dependencies.add(new Dependency((Entity<Instance<?>>)this, f, onDelete));
		}
		else {
			// TODO check allowed data types
		}
		_fields.put(name, f);
		return f;
	}
	
	private void _addNaturalKeyConstraint() {
		if(!_naturalKeyAdded) {
			_naturalKeyAdded = true;
			Field<?>[] fields = getNaturalKeyFields();
			if(fields != null) {
				if(fields.length == 1) {
					if(!fields[0].isUnique()) {
						throw new IllegalUsageException(Strings.expand("field {} cannot be a natural key because it is not unique", fields[0]));
					}
					else {
						// We don't want to add a single field as a constraint:
						fields = null;
					}
				}
			}
			addUniqueConstraint(fields);
		}
	}

	private void _checkLoaded() {
		if(!_loaded) {
			throw new IllegalStateException(getName()+" has not been loaded (see Entities.load)");
		}
	}

	private void _checkPersisted(I i) {
		if(!i.isPersisted()) {
			throw new IllegalArgumentException("entity has not been persisted");
		}
	}

	private Query _prepareQuery(QueryArg ... params) {
		Checker.checkNullElements(params);
		Query q = new Query(getName());
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
	
	private void _checkUniqueConstraints(Instance<?> i, boolean insert, Set<Field<?>> updatedFields) {
		Iterator<UniqueConstraint> it = _getUniqueConstraints();
		while(it.hasNext()) {
			UniqueConstraint uc = it.next();
			// Check if any of the constraint's fields have been updated:
			boolean updated = false;
			checkUpdates:
			for(Field<?> f : uc.getFields()) {
				if(updatedFields.contains(f)) {
					updated = true;
					break checkUpdates;
				}
			}
			if(updated) {
				// They have, we need to check if the constraint has been violated:
				Instance<?> existing = findSingle(uc.toArgs(i));
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
	}
	
	private Iterator<UniqueConstraint> _getUniqueConstraints() {
		_addNaturalKeyConstraint();
		return _uConstraints.iterator();
	}

	private I _createSafely(com.google.appengine.api.datastore.Entity e) {
		if(e == null) {
			return null;
		}
		return Instance.newFrom(_instanceType, e);
	}
	
	private Iterable<Key> _getKeysFrom(final Iterator<I> it) {
		return new Iterable<Key>() {
			public Iterator<Key> iterator() {
				return new Iterator<Key>() {
					private final Iterator<I> _it = it;					
					public boolean hasNext() { return _it.hasNext(); }
					public Key next() { return _it.next().getGoogleEntity().getKey(); }
					public void remove() { _it.remove(); }
				};
			}
		};
	}
}
