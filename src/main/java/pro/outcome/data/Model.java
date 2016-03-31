// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import pro.outcome.data.Field.Constraint;
import pro.outcome.util.Checker;
import pro.outcome.util.ImmutableList;
import pro.outcome.util.ImmutableMap;
import pro.outcome.util.Reflection;
import pro.outcome.util.Strings;
import pro.outcome.util.IntegrityException;


public abstract class Model {

	public final Field<Long> id;
	public final Field<Date> timeCreated;
	public final Field<Date> timeUpdated;
	private final String _entityName;
	private final String _instanceName;
	private final Map<String,Field<?>> _fields;
	private final List<Dependency> _dependencies;
	private final Set<UniqueConstraint> _uConstraints;
	private boolean _naturalKeyAdded;
	
	protected Model(String entityName, String instanceName) {
		Checker.checkEmpty(entityName, "entityName");
		Checker.checkEmpty(instanceName, "instanceName");
		// TODO check whether this entity has been registered
		_entityName = entityName;
		_instanceName = instanceName;
		_fields = new HashMap<>();
		_dependencies = new ArrayList<>();
		_uConstraints = new HashSet<>();
		_naturalKeyAdded = false;
		id = addField(Long.class, "id", true, Constraint.MANDATORY, Constraint.AUTO_GENERATED);
		// TODO add defaults "now()"
		timeCreated = addField(Date.class, "timeCreated", true, new NowGenerator(), Constraint.MANDATORY, Constraint.READ_ONLY);
		timeUpdated = addField(Date.class, "timeUpdated", true, new NowGenerator(), Constraint.MANDATORY, Constraint.READ_ONLY);
		Entities.ref.register(this);
	}
	
	public String getEntityName() {
		return _entityName;
	}

	public String getInstanceName() {
		return _instanceName;
	}
	
	public ImmutableMap<String,Field<?>> getFields() {
		return new ImmutableMap<String,Field<?>>(_fields);
	}

	public abstract Field<?>[] getNaturalKeyFields();
	
	protected <T> Field<T> addField(Class<T> c, String name, boolean indexed, Constraint ... constraints) {
		return _addField(c, name, indexed, (DirectGenerator<T>)null, null, constraints);
	}

	protected <T> Field<T> addField(Class<T> c, String name, boolean indexed, T def, Constraint ... constraints) {
		return _addField(c, name, indexed, new DirectGenerator<T>(def), null, constraints);
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
		UniqueConstraint uc = new UniqueConstraint(fields);
		if(_uConstraints.contains(uc)) {
			throw new IllegalArgumentException(Strings.expand("a unique constraint with fields {} already exists", uc));
		}
		_uConstraints.add(uc);
	}
	
	// For Facade:
	List<Dependency> getDependencies() {
		return new ImmutableList<>(_dependencies);
	}
	
	// For Facade:
	Iterator<UniqueConstraint> getUniqueConstraints() {
		_addNaturalKeyConstraint();
		return _uConstraints.iterator();
	}
	
	private <T> Field<T> _addField(Class<T> c, String name, boolean indexed, ValueGenerator<T> def, Field.OnDelete onDelete, Constraint ... constraints) {
		Checker.checkNull(c, "c");
		Checker.checkEmpty(name, "name");
		Checker.checkNullElements(constraints, "constraints");
		if(_fields.containsKey(name)) {
			throw new IllegalArgumentException("field with name '"+name+"' already exists");
		}
		Field<T> f = new Field<T>(this, c, name, indexed, def, constraints);
		if(f.isForeignKey()) {
			if(onDelete == null) {
				throw new IllegalArgumentException("foreign keys require an on-delete constraint");
			}
			// Get the foreign entity:
			Model foreignEntity = (Model)Reflection.readField(c, "model", null);
			if(foreignEntity == null) {
				throw new IntegrityException();
			}
			// Record a delete dependency:
			foreignEntity._dependencies.add(new Dependency(this, f, onDelete));
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
			addUniqueConstraint(getNaturalKeyFields());
		}
	}
	
	private class DirectGenerator<T> implements ValueGenerator<T> {
		
		private final T _value;
		
		public DirectGenerator(T value) {
			_value = value;
		}

		public T generate() {
			return _value;
		}
	}

	// TODO substitute for regexp generator
	private class NowGenerator implements ValueGenerator<Date> {
		public Date generate() { return new Date(); }
	}
}
