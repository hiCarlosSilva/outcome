// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.data;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;


public class QueryArg {

	private final Field<?> _field;
	private final Object _value;
	
	// For Field
	<T> QueryArg(Field<T> field, T value) {
		_field = field;
		_value = value;
	}
	
	public Field<?> getField() {
		return _field;
	}
	
	public Object getValue() {
		return _value;
	}
	
	// For Facade:
	FilterPredicate toFilter() {
		return new FilterPredicate(_field.getName(), FilterOperator.EQUAL, _value);
	}
}
