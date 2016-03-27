// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.data;
import java.util.List;
import java.util.ArrayList;
import pro.outcome.util.IllegalUsageException;


public class Config extends Facade<ConfigValue> {

	// TYPE:
	public static final Config ref = new Config();
	public interface Properties {
		public static final String ENV = "env";
		public static final String CHARSET = "charset";
		public static final String SEND_HTTP_STATUS_CODES = "send-http-status-codes";
		public static final String ALLOWED_ORIGINS = "allowed-origins";
	}
	
	// INSTANCE:
	private Config() {
	}

	public Object getValue(String name) {
		ConfigValue value = getSingle(ConfigValue.model.name.toArg(name));
		if(value == null) {
			return null;
		}
		return value.getValue();
	}
	
	public void save(String name, Object value) {
		ConfigValue cv = getSingle(ConfigValue.model.name.toArg(name));
		if(cv == null) {
			cv = new ConfigValue(name, value);
			insert(cv);
		}
		else {
			cv.setValue(value);
		}		
	}
	
	public String getEnvironment() {
		String e = (String)getValue(Properties.ENV);
		if(e == null) {
			throw new IllegalUsageException("environment config property has not been set");
		}
		return e;
	}
	
	public String getCharset() {
		String charset = (String)getValue(Properties.CHARSET);
		return charset == null ? "UTF-8" : charset;
	}
	
	public boolean getSendHttpStatusCodes() {
		Boolean b = (Boolean)getValue(Properties.SEND_HTTP_STATUS_CODES);
		return b == null ? true : b;
	}
	
	public List<String> getAllowedOrigins() {
		@SuppressWarnings("unchecked")
		List<String> ao = (List<String>)getValue(Properties.ALLOWED_ORIGINS);
		return ao == null ? new ArrayList<String>(0) : ao;
	}
}
