// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.rest;
import java.net.URLDecoder;
import java.io.UnsupportedEncodingException;
import javax.servlet.http.HttpServletRequestWrapper;

import pro.outcome.util.Checker;
import pro.outcome.util.IntegrityException;
import pro.outcome.util.Strings;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;


public class RequestImpl extends HttpServletRequestWrapper implements Request {

	public RequestImpl(HttpServletRequest request) {
		super(request);
	}

	public Cookie getCookie(String name) {
		Checker.checkEmpty(name, "name");
		Cookie[] cookies = getCookies();
		if(cookies == null) {
			return null;
		}
		for(int i=0; i<cookies.length; i++) {
			if(cookies[i].getName().equals(name)) {
				return cookies[i];					
			}
		}
		return null;
	}

	public String getOrigin() {
		String origin = getHeader("origin");
		if(origin == null) {
			origin = getHeader("host");
		}
		if(origin.startsWith("http://")) {
			return origin;
		}
		else {
			return "http://"+origin;
		}
	}

	public String getServerPath() {
		StringBuilder sb = new StringBuilder(getScheme());
		sb.append("://");
		sb.append(getServerName());
		int port = getServerPort();
		if(port != 80) {
			sb.append(":");			
			sb.append(port);			
		}
		return sb.toString();
	}

	public String getRequestPath() {
		return getRequestURI().substring(getContextPath().length()+1);
	}

	public String getApplicationName() {
		String s = getContextPath();
		if(Strings.isEmpty(s)) {
			return s;
		}
		else {
			return getContextPath().substring(1);
		}
	}

	public String getQueryString(boolean decode) {
		if(decode) {
			String qs = super.getQueryString();
			if(qs == null) {
				return null;
			}
			else {
				try {
					return URLDecoder.decode(qs, Config.CHARSET);
				}
				catch(UnsupportedEncodingException uee) {
					throw new IntegrityException(uee);
				}
			}
		}
		else {
			return super.getQueryString();
		}
	}

	public String getCompleteRequestURL(boolean decode) {
		String query = getQueryString(decode);
		return getRequestURL().append(query==null?"":"?"+query).toString();
	}

	public String getCompleteRequestURL() {
		return getCompleteRequestURL(false);
	}

	public String[] breakRequestPath() {
		return getRequestURI().substring(1).split("\\/");
	}

	public String getParameter(String name, boolean required) {
		Checker.checkEmpty(name, "name");
		String param = super.getParameter(name);
		param = Strings.isEmpty(param) ? null : param;
		if(required && param==null) {
			throw new ParameterValidationException(name);
		}
		return param;
	}
	
	public String getParameter(String name) {
		return getParameter(name, false);
	}

	public Integer getIntegerParameter(String name, boolean required) {
		String param = getParameter(name, required);
		return param == null ? null : Parameter.toInteger(name, param);
	}

	public Integer getIntegerParameter(String name) {
		return getIntegerParameter(name, false);
	}

	public Long getLongParameter(String name, boolean required) {
		String param = getParameter(name, required);
		return param == null ? null : Parameter.toLong(name, param);
	}

	public Long getLongParameter(String name) {
		return getLongParameter(name, false);
	}

	public Float getFloatParameter(String name, boolean required) {
		String param = getParameter(name, required);
		return param == null ? null : Parameter.toFloat(name, param);
	}

	public Float getFloatParameter(String name) {
		return getFloatParameter(name, false);
	}

	public Double getDoubleParameter(String name, boolean required) {
		String param = getParameter(name, required);
		return param == null ? null : Parameter.toDouble(name, param);
	}

	public Double getDoubleParameter(String name) {
		return getDoubleParameter(name, false);
	}
}
