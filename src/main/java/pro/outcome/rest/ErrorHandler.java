// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.rest;
import java.io.PrintWriter;

import pro.outcome.util.Logger;


class ErrorHandler {

	private static final String _MSG_ERROR = "[Critical] Caught unexpected error on exception handling section:";
	private static final String _MSG_ORIGINAL = "[Critical] Original application error was:";
	private static final String _MSG_NO_MSG = "[Critical] There was no error reported by the application before the critical error";
	private static final String _MSG_SECOND = "[Critical] Caught second unexpected error on error-handler servlet";
	private static final Logger _logger = Logger.get(ErrorHandler.class);
	
	public static void handleException(Exception e, Request req, Response resp) {
		try {
			resp.setDisableCache();
			if(e instanceof EndpointException) {
				EndpointException ee = (EndpointException)e;
				// Expected exception, log as INFO:
				_logger.info(ee.getMessage());
				// Send response:
				resp.sendError(ee);
			}
			else {
				// Unexpected exception, we need to log it:
				// TODO we need to translate the error message to something intelligible
				// TODO move logging into ResponseImpl, we are repeating "expand"
				String message = StatusCodes.UNEXPECTED.expand(e.getMessage());
				_logger.severe(e, message);
				// Send response:
				resp.sendError(StatusCodes.UNEXPECTED, null, e.getMessage());
			}
		}
		catch(Throwable t) {
			handleCriticalException(resp, t, e);
		}
	}
	
	public static void handleCriticalException(Response response, Throwable t1, Throwable t0) {
		// Log the error first:
		_logger.severe(t1, "critical exception");
		if(t0 != null) {
			_logger.severe(t0, _MSG_ORIGINAL);
		}
		else {
			_logger.severe(_MSG_NO_MSG);
		}
		// Try showing the new error as simple text:
		// (this may not work if the response has already been committed)
		try {
			response.setContentType("text/plain");
			PrintWriter out = response.getWriter();
			out.println(_MSG_ERROR);
			t1.printStackTrace(out);
			if(t0 != null) {
				out.println(_MSG_ORIGINAL);
				t0.printStackTrace(out);
			}
			else {
				out.println(_MSG_NO_MSG);
			}
			out.close();
		}
		catch(Throwable t2) {
			// Unrecoverable:
			_logger.severe(t2, _MSG_SECOND);
		}
	}
}
