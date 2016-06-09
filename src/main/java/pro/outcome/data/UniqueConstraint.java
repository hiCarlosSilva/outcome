package pro.outcome.data;
import pro.outcome.util.Arrays;


class UniqueConstraint {

	private final Property<?>[] _props;

	public UniqueConstraint(Property<?> ... props) {
		// Precondition checks are done on Entity.
		_props = props;
	}
	
	public String toString() {
		return Arrays.toString(_props);
	}
	
	public int hashCode() {
		int hash = 7;
		for(int i=0; i<_props.length; i++) {
			hash = 31 * hash + _props[i].hashCode();
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
			return Arrays.equalsIgnoreOrder(_props, ((UniqueConstraint) o)._props);
		}
		return false;
	}
	
	public Property<?>[] getProperties() {
		return _props;
	}

	public QueryArg[] toArgs(Instance<?> i) {
		QueryArg[] args = new QueryArg[_props.length];
		for(int j=0; j<_props.length; j++) {
			args[j] = new QueryArg(_props[j], i.getValue(_props[j]));
		}
		return args;
	}
}
