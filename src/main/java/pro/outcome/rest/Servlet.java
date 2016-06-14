// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.rest;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;
import pro.outcome.rest.Request.HttpMethod;
import pro.outcome.data.ConfigWrapper;
import pro.outcome.util.Checker;
import pro.outcome.util.IntegrityException;
import pro.outcome.util.Reflection;
import static pro.outcome.util.Shortcuts.*;


public abstract class Servlet extends HttpServlet {

	// TYPE:
	static final String CHARSET = "UTF-8";
	private static final String _TEMPLATES_CFG_ATTR = "ftl-cfg";

	// INSTANCE:
	private final List<Processor> _pre;
	private final List<Processor> _post;
	private final Logger _logger;
	private final boolean _doGetOverridden;
	private final boolean _doPostOverridden;
	private Configuration _cfg;
	
	protected Servlet() {
		super();
		_pre = new ArrayList<Processor>();
		_post = new ArrayList<Processor>();
		_logger = Logger.getLogger(getClass().getName());
		_doGetOverridden = Reflection.getDeclaredMethod(true, getClass(), "doGet", Request.class, Response.class) != null;
		_doPostOverridden = Reflection.getDeclaredMethod(true, getClass(), "doPost", Request.class, Response.class) != null;
		_cfg = null;
	}

	protected abstract String getExpectedContentType();

	public final void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_process(HttpMethod.GET, req, resp);
	}

	public final void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_process(HttpMethod.POST, req, resp);
	}

	public final void doTrace(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_process(HttpMethod.TRACE, req, resp);
	}
	
	public final void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_process(HttpMethod.PUT, req, resp);
	}

	public final void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		_process(HttpMethod.DELETE, req, resp);
	}

	public final void doOptions(HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException, ServletException {
		Request req = new RequestImpl(httpReq);
		Response resp = new ResponseImpl(httpResp, getExpectedContentType());
		super.doOptions(req, resp);
		StringBuilder allowedMethods = new StringBuilder();
		allowedMethods.append("OPTIONS, HEAD");
		if(_doGetOverridden) {
			allowedMethods.append(", ").append(HttpMethod.GET);
		}
		if(_doPostOverridden) {
			allowedMethods.append(", ").append(HttpMethod.POST);
		}
		resp.setHeader("Allow", allowedMethods.toString());
		String contentType = getExpectedContentType();
		if(contentType != null) {
			resp.setContentType(contentType);
		}
		_checkAllowedOrigins(req, resp);
	}

	// Note: this method stub is included so that subclasses don't have to override it.
	protected void doGet(Request req, Response resp) throws Exception {
		// Code should never get here:
		throw new IntegrityException();
	}

	// Note: this method stub is included so that subclasses don't have to override it.
	protected void doPost(Request req, Response resp) throws Exception {
		// Code should never get here:
		throw new IntegrityException();
	}

	protected Logger getLogger() {
		return _logger;
	}

	protected void addPreProcessor(Processor pre) {
		Checker.checkNull(pre);
		_pre.add(pre);
	}

	protected void addPostProcessor(Processor post) {
		Checker.checkNull(post);
		_post.add(post);
	}
	
	protected Template getTemplate(String path) throws IOException {
		Checker.checkEmpty(path);
		if(_cfg == null) {
			ServletContext ctx = getServletContext();
			synchronized(ctx) {
				_cfg = (Configuration)ctx.getAttribute(_TEMPLATES_CFG_ATTR);
				if(_cfg == null) {
					_cfg = new Configuration();
					_cfg.setServletContextForTemplateLoading(getServletContext(), "/WEB-INF/templates");
					_cfg.setDefaultEncoding(CHARSET);
					if(ConfigWrapper.getEnvironment().equals("live")) {
						_cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
					}
					else {
						_cfg.setTemplateExceptionHandler(TemplateExceptionHandler.HTML_DEBUG_HANDLER);
					}
					ctx.setAttribute(_TEMPLATES_CFG_ATTR, _cfg);
				}
			}
		}
		return _cfg.getTemplate(path);
	}

	private void _checkAllowedOrigins(Request req, Response resp) throws IOException {
		String origin = req.getOrigin();
		if(origin == null) {
			getLogger().log(info("received null Origin header"));
			return;
		}
		List<String> allowedOrigins = ConfigWrapper.getAllowedOrigins();
		if(allowedOrigins.contains(origin)) {
			// Enable Cross-Origin Resource Sharing (see link below for details)
			// http://www.html5rocks.com/en/tutorials/cors/
			resp.setHeader("Access-Control-Allow-Origin", origin);
			resp.setHeader("Access-Control-Allow-Credentials", "true");
			resp.setHeader("Access-Control-Expose-Headers", "Set-Cookie");
		}
		else {
			getLogger().log(info("origin '{}' is not allowed", origin));
		}
	}

	private void _process(HttpMethod method, HttpServletRequest httpReq, HttpServletResponse httpResp) throws IOException {
		Request req = new RequestImpl(httpReq);
		Response resp = new ResponseImpl(httpResp, getExpectedContentType());
		getLogger().info(x("servlet '{}' received a {} request", getClass().getSimpleName(), method));
		try {
			req.setCharacterEncoding(CHARSET);
			resp.setCharacterEncoding(CHARSET);
			_checkAllowedOrigins(req, resp);
			// Preprocessors:
			for(Processor p : _pre) {
				p.process(req, resp);
			}
			// Process request:
			if(method == HttpMethod.GET) {
				if(!_doGetOverridden) {
					throw new MethodNotAllowedException(method);
				}
				doGet(req, resp);
			}
			else if(method == HttpMethod.POST) {
				if(!_doPostOverridden) {
					throw new MethodNotAllowedException(method);
				}
				doPost(req, resp);
			}
			else {
				throw new MethodNotAllowedException(method);
			}
			// Post-processors:
			for(Processor p : _post) {
				p.process(req, resp);
			}
		}
		catch(Exception e) {
			ErrorHandler.handleException(e, req, resp);
		}
		getLogger().info(x("servlet '{}' completed a {} request", getClass().getSimpleName(), method));
	}

	private static final long serialVersionUID = 1L;
}
