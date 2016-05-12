package pro.outcome.util;
import java.util.logging.Level;
import java.util.logging.LogRecord;


public class Shortcuts {

	public static String x(String s, Object ... params) {
		return Strings.expand(s, params);
	}
	
	public static void print(String s, Object ... params) {
		System.out.print(Strings.expand(s, params));
	}

	public static void println(String s, Object ... params) {
		System.out.println(Strings.expand(s, params));
	}
	
	public static LogRecord info(String message, Object ... params) {
		return new LogRecord(Level.INFO, x(message, params));
	}
	
	public static LogRecord warn(String message, Object ... params) {
		return new LogRecord(Level.WARNING, x(message, params));
	}
	
	public static LogRecord warn(Throwable t, String message, Object ... params) {
		LogRecord record = new LogRecord(Level.WARNING, x(message, params));
		record.setThrown(t);
		return record;
	}

	public static LogRecord severe(String message, Object ... params) {
		return new LogRecord(Level.SEVERE, x(message, params));
	}
	
	public static LogRecord severe(Throwable t, String message, Object ... params) {
		LogRecord record = new LogRecord(Level.SEVERE, x(message, params));
		record.setThrown(t);
		return record;
	}
	
}
