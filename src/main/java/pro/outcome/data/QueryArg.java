// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import pro.outcome.util.IntegrityException;
import static pro.outcome.util.Shortcuts.*;


public class QueryArg {

	// TYPE:
	public static enum Operator { EQUAL, NOT_EQUAL };
	
	// INSTANCE:
	private final Property<?> _property;
	private final Object _value;
	private final Operator _op;
	
	// For Property:
	<T> QueryArg(Property<T> property, Object value, Operator op) {
		if(!property.isIndexed()) {
			throw new IllegalArgumentException(x("cannot query property {} because it is not indexed", property.getFullName()));
		}
		_property = property;
		_value = value;
		_op = op;
	}
	
	// For Property:
	<T> QueryArg(Property<T> property, Object value) {
		this(property, value, Operator.EQUAL);
	}

	public Property<?> getProperty() {
		return _property;
	}
	
	public Object getValue() {
		return _value;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(_property);
		sb.append(" ");
		sb.append(_op);
		sb.append(" ");
		sb.append(_value);
		sb.append("]");
		return sb.toString();
	}

	// For Facade:
	FilterPredicate toFilter() {
		return new FilterPredicate(_property.getName(), _getOperator(), _property.toPrimitive(_value));
	}
	
	private FilterOperator _getOperator() {
		if(_op == Operator.EQUAL) {
			return FilterOperator.EQUAL;
		}
		if(_op == Operator.NOT_EQUAL) {
			return FilterOperator.NOT_EQUAL;
		}
		throw new IntegrityException(_op);
	}
}
