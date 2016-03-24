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

import pro.outcome.data.Field.Constraint;
import pro.outcome.util.Checker;
import pro.outcome.util.ImmutableList;
import pro.outcome.util.ImmutableMap;


public abstract class Model {

	public final Field<Long> id;
	public final Field<Date> timeCreated;
	public final Field<Date> timeUpdated;
	private final String _entityName;
	private final String _instanceName;
	private final Map<String,Field<?>> _fields;
	private final List<Dependency> _dependencies;
	
	protected Model(String entityName, String instanceName) {
		Checker.checkEmpty(entityName, "entityName");
		Checker.checkEmpty(instanceName, "instanceName");
		// TODO check whether this entity has been registered
		_entityName = entityName;
		_instanceName = instanceName;
		_fields = new HashMap<String,Field<?>>();
		_dependencies = new ArrayList<Dependency>();
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

	protected <T> Field<T> addField(Class<T> cl, String name, boolean indexed, Constraint ... constraints) {
		return addField(cl, name, indexed, (DirectGenerator<T>)null, constraints);
	}

	protected <T> Field<T> addField(Class<T> cl, String name, boolean indexed, T def, Constraint ... constraints) {
		return addField(cl, name, indexed, new DirectGenerator<T>(def), constraints);
	}

	protected <T> Field<T> addField(Class<T> cl, String name, boolean indexed, ValueGenerator<T> def, Constraint ... constraints) {
		Checker.checkNull(cl, "cl");
		Checker.checkEmpty(name, "name");
		Checker.checkNullElements(constraints, "constraints");
		if(_fields.containsKey(name)) {
			throw new IllegalArgumentException("field with name '"+name+"' already exists");
		}
		Field<T> f = new Field<T>(this, cl, name, indexed, def, constraints);
		_fields.put(name, f);
		return f;
	}
	
	protected Field<Long> addForeignKey(String name, Model foreignEntity, Field.OnDelete onDelete, Constraint ... constraints) {
		// TODO check that OnDelete.SET_NULL cannot exist with Constraint.READ_ONLY
		Field<Long> foreignKey = addField(Long.class, name, true, (ValueGenerator<Long>)null, constraints);
		foreignEntity._dependencies.add(new Dependency(this, foreignKey, onDelete));
		return foreignKey;
	}
	
	// For Facade:
	List<Dependency> getDependencies() {
		return new ImmutableList<Dependency>(_dependencies);
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
