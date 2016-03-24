// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.util;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



public class Checker {

	public static Object checkNull(Object arg, String varName) {
		if(arg == null) {
			throw new IllegalArgumentException(varName+": null argument");
		}
		return arg;
	}
	
	public static String checkEmpty(String arg, String varName) {
		checkNull(arg, varName);
		if(arg.equals(Constants.EMPTY)) {
			throw new IllegalArgumentException(varName+": empty argument");
		}
		return arg;
	}
	
	public static Object[] checkEmpty(Object[] arg, String varName) {
		checkNull(arg, varName);
		if(arg.length == 0) {
			throw new IllegalArgumentException(varName+": empty argument");
		}
		return arg;
	}

	public static char[] checkEmpty(char[] arg, String varName) {
		checkNull(arg, varName);
		if(arg.length == 0) {
			throw new IllegalArgumentException(varName+": empty argument");
		}
		return arg;
	}

	public static byte[] checkEmpty(byte[] arg, String varName) {
		checkNull(arg, varName);
		if(arg.length == 0) {
			throw new IllegalArgumentException(varName+": empty argument");
		}
		return arg;
	}
	
	public static short[] checkEmpty(short[] arg, String varName) {
		checkNull(arg, varName);
		if(arg.length == 0) {
			throw new IllegalArgumentException(varName+": empty argument");
		}
		return arg;
	}

	public static int[] checkEmpty(int[] arg, String varName) {
		checkNull(arg, varName);
		if(arg.length == 0) {
			throw new IllegalArgumentException(varName+": empty argument");
		}
		return arg;
	}

	public static long[] checkEmpty(long[] arg, String varName) {
		checkNull(arg, varName);
		if(arg.length == 0) {
			throw new IllegalArgumentException(varName+": empty argument");
		}
		return arg;
	}

	public static float[] checkEmpty(float[] arg, String varName) {
		checkNull(arg, varName);
		if(arg.length == 0) {
			throw new IllegalArgumentException(varName+": empty argument");
		}
		return arg;
	}

	public static double[] checkEmpty(double[] arg, String varName) {
		checkNull(arg, varName);
		if(arg.length == 0) {
			throw new IllegalArgumentException(varName+": empty argument");
		}
		return arg;
	}

	public static Collection<?> checkEmpty(Collection<?> arg, String varName) {
		checkNull(arg, varName);
		if(arg.isEmpty()) {
			throw new IllegalArgumentException(varName+": empty argument");
		}
		return arg;
	}

	public static Map<?,?> checkEmpty(Map<?,?> arg, String varName) {
		checkNull(arg, varName);
		if(arg.isEmpty()) {
			throw new IllegalArgumentException(varName+": empty argument");
		}
		return arg;
	}

	public static Object[] checkNullElements(Object[] array, String varName) {
		checkNull(array, "array");
		for(int i=0; i<array.length; i++) {
			if(array[i] == null) {
				throw new IllegalArgumentException(varName+"["+i+"] is null");
			}
		}
		return array;
	}

	public static String[] checkEmptyElements(String[] array, String varName) {
		checkNull(array, "array");
		for(int i=0; i<array.length; i++) {
			if(Strings.isEmpty(array[i])) {
				throw new IllegalArgumentException(varName+"["+i+"] is "+(array[i]==null ? "null" : "empty"));
			}
		}
		return array;
	}

	public static Object[] checkLength(Object[] array, int length, String varName) {
		checkNull(array, varName);
		if(array.length != length) {
			throw new IllegalArgumentException(varName+": illegal length "+array.length+" (expected "+length+")");
		}
		else {
			return array;
		}
	}

	public static Object[] checkMatchingLength(Object[] array1, String array1Name, Object[] array2, String array2Name) {
		checkNull(array1, array1Name);
		checkNull(array2, array2Name);
		if(array1.length != array2.length) {
			throw new IllegalArgumentException(array1Name+": length does't match "+array2Name+"'s length");
		}
		else {
			return array1;
		}
	}

	public static int checkIndex(int index, String varName) {
		if(index < 0) {
			throw new IllegalArgumentException(varName+": negative index");
		}
		return index;
	}
	
	public static long checkMaxValue(long value, long max, String varName) {
		if(value > max) {
			throw new IllegalArgumentException(varName+": illegal value "+value);
		}
		return value;
	}

	public static long checkMinValue(long value, long min, String varName) {
		if(value < min) {
			throw new IllegalArgumentException(varName+": illegal value "+value);
		}
		return value;
	}

	public static long checkIllegalValue(long value, long illegal, String varName) {
		if(value == illegal) {
			throw new IllegalArgumentException(varName+": illegal value "+value);
		}
		return value;
	}

	public static Object checkIllegalValue(Object value, String varName, Object ... allowed) {
		for(Object o : allowed) {
			if(value.equals(o)) {
				return value;
			}
		}
		throw new IllegalArgumentException(varName+": illegal value "+value);
	}

	// TODO share implementation with EAP BL validators
	private final static Pattern _iPattern  = Pattern.compile("[a-zA-Z0-9_]*");
	private final static Pattern _tiPattern = Pattern.compile("[a-zA-Z0-9_-]*");
	private final static Pattern _iPatternDot  = Pattern.compile("([.]|[a-zA-Z0-9_])*");
	private final static Pattern _tiPatternDot = Pattern.compile("([.]|[a-zA-Z0-9_-])*");

	public static String checkCodeIdentifier(String name, String varName, boolean acceptDot) {
		checkEmpty(name, varName);
		if(acceptDot) {
			if(name.contains("..")) {
				throw new IllegalArgumentException(varName+": identifier uses dot characters in sequence");
			}
			_checkIdentifier(name, varName, _iPatternDot.matcher(name));
		}
		else {
			_checkIdentifier(name, varName, _iPattern.matcher(name));
		}
		return name;
	}
	
	public static String checkCodeIdentifier(String name, String varName) {
		return checkCodeIdentifier(name, varName, false);
	}
	
	public static String checkTextIdentifier(String name, String varName, boolean acceptDot) {
		checkEmpty(name, varName);
		// Check first letter:
		if(Character.isDigit(name.charAt(0))) {
			throw new IllegalArgumentException(varName+": identifier must start with a letter or _ and starts with '"+name.charAt(0)+"'");
		}
		if(acceptDot) {
			if(name.contains("-.") || name.contains(".-") || name.contains("--") || name.contains("..")) {
				throw new IllegalArgumentException(varName+": identifier uses dot and - characters in sequence");
			}
			_checkIdentifier(name, varName, _tiPatternDot.matcher(name));
		}
		else {
			_checkIdentifier(name, varName, _tiPattern.matcher(name));
		}
		return name;
	}

	public static String checkTextIdentifier(String name, String varName) {
		return checkTextIdentifier(name, varName, false);
	}

	private static void _checkIdentifier(String name, String varName, Matcher m) {
		// Check first letter:
		if(Character.isDigit(name.charAt(0))) {
			throw new IllegalArgumentException(varName+": identifier must start with a letter or _ and starts with '"+name.charAt(0)+"'");
		}
		if(!m.matches()) {
			// show the illegal characters found:
			String illegalChars = m.replaceAll("");
			illegalChars = Strings.eliminateRepeatedChars(illegalChars);
			throw new IllegalArgumentException(varName+": identifier has illegal characters '"+illegalChars+"'");
		}
	}

}
