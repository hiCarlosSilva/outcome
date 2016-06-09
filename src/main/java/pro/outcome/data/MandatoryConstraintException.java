// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;


public class MandatoryConstraintException extends ConstraintException {

	MandatoryConstraintException(Property<?> prop) {
		super("mandatory constraint: property '"+prop.getName()+"' cannot be set to NULL", prop, null);
	}

	private static final long serialVersionUID = 1L;
}
