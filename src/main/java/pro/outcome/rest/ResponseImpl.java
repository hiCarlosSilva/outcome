// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.rest;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Set;
import java.util.HashSet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import pro.outcome.util.Checker;
import pro.outcome.util.Tools;


public class ResponseImpl extends HttpServletResponseWrapper implements Response {

	private final Set<String> _cookieNames;
	
	public ResponseImpl(HttpServletResponse response) {
		super(response);
		_cookieNames = new HashSet<String>();
	}

	public boolean hasCookie(String name, String path) {
		Checker.checkEmpty(name, "name");
		if(path != null) {
			Checker.checkEmpty(path, "path");
		}
		return _cookieNames.contains(_getCookieFQN(name, path));
	}

	public void addCookie(Cookie c) {
		Checker.checkNull(c, "c");
		if(hasCookie(c.getName(), c.getPath())) {
			throw new IllegalStateException("cookie '"+_getCookieFQN(c)+"' has already been added to the response");
		}
		_cookieNames.add(_getCookieFQN(c));
		super.addCookie(c);
	}

	public void removeCookie(String name, String path) {
		Checker.checkEmpty(name, "name");
		if(path != null) {
			Checker.checkEmpty(path, "path");
		}
		Cookie c = new Cookie(name, "deleted");
		c.setMaxAge(0);
		c.setPath(path);
		addCookie(c);
	}

	public void setContentType(String contentType) {
		Checker.checkEmpty(contentType, "contentType");
		if(contentType.startsWith("text")) {
			if(contentType.indexOf("charset") == -1) {
				contentType = contentType+"; charset="+Servlet.CHARSET;
			}
		}
		super.setContentType(contentType);
	}

	public void setLastModified(long timestamp) {
		setDateHeader("Last-Modified", timestamp);
	}

	public void setDisableCache() {
		// TODO can we move the code here?
		Tools.sendDisableCacheHeaders(this);
	}

	public void setEnableCache() {
		// TODO can we move the code here?
		Tools.sendEnableCacheHeaders(this);
	}

	public void sendError(StatusCode status, Json jContent, Object ... params) throws IOException {
		Checker.checkNull(status, "status");
		setContentType("application/json");
		setStatus(status.httpCode);
		Json jResponse = new Json();
		Json jHeader = jResponse.add("header");
		jHeader.add("status", status.code);
		if(status != StatusCodes.OK) {
			String message = status.expand(params);
			jHeader.add("message", message);
		}
		if(jContent != null) {
			jResponse.add("content", jContent);
		}
		PrintWriter out = getWriter();
		jResponse.print(out);
		out.close();
	}
	
	public void sendError(EndpointException e) throws IOException {
		Checker.checkNull(e, "e");
		setContentType("application/json");
		setStatus(e.getErrorCode().httpCode);
		Json jResponse = new Json();
		Json jHeader = jResponse.add("header");
		jHeader.add("status", e.getErrorCode().code);
		jHeader.add("message", e.getMessage());
		if(e.getContent() != null) {
			jResponse.add("content", e.getContent());
		}
		PrintWriter out = getWriter();
		jResponse.print(out);
		out.close();
	}

	public void sendOk(Json jContent) throws IOException {
		sendError(StatusCodes.OK, jContent);
	}

	public void sendOk() throws IOException {
		sendOk(null);
	}
	
	private String _getCookieFQN(Cookie c) {
		return _getCookieFQN(c.getName(), c.getPath());
	}
	
	private String _getCookieFQN(String name, String path) {
		if(path == null) {
			return name;
		}
		else {
			return path + "/" + name;
		}
	}	
}
