package pro.outcome.data;
import pro.outcome.util.Arrays;


class UniqueConstraint {

	private final Field<?>[] _fields;

	public UniqueConstraint(Field<?> ... fields) {
		// Precondition checks are done on Entity.
		_fields = fields;
	}
	
	public String toString() {
		return Arrays.toString(_fields);
	}
	
	public int hashCode() {
		int hash = 7;
		for(int i=0; i<_fields.length; i++) {
			hash = 31 * hash + _fields[i].hashCode();
		}
		return hash;
	}
	
	public boolean equals(Object o) {
		if(this == o) {
			return true;
		}
		if(o == null) {
			return false;
		}
		if(o instanceof UniqueConstraint) {
			return Arrays.equalsIgnoreOrder(_fields, ((UniqueConstraint) o)._fields);
		}
		return false;
	}
	
	public Field<?>[] getFields() {
		return _fields;
	}

	public QueryArg[] toArgs(Instance<?> i) {
		QueryArg[] args = new QueryArg[_fields.length];
		for(int j=0; j<_fields.length; j++) {
			args[j] = new QueryArg(_fields[j], i.getValue(_fields[j]));
		}
		return args;
	}
}
