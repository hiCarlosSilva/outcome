// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.rest;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Set;
import java.util.Calendar;
import java.util.HashSet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import pro.outcome.util.Checker;
import pro.outcome.util.IntegrityException;
import freemarker.template.Template;
import freemarker.template.TemplateException;


public class ResponseImpl extends HttpServletResponseWrapper implements Response {

	private final Set<String> _cookieNames;
	private boolean _contentTypeSet;
	private boolean _lenient;
	
	public ResponseImpl(HttpServletResponse response) {
		super(response);
		_cookieNames = new HashSet<String>();
		_contentTypeSet = false;
		_lenient = false;
	}

	public boolean hasCookie(String name, String path) {
		Checker.checkEmpty(name);
		if(path != null) {
			Checker.checkEmpty(path);
		}
		return _cookieNames.contains(_getCookieFQN(name, path));
	}

	public void addCookie(Cookie c) {
		Checker.checkNull(c);
		if(hasCookie(c.getName(), c.getPath())) {
			throw new IllegalStateException("cookie '"+_getCookieFQN(c)+"' has already been added to the response");
		}
		_cookieNames.add(_getCookieFQN(c));
		super.addCookie(c);
	}

	public void removeCookie(String name, String path) {
		Checker.checkEmpty(name);
		if(path != null) {
			Checker.checkEmpty(path);
		}
		Cookie c = new Cookie(name, "deleted");
		c.setMaxAge(0);
		c.setPath(path);
		addCookie(c);
	}

	public void setContentType(String contentType) {
		Checker.checkEmpty(contentType);
		if(_contentTypeSet && !_lenient) {
			throw new IllegalStateException("content type has already been set");
		}
		if(contentType.startsWith("text")) {
			if(contentType.indexOf("charset") == -1) {
				contentType = contentType+"; charset="+Servlet.CHARSET;
			}
		}
		super.setContentType(contentType);
		_contentTypeSet = true;
	}

	public void setLastModified(long timestamp) {
		setDateHeader("Last-Modified", timestamp);
	}

	public void setDisableCache() {
		setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT"); // Date in the past
		setDateHeader("Last-Modified", Calendar.getInstance().getTimeInMillis()); // always modified
		setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Supposedly, the Pragma header is a request header, not a response header (i.e., only HTTP clients
		// should be using it). However, IE seems to use it to avoid caching, so we will keep sending it.
		addHeader("Pragma", "no-cache");
	}

	public void setEnableCache() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, c.get(Calendar.YEAR)+1);
		setDateHeader("Expires", c.getTimeInMillis()); // Date in the future
		setHeader("Last-Modified", "Mon, 26 Jul 1997 05:00:00 GMT"); // Date in the past
	}

	public void sendError(StatusCode status, Json jContent, Object ... params) throws IOException {
		Checker.checkNull(status);
		setContentType(MimeTypes.JSON);
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
	
	public void sendError(StatusCode status) throws IOException {
		if(status == StatusCodes.OK) {
			throw new IllegalArgumentException("incorrect status code (cannot be OK)");
		}
		sendError(status, null);
	}

	public void sendError(EndpointException e) throws IOException {
		Checker.checkNull(e);
		setContentType(MimeTypes.JSON);
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
	
	public void sendTemplate(Template ftl, Object data) throws IOException {
		if(!_contentTypeSet) {
			throw new IllegalStateException("content type has not been set yet");
		}
		try {
			ftl.process(data, getWriter());
		}
		catch(TemplateException te) {
			throw new IntegrityException(te);
		}
	}

	public void setLenient(boolean lenient) {
		_lenient = lenient;
	}

	private String _getCookieFQN(Cookie c) {
		return _getCookieFQN(c.getName(), c.getPath());
	}
	
	private String _getCookieFQN(String name, String path) {
		if(path == null) {
			return name;
		}
		else {
			return path + name;
		}
	}	
}
