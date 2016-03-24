// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.util;
import java.util.Map;
import java.util.HashMap;
import java.util.logging.Level;


public class Logger {

	// TYPE:
	private static Level _level = Level.INFO;
	private static Map<Class<?>, Logger> _cache = new HashMap<Class<?>, Logger>();

	public static Logger get(Class<?> c) {
		Checker.checkNull(c, "c");
		Logger l = _cache.get(c);
		if(l != null) {
			return l;
		}
		l = new Logger(c);
		_cache.put(c, l);
		return l;
	}
	
	public static void setLevel(Level level) {
		Checker.checkNull(level, "level");
		Checker.checkIllegalValue(level, "level", Level.OFF, Level.INFO, Level.WARNING, Level.SEVERE);
		// Set the level across already registered loggers:
		for(Logger l : _cache.values()) {
			l._logger.setLevel(level);
		}
		_level = level;
	}

	// INSTANCE:
	private final java.util.logging.Logger _logger;
	
	private Logger(Class<?> c) {
		_logger = java.util.logging.Logger.getLogger(c.getName());
		_logger.setLevel(_level);
	}
	
	public void info(String message, Object ... params) {
		_log(Level.INFO, null, message, params);
	}

	public void warning(String message, Object ... params) {
		_log(Level.WARNING, null, message, params);
	}

	public void warning(Throwable t, String message, Object ... params) {
		_log(Level.WARNING, t, message, params);
	}
	
	public void severe(String message, Object ... params) {
		_log(Level.SEVERE, null, message, params);
	}

	public void severe(Throwable t, String message, Object ... params) {
		_log(Level.SEVERE, t, message, params);
	}

	//System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
	private void _log(Level level, Throwable t, String message, Object[] params) {
		if(!_logger.isLoggable(level)) {
			return;
		}
		// Format message:
		message = Strings.expand(message, params);
		// Get the method name:
		String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
		// Log it:
		_logger.logp(level, _logger.getName(), methodName, message, t);
	}
}
