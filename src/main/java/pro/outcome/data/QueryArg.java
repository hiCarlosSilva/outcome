// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;
import pro.outcome.util.Strings;
import pro.outcome.util.IntegrityException;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;


public class QueryArg {

	// TYPE:
	public static enum Operator { EQUAL, NOT_EQUAL };
	
	// INSTANCE:
	private final Field<?> _field;
	private final Object _value;
	private final Operator _op;
	
	// For Field
	<T> QueryArg(Field<T> field, Object value, Operator op) {
		if(!field.isIndexed()) {
			throw new IllegalArgumentException(Strings.expand("cannot query field {} because it is not indexed", field.getFullName()));
		}
		_field = field;
		_value = value;
		_op = op;
	}
	
	// For Field
	<T> QueryArg(Field<T> field, Object value) {
		this(field, value, Operator.EQUAL);
	}

	public Field<?> getField() {
		return _field;
	}
	
	public Object getValue() {
		return _value;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(_field);
		sb.append(" ");
		sb.append(_op);
		sb.append(" ");
		sb.append(_value);
		sb.append("]");
		return sb.toString();
	}

	// For Facade:
	FilterPredicate toFilter() {
		return new FilterPredicate(_field.getName(), _getOperator(), _field.toPrimitive(_value));
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
