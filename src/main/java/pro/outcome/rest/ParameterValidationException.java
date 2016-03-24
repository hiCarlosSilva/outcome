// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.rest;


public class ParameterValidationException extends EndpointException {

	public ParameterValidationException(String parameterName, Object value) {
		super(StatusCodes.INVALID_PARAM, parameterName, value);
	}	

	public ParameterValidationException(String parameterName) {
		super(StatusCodes.PARAM_NOT_FOUND, parameterName);
	}	

	private static final long serialVersionUID = 1L;
}
