// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.rest;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;


public interface Response extends HttpServletResponse {

	public boolean hasCookie(String name, String path);
	public void removeCookie(String name, String path);
	public void setContentType(String contentType);
	public void setLastModified(long timestamp);
	public void setDisableCache();
	public void setEnableCache();
	public void sendError(StatusCode status, Json content, Object ... params) throws IOException;
	public void sendError(EndpointException e) throws IOException;
	public void sendOk(Json content) throws IOException;
	public void sendOk() throws IOException;
}
