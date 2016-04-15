// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.rest;
import static pro.outcome.util.Shortcuts.*;


public class StatusCode {

	public final int code;
	private final String _message;
	public final int httpCode;
	
	StatusCode(int code, String message, int httpStatus) {
		this.code = code;
		_message = message;
		this.httpCode = httpStatus;
	}
	
	public String expand(Object ... params) {
		if(params == null) {
			// TODO add a check. If message expects params, fail
			return _message;
		}
		return x(_message, params);
	}
	
	public String toString() {
		return Integer.toString(code);
	}
}
