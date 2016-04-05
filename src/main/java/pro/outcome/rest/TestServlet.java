// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on hiCarlosSilva@gmail.com.
package pro.outcome.rest;
import pro.outcome.util.IllegalUsageException;


public abstract class TestServlet extends Servlet {

	// TYPE:
	public static final String LIVE_SERVER_NAME = "api.connector.im";

	// INSTANCE:
	protected TestServlet() {
		addPreProcessor(new Processor() {
			public void process(Request req, Response resp) {
				if(req.getServerName().equals(LIVE_SERVER_NAME)) {
					throw new IllegalUsageException("cannot use test servlets in the Live environment");
				}
			}
		});
	}
	
	private static final long serialVersionUID = 1L;
}
