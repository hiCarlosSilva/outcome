// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.data;


public class UniqueConstraintException extends ConstraintException {

	UniqueConstraintException(Field<?> field, Object value) {
		super("unique constraint: field '"+field.getName()+"' already contains value '"+value+"'", field, value);
	}

	private static final long serialVersionUID = 1L;
}
