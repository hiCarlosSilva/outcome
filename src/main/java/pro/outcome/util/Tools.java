// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.util;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;


// TODO methods in this class are not used anywhere
public class Tools {

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
		if(Arrays.indexOf(l, Locale.getAvailableLocales()) == -1) {
			throw new IllegalArgumentException("locale '"+language+"_"+country+"' is not supported");
		}
		return l;
	}

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
}