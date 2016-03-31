// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.util;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;
import java.util.Calendar;
import javax.servlet.http.HttpServletResponse;


public class Tools {

	public static void handleCriticalError(String message, Throwable t) {
		// TODO this must attempt to log in the error log as well.
		System.err.println("[CRITICAL ERROR]: "+(Strings.isEmpty(message) ? "" : message));
		if(t != null) {
			t.printStackTrace(System.err);
		}
		else {
			System.err.println("(null exception)");
		}
	}

	public static void handleCriticalError(Throwable t) {
		handleCriticalError(null, t);
	}
	
	public static boolean contains(Collection<?> c, Object o, Comparator<Object> comparator) {
		Checker.checkNull(c);
		Checker.checkNull(comparator);
		for(Object element : c) {
			if(comparator.compare(element, o) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public static Locale getLocale(String locale) {
		Checker.checkEmpty(locale);
		String[] array = locale.split("[_]");
		if(array.length != 2) {
			throw new IllegalArgumentException("wrong format for locale: "+locale);
		}
		return getLocale(array[0], array[1]);
	}

	public static Locale getLocale(String language, String country) {
		Checker.checkEmpty(language);
		Checker.checkEmpty(country);
		Locale l = new Locale(language, country);
		if(Arrays.find(l, Locale.getAvailableLocales()) == -1) {
			throw new IllegalArgumentException("locale '"+language+"_"+country+"' is not supported");
		}
		return l;
	}

	// TODO document
	public static byte[] parseInetAddress(String address) {
		Checker.checkEmpty(address);
		String[] array = address.split("\\.");
		if(array.length != 4) {
			throw new IllegalArgumentException("illegal IP address: "+address);
		}
		byte[] result = new byte[array.length];
		for(int i=0; i<array.length; i++) {
			try {
				Short s = new Short(array[i]);
				if(s > 255) {
					throw new IllegalArgumentException("illegal IP address: "+address+" (wrong segment '"+array[i]+"')");
				}
				result[i] = s.byteValue();
			}
			catch(NumberFormatException nfe) {
				throw new IllegalArgumentException("illegal IP address: "+address+" (wrong segment '"+array[i]+"')");
			}
		}
		return result;
	}

	// TODO move to REST package
	public static void sendDisableCacheHeaders(HttpServletResponse response) {
		response.setHeader("Expires", "Mon, 26 Jul 1997 05:00:00 GMT"); // Date in the past
		response.setDateHeader("Last-Modified", Calendar.getInstance().getTimeInMillis()); // always modified
		response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0");
		response.addHeader("Cache-Control", "post-check=0, pre-check=0");
		// Supposedly, the Pragma header is a request header, not a response header (ie, only HTTP clients
		// should be using it). However, IE seems to use it to avoid caching, so we will keep sending it.
		response.addHeader("Pragma", "no-cache");
	}

	// TODO move to REST package
	public static void sendEnableCacheHeaders(HttpServletResponse response) {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, c.get(Calendar.YEAR)+1);
		response.setDateHeader("Expires", c.getTimeInMillis()); // Date in the future
		response.setHeader("Last-Modified", "Mon, 26 Jul 1997 05:00:00 GMT"); // Date in the past
	}
}