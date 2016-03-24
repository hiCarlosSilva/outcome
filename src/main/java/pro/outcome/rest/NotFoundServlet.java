// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.rest;
import java.io.IOException;

import pro.outcome.rest.Request;
import pro.outcome.rest.Response;
import pro.outcome.rest.Servlet;


public class NotFoundServlet extends Servlet {

	public NotFoundServlet() {
		super(null);
	}

	public void doGet(Request req, Response resp) throws IOException {
		String uri = (String)req.getAttribute("javax.servlet.error.request_uri");
		resp.sendError(StatusCodes.NOT_FOUND, null, uri);
	}

	private static final long serialVersionUID = 1L;
}
