// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.rest;
import java.util.Map;
import java.util.HashMap;
import javax.servlet.http.HttpServletResponse;

import pro.outcome.util.Strings;


public class StatusCodes {

	private final static Map<Integer,StatusCode> _statusCodes = new HashMap<Integer,StatusCode>();
	
	protected static StatusCode add(int status, String message, int httpStatus) {
		if(_statusCodes.containsKey(status)) {
			throw new IllegalArgumentException(Strings.expand("status code {} already exists", status));
		}
		StatusCode code = new StatusCode(status, message, httpStatus);
		_statusCodes.put(status, code);
		return code;
	}

	public static final StatusCode OK = add(0, "OK", HttpServletResponse.SC_OK);
	public static final StatusCode NOT_FOUND = add(1, "'{}': endpoint not found", HttpServletResponse.SC_NOT_FOUND);
	public static final StatusCode VERSION_DEPRECATED = add(2, "version {} has been deprecated", HttpServletResponse.SC_GONE);
	public static final StatusCode METHOD_NOT_ALLOWED = add(3, "{}: method not allowed", HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	public static final StatusCode PARAM_NOT_FOUND = add(4, "mandatory parameter '{}' not found", HttpServletResponse.SC_BAD_REQUEST);
	public static final StatusCode INVALID_PARAM = add(5, "unexpected value for parameter '{}': '{}'", HttpServletResponse.SC_BAD_REQUEST);
	public static final StatusCode NOT_AUTHENTICATED = add(6, "user is not authenticated", HttpServletResponse.SC_UNAUTHORIZED);
	public static final StatusCode SESSION_CLOSED = add(7, "session is closed", HttpServletResponse.SC_UNAUTHORIZED);
	public static final StatusCode RESOURCE_NOT_FOUND = add(8, "{} was not found: '{}'", HttpServletResponse.SC_NOT_FOUND);
	// TODO remove public static final StatusCode INVALID_SESSION = add(8, "could not find user session", HttpServletResponse.SC_UNAUTHORIZED);
	// TODO remove public static final StatusCode NO_ACCESS = add(7, "user does not have permissions to access the resource requested", HttpServletResponse.SC_FORBIDDEN);
	public static final StatusCode UNEXPECTED = add(999, "unexpected error occurred: {}", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
}
