// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.rest;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;


public interface Request extends HttpServletRequest {

	public static enum HttpMethod {GET, POST, PUT, DELETE, HEAD, OPTIONS, TRACE};

	// TODO make a note in the documentation that cookies sent by the browser only contain name=value
	// in the header, since the browser only returns cookies visible to the requesting server resource.
	// So a method getCookie(name, path) does not make sense for example.
	public Cookie getCookie(String name);

	public String		getParameter(String name);
	public String		getParameter(String name, boolean required);
	public Integer		getIntegerParameter(String name);
	public Integer		getIntegerParameter(String name, boolean required);
	public Long			getLongParameter(String name);
	public Long			getLongParameter(String name, boolean required);
	public Float		getFloatParameter(String name);
	public Float		getFloatParameter(String name, boolean required);
	public Double		getDoubleParameter(String name);
	public Double		getDoubleParameter(String name, boolean required);
	public JsonObject	readDataAsJson() throws IOException;
	
	public String getOrigin();
	public String getServerPath();
	public String getRequestPath();
	public String getApplicationName();
	public String getQueryString(boolean decode);
	public String getCompleteRequestURL();
	public String getCompleteRequestURL(boolean decode);
	public String[] breakRequestPath();
}
