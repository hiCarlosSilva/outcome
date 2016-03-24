// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.rest;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import pro.outcome.rest.Request.HTTP_METHOD;
import pro.outcome.util.Checker;
import pro.outcome.util.IntegrityException;
import pro.outcome.util.Reflection;
import pro.outcome.util.Strings;


public abstract class Servlet extends HttpServlet {

	private final List<AccessChecker> _checkers;
	private final boolean _doGetOverridden;
	private final boolean _doPostOverridden;
	
	protected Servlet(AccessChecker[] checkers) {
		super();
		if(checkers == null) {
			_checkers = new ArrayList<AccessChecker>(0);
		}
		else {
			Checker.checkNullElements(checkers, "checkers");
			_checkers = Arrays.asList(checkers);
		}
		_doGetOverridden = Reflection.getDeclaredMethod(true, getClass(), "doGet", Request.class, Response.class) != null;
		_doPostOverridden = Reflection.getDeclaredMethod(true, getClass(), "doPost", Request.class, Response.class) != null;
	}
	
	// INSTANCE:
	public final void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_process(HTTP_METHOD.GET, req, resp);
	}

	public final void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_process(HTTP_METHOD.POST, req, resp);
	}

	public final void doHead(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_process(HTTP_METHOD.HEAD, req, resp);
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

	private final void _process(HTTP_METHOD method, HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException {
		Request req = new RequestImpl(httpReq);
		Response resp = new ResponseImpl(httpResp);
		try {
			req.setCharacterEncoding(Config.CHARSET);
			resp.setCharacterEncoding(Config.CHARSET);
			resp.setContentType("application/json");
			// Check allowed origins:
			String origin = req.getOrigin();
			// TODO this must be an access checker
			if(Strings.find(origin, Config.ALLOWED_ORIGINS) != -1) {
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
