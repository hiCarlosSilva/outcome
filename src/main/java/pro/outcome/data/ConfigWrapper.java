package pro.outcome.data;
import java.util.ArrayList;
import java.util.List;


public class ConfigWrapper {
	
	// TYPE:
	public static final String BASE_URL = "base-url";
	public static final String ENV = "env";
	public static final String ALLOWED_ORIGINS = "allowed-origins";

	public static String getBaseUrl() {
		return (String)getConfig().getValue(BASE_URL, true);
	}

	public static void setBaseUrl(String baseUrl) {
		getConfig().save(new ConfigValue(BASE_URL, baseUrl));
	}

	// TODO enum
	public static String getEnvironment() {
		return (String)getConfig().getValue(ENV, true);
	}

	// TODO enum
	public static void setEnvironment(String env) {
		getConfig().save(new ConfigValue(ENV, env));
	}

	// TODO change this method
	public static List<String> getAllowedOrigins() {
		Object tmp = getConfig().getValue(ALLOWED_ORIGINS);
		if(tmp instanceof String) {
			List<String> list = new ArrayList<>();
			list.add((String)tmp);
			return list;
		}
		else {
			@SuppressWarnings("unchecked")
			List<String> ao = (List<String>)getConfig().getValue(ALLOWED_ORIGINS);
			return ao == null ? new ArrayList<String>(0) : ao;
		}		
	}

	public static void setAllowedOrigins(String server) {
		if(server == null) {
			_delete(ALLOWED_ORIGINS);
		}
		else {
			getConfig().save(new ConfigValue(ALLOWED_ORIGINS, server));
		}
	}

	public static void setAllowedOrigins(List<String> servers) {
		if(servers == null || servers.isEmpty()) {
			_delete(ALLOWED_ORIGINS);
		}
		else {
			getConfig().save(new ConfigValue(ALLOWED_ORIGINS, servers));
		}
	}

	protected static Config getConfig() {
		return Entities.config;
	}

	private static void _delete(String propName) {
		ConfigValue value = getConfig().findSingle(Entities.config.name.toArg(propName));
		if(value != null) {
			getConfig().delete(value);
		}
	}

	// INSTANCE:
	protected ConfigWrapper() {
	}
}
