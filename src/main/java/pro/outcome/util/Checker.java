// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.util;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.regex.Matcher;



public class Checker {

	public static Object checkNull(Object arg) {
		if(arg == null) {
			throw new IllegalArgumentException("null argument");
		}
		return arg;
	}
	
	public static String checkEmpty(String arg) {
		checkNull(arg);
		if(arg.equals(Strings.EMPTY)) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}
	
	public static Object[] checkEmpty(Object[] arg) {
		checkNull(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static char[] checkEmpty(char[] arg) {
		checkNull(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static byte[] checkEmpty(byte[] arg) {
		checkNull(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}
	
	public static short[] checkEmpty(short[] arg) {
		checkNull(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static int[] checkEmpty(int[] arg) {
		checkNull(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static long[] checkEmpty(long[] arg) {
		checkNull(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static float[] checkEmpty(float[] arg) {
		checkNull(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static double[] checkEmpty(double[] arg) {
		checkNull(arg);
		if(arg.length == 0) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static Collection<?> checkEmpty(Collection<?> arg) {
		checkNull(arg);
		if(arg.isEmpty()) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static Map<?,?> checkEmpty(Map<?,?> arg) {
		checkNull(arg);
		if(arg.isEmpty()) {
			throw new IllegalArgumentException("empty argument");
		}
		return arg;
	}

	public static Object[] checkNullElements(Object[] array) {
		checkNull(array);
		for(int i=0; i<array.length; i++) {
			if(array[i] == null) {
				throw new IllegalArgumentException("position "+i+" is null");
			}
		}
		return array;
	}

	public static String[] checkEmptyElements(String[] array) {
		checkNull(array);
		for(int i=0; i<array.length; i++) {
			if(Strings.isEmpty(array[i])) {
				throw new IllegalArgumentException(Strings.expand("position {} is {}", i, (array[i]==null ? "null" : "empty")));
			}
		}
		return array;
	}

	public static Object[] checkDuplicateElements(Object[] array) {
		checkNull(array);
		Map<Object,Integer> map = new HashMap<>();
		for(int i=0; i<array.length; i++) {
			Integer pos = map.get(array[i]);			
			if(pos != null) {
				throw new IllegalArgumentException(Strings.expand("positions {} and {} are duplicate", pos, i));
			}
			map.put(array[i], i);
		}
		return array;
	}

	public static Object[] checkLength(Object[] array, int length) {
		checkNull(array);
		if(array.length != length) {
			throw new IllegalArgumentException(Strings.expand("illegal length {} (expected {})", array.length, length));
		}
		else {
			return array;
		}
	}

	public static Object[] checkMatchingLength(Object[] array1, Object[] array2) {
		checkNull(array1);
		checkNull(array2);
		if(array1.length != array2.length) {
			throw new IllegalArgumentException(Strings.expand("array 1 has {} elements, array 2 has {}", array1.length, array2.length));
		}
		else {
			return array1;
		}
	}

	public static int checkIndex(int index) {
		if(index < 0) {
			throw new IllegalArgumentException(Strings.expand("negative index: {}", index));
		}
		return index;
	}
	
	public static long checkMaxValue(long value, long max) {
		if(value > max) {
			throw new IllegalArgumentException("illegal value "+value);
		}
		return value;
	}

	public static long checkMinValue(long value, long min) {
		if(value < min) {
			throw new IllegalArgumentException("illegal value "+value);
		}
		return value;
	}

	public static long checkIllegalValue(long value, long illegal) {
		if(value == illegal) {
			throw new IllegalArgumentException("illegal value "+value);
		}
		return value;
	}

	public static Object checkIllegalValue(Object value, Object ... allowed) {
		for(Object o : allowed) {
			if(value.equals(o)) {
				return value;
			}
		}
		throw new IllegalArgumentException("illegal value "+value);
	}

	// TODO share implementation with EAP BL validators
	private final static Pattern _iPattern  = Pattern.compile("[a-zA-Z0-9_]*");
	private final static Pattern _tiPattern = Pattern.compile("[a-zA-Z0-9_-]*");
	private final static Pattern _iPatternDot  = Pattern.compile("([.]|[a-zA-Z0-9_])*");
	private final static Pattern _tiPatternDot = Pattern.compile("([.]|[a-zA-Z0-9_-])*");

	public static String checkCodeIdentifier(String name, boolean acceptDot) {
		checkEmpty(name);
		if(acceptDot) {
			if(name.contains("..")) {
				throw new IllegalArgumentException("identifier uses dot characters in sequence");
			}
			_checkIdentifier(name, _iPatternDot.matcher(name));
		}
		else {
			_checkIdentifier(name, _iPattern.matcher(name));
		}
		return name;
	}
	
	public static String checkCodeIdentifier(String name) {
		return checkCodeIdentifier(name, false);
	}
	
	public static String checkTextIdentifier(String name, boolean acceptDot) {
		checkEmpty(name);
		// Check first letter:
		if(Character.isDigit(name.charAt(0))) {
			throw new IllegalArgumentException("identifier must start with a letter or _ and starts with '"+name.charAt(0)+"'");
		}
		if(acceptDot) {
			if(name.contains("-.") || name.contains(".-") || name.contains("--") || name.contains("..")) {
				throw new IllegalArgumentException("identifier uses dot and - characters in sequence");
			}
			_checkIdentifier(name, _tiPatternDot.matcher(name));
		}
		else {
			_checkIdentifier(name, _tiPattern.matcher(name));
		}
		return name;
	}

	public static String checkTextIdentifier(String name) {
		return checkTextIdentifier(name, false);
	}

	private static void _checkIdentifier(String name, Matcher m) {
		// Check first letter:
		if(Character.isDigit(name.charAt(0))) {
			throw new IllegalArgumentException("identifier must start with a letter or _ and starts with '"+name.charAt(0)+"'");
		}
		if(!m.matches()) {
			// show the illegal characters found:
			String illegalChars = m.replaceAll("");
			illegalChars = Strings.eliminateRepeatedChars(illegalChars);
			throw new IllegalArgumentException("identifier has illegal characters '"+illegalChars+"'");
		}
	}

}
