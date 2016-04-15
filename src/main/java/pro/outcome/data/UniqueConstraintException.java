// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;
import static pro.outcome.util.Shortcuts.*;


public class UniqueConstraintException extends ConstraintException {

	UniqueConstraintException(Field<?> field, Object value) {
		super(x("unique constraint: entity with '{}' = '{}' already exists", field.getName(), value), field, value);
	}

	UniqueConstraintException(UniqueConstraint uc) {
		super(x("unique constraint: entity with fields {} already exists", uc), null, null);
	}

	private static final long serialVersionUID = 1L;
}
