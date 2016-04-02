// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;
import pro.outcome.util.Arrays;
import pro.outcome.util.Strings;
import pro.outcome.util.IntegrityException;


public class Field<T> {

	// TODO add IGNORE_CASE constraint
	public enum Constraint { MANDATORY, READ_ONLY, UNIQUE, AUTO_GENERATED };
	public enum OnDelete { CASCADE, RESTRICT, SET_NULL };
	private final Entity<?> _parent;
	private final Class<T> _type;
	private final String _name;
	private final ValueGenerator<T> _default;
	private final boolean _indexed;
	private final boolean _mandatory;
	private final boolean _readOnly;
	private final boolean _unique;
	private final boolean _autoGenerated;
	private final boolean _foreignKey;
	private Entity<?> _related;

	// For Model:
	Field(Entity<?> parent, Class<T> cl, String name, boolean indexed, ValueGenerator<T> def, Constraint ... constraints) {
		// Note: null and empty checks are carried out on Model.addField 
		_parent = parent;
		_type = cl;
		_name = name;
		_indexed = indexed;
		_default = def;
		_mandatory = Arrays.contains(Constraint.MANDATORY, constraints);
		_readOnly = Arrays.contains(Constraint.READ_ONLY, constraints);
		_autoGenerated = Arrays.contains(Constraint.AUTO_GENERATED, constraints);
		_unique = Arrays.contains(Constraint.UNIQUE, constraints) || _autoGenerated;
		_foreignKey = Instance.class.isAssignableFrom(cl);		
		// Note: presence of onDelete constraint is checked on Model._addField
		if(_autoGenerated && def != null) {
			throw new IllegalArgumentException("fields with auto-generated constraints cannot have a default value");
		}
		// We need to delay fetching the related entity to prevent initialization order problems:
		_related = null;
	}

	public Entity<?> getEntity() {
		return _parent;
	}

	public Class<T> getType() {
		return _type;
	}

	public String getName() {
		return _name;
	}
	
	public String getFullName() {
		return getEntity().getName()+'.'+getName();
	}
	
	public T getDefaultValue() {
		return _default == null ? null : _default.generate();
	}

	public boolean isIndexed() {
		return _indexed;
	}

	public boolean isMandatory() {
		return _mandatory;
	}

	public boolean isReadOnly() {
		return _readOnly;
	}
	
	public boolean isUnique() {
		return _unique;
	}
	
	public boolean isAutoGenerated() {
		return _autoGenerated;
	}
	
	public boolean isForeignKey() {
		return _foreignKey;
	}
	
	public QueryArg toArg(T value) {
		return new QueryArg(this, value);
	}

	public QueryArg toArg(T value, QueryArg.Operator op) {
		return new QueryArg(this, value, op);
	}

	public String toString() {
		return getFullName();
	}

	// For Self and Instance:
	Object toPrimitive(Object value) {
		if(value == null) {
			return null;
		}
		// Convert foreign entities to their id's:
		if(Instance.class.isAssignableFrom(_type)) {
			Instance<?> i = (Instance<?>)value;
			// Validate that foreign instance key is set:
			if(!i.isPersisted()) {
				throw new IllegalStateException(Strings.expand("foreign entity [{}] has not been persisted", i));
			}
			return i.getId();
		}
		return value;
	}
	
	// For Instance:
	@SuppressWarnings("unchecked")
	T toObject(Object value) {
		if(value == null) {
			return null;
		}
		if(_foreignKey) {
			if(_related == null) {
				_related = Entities.getEntityForInstance(_type);
				if(_related == null) {
					throw new IntegrityException();
				}
			}
			return (T)_related.find((Long)value);
		}
		return (T)value;
	}
}
