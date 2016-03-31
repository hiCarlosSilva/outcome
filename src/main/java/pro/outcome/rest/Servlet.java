// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.rest;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pro.outcome.rest.Request.HTTP_METHOD;
import pro.outcome.data.Config;
import pro.outcome.util.Checker;
import pro.outcome.util.IntegrityException;
import pro.outcome.util.Reflection;


public abstract class Servlet extends HttpServlet {

	// TYPE:
	static final String CHARSET = "UTF-8";

	// INSTANCE:
	private final List<AccessChecker> _checkers;
	private final boolean _doGetOverridden;
	private final boolean _doPostOverridden;
	
	protected Servlet() {
		super();
		_checkers = new ArrayList<AccessChecker>();
		_doGetOverridden = Reflection.getDeclaredMethod(true, getClass(), "doGet", Request.class, Response.class) != null;
		_doPostOverridden = Reflection.getDeclaredMethod(true, getClass(), "doPost", Request.class, Response.class) != null;
	}
	
	public final void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_process(HTTP_METHOD.GET, req, resp);
	}

	public final void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_process(HTTP_METHOD.POST, req, resp);
	}

	public final void doTrace(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_process(HTTP_METHOD.TRACE, req, resp);
	}
	
	public final void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_process(HTTP_METHOD.PUT, req, resp);
	}

	public final void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_process(HTTP_METHOD.DELETE, req, resp);
	}

	protected void doGet(Request req, Response resp) throws IOException {
		// Code should never get here:
		throw new IntegrityException();
	}

	protected void doPost(Request req, Response resp) throws IOException {
		// Code should never get here:
		throw new IntegrityException();
	}

	protected void addAccessChecker(AccessChecker checker) {
		Checker.checkNull(checker, "checker");
		_checkers.add(checker);
	}

	private final void _process(HTTP_METHOD method, HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException {
		Request req = new RequestImpl(httpReq);
		Response resp = new ResponseImpl(httpResp);
		try {
			req.setCharacterEncoding(CHARSET);
			resp.setCharacterEncoding(CHARSET);
			// TODO this needs to be done via config
			resp.setContentType("application/json");
			// Check allowed origins:
			String origin = req.getOrigin();
			// TODO this must be an access checker
			List<String> allowedOrigins = Config.ref.getAllowedOrigins();
			if(allowedOrigins.contains(origin)) {
				// If it is an allowed origin, enable cross-site scripting:
				resp.setHeader("Access-Control-Allow-Origin", origin);
				resp.setHeader("Access-Control-Allow-Credentials", "true");
			}
			// Check access:
			for(AccessChecker checker : _checkers) {
				checker.checkAccess(req, resp);
			}
			// Process request:
			if(method == HTTP_METHOD.GET) {
				if(!_doGetOverridden) {
					throw new MethodNotAllowedException(method);
				}
				doGet(req, resp);
			}
			else if(method == HTTP_METHOD.POST) {
				if(!_doPostOverridden) {
					throw new MethodNotAllowedException(method);
				}
				doPost(req, resp);
			}
			else {
				throw new MethodNotAllowedException(method);
			}
		}
		catch(Exception e) {
			ErrorHandler.handleException(e, req, resp);
		}
	}

	private static final long serialVersionUID = 1L;
}
