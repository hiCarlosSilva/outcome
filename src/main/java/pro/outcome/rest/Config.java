// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.rest;

public class Config {

	public static final String CHARSET = "UTF-8";
	public static final boolean SEND_HTTP_STATUS_CODES = true;
	// TODO test this on the web
	public static final String[] ALLOWED_ORIGINS = {"http://localhost:8080", "http://localhost:3000", "http://zippy-haiku-622.appspot.com"};
}
