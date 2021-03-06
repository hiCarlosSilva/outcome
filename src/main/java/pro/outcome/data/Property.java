// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;
import pro.outcome.util.Arrays;
import pro.outcome.util.IntegrityException;
import static pro.outcome.util.Shortcuts.*;



public class Property<T> {

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
	private final OnDelete _onDelete;
	private Entity<?> _related;

	// For Entity:
	Property(Entity<?> parent, Class<T> cl, String name, boolean indexed, ValueGenerator<T> def, OnDelete onDelete, Constraint ... constraints) {
		// Note: null and empty checks are carried out on Entity.addProperty 
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
		_onDelete = onDelete;
		// Extra checks:
		if(_autoGenerated && def != null) {
			throw new IllegalArgumentException("properties with auto-generated constraints cannot have a default value");
		}
		if(_foreignKey) {
			if(_onDelete == null) {
				throw new IllegalArgumentException("foreign keys require an on-delete constraint");
			}
			if(_indexed == false) {
				throw new IllegalArgumentException("foreign keys need to be indexed");
			}
		}
		// TODO check allowed data types
		// We need to delay fetching the related entity to prevent initialization order problems.
		// Delayed fetching is done in Entity.loadOnFirstUse.
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
	
	public Entity<?> getRelatedEntity() {
		return _related;
	}

	public OnDelete getOnDeleteAction() {
		return _onDelete;
	}
	
	public QueryArg isEqualTo(T value) {
		return new QueryArg(this, value, QueryArg.Operator.EQUAL);
	}
	
	public QueryArg isNotEqualTo(T value) {
		return new QueryArg(this, value, QueryArg.Operator.NOT_EQUAL);
	}

	public String toString() {
		return getFullName();
	}

	// For Entity:
	void setRelatedEntity(Entity<?> related) {
		if(related == null) {
			throw new IntegrityException();
		}
		_related = related;
	}

	// For Self and Instance:
	Object toPrimitive(Object value) {
		if(value == null) {
			return null;
		}
		// Convert enum to String:
		if(_type.isEnum()) {
			value = value.toString();
		}
		// Convert foreign entities to their id's:
		if(Instance.class.isAssignableFrom(_type)) {
			// Direct conversion to Long:
			if(value instanceof Long) {
				return value;
			}
			Instance<?> i = (Instance<?>)value;
			// Validate that foreign instance key is set:
			if(!i.isPersisted()) {
				throw new IllegalStateException(x("foreign entity [{}] has not been persisted", i));
			}
			return i.getId();
		}
		return value;
	}
	
	// For Instance:
	@SuppressWarnings({ "unchecked", "rawtypes" })
	T toObject(Object value) {
		if(value == null) {
			return null;
		}
		// Convert String to enum:
		if(_type.isEnum()) {
			return (T)Enum.valueOf((Class<? extends Enum>)_type, value.toString());
		}
		// Convert foreign key to Instance:
		if(_foreignKey) {
			return (T)_related.find((Long)value);
		}
		return (T)value;
	}
}
