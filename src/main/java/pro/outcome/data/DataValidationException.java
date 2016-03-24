// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;


// TODO extend from ManagedException
public abstract class DataValidationException extends RuntimeException {

	private final Field<?> _field;
	private final Object _value;
	
	protected DataValidationException(String message, Field<?> field, Object value) {
		super(message);
		_field = field;
		_value = value;
	}
	
	public Field<?> getField() {
		return _field;
	}
	
	public Object getValue() {
		return _value;
	}

	private static final long serialVersionUID = 1L;
}
