// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;


public class AutoGenConstraintException extends ConstraintException {

	AutoGenConstraintException(Field<?> field, Object value) {
		super("auto-generated constraint: field '"+field.getName()+"' cannot be set", field, value);
	}

	private static final long serialVersionUID = 1L;
}
