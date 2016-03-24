// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.util;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;


public class Strings {

	private static final String _S = "s";
	private static String _EXPANSION_REGEX = "\\{\\}";

	private Strings() {
		super();
	}

	public static boolean isEmpty(String s) {
		return s==null || s.trim().equals(Constants.EMPTY);
	}

	public static boolean isEmpty(String s, boolean trim) {
		if(s == null) {
			return true;
		}
		if(trim) {
			s = s.trim();
		}
		return s.equals(Constants.EMPTY);
	}

	public static boolean isUpperCase(String s) {
		Checker.checkNull(s, _S);
		for(int i=0; i<s.length(); i++) {
			if(Character.isLowerCase(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isLowerCase(String s) {
		Checker.checkNull(s, _S);
		for(int i=0; i<s.length(); i++) {
			if(Character.isUpperCase(s.charAt(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isNumber(String s) {
		Checker.checkEmpty(s, _S);
		try {
			new Double(s);
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}

	public static boolean isInteger(String s) {
		Checker.checkEmpty(s, _S);
		try {
			new Integer(s);
			return true;
		}
		catch(NumberFormatException nfe) {
			return false;
		}
	}

	// TODO add a isDecimal that checks whether the number has a decimal part
	// (use the locale independent decimal separator)

	// Note: we need to escape the slash character itself (\) otherwise when un-escaping we will
	// miss the character just next to it (i.e. if(charAt(i)=='\\') skip;)
	public static String addSlashes(String s) {
		Checker.checkNull(s, _S);
		StringBuilder b = new StringBuilder(s);
		for(int i=0; i<b.length(); i++) {
			if(b.charAt(i) == '"' || b.charAt(i) == '\'' || b.charAt(i) == '\\') {
				b.insert(i, '\\');
				i++;
			}
		}
		return b.toString();
	}

	// TODO document
	// TODO can we reimplement addSlashes as escape(s, "\"\'")?
	// Note: we need to espace the slash character itself (\) otherwise when unescaping we will
	// miss the character just next to it (i.e. if(charAt(i)=='\\') skip;)
	public static String escape(String s, String tokens) {
		Checker.checkNull(s, _S);
		Checker.checkEmpty(tokens, "tokens");
		tokens = '\\' + tokens;
		StringBuilder sb = new StringBuilder(s);
		for(int i=0; i<sb.length(); i++) {
			for(int j=0; j<tokens.length(); j++) {
				if(sb.charAt(i) == tokens.charAt(j)) {
					sb.insert(i, '\\');
					i++;
					break;
				}
			}
		}
		return sb.toString();
	}
	
	// TODO document: this is to be used with strings that have been escaped with "escape"
	// and both splits and unescapes the characters. It does not use regular exprs
	public static String[] splitAndUnescape(String s, String tokens) {
		Checker.checkNull(s, _S);
		Checker.checkEmpty(tokens, "tokens");
		List<String> rList = new ArrayList<String>();
		StringBuilder sb = new StringBuilder();
		Main:
		for(int i=0; i<s.length(); i++) {
			if(s.charAt(i) == '\\') {
				// Add the character after the slash:
				sb.append(s.charAt(i+1));
				// Skip the slash and the character after it:
				i++;
				continue;
			}
			for(int j=0; j<tokens.length(); j++) {
				if(s.charAt(i) == tokens.charAt(j)) {
					if(sb.length() > 0) {
						rList.add(sb.toString());
						sb = new StringBuilder();
					}
					// Skip the break token:
					continue Main;
				}
			}
			if(i < s.length()) {
				sb.append(s.charAt(i));
			}
		}
		if(sb.length() > 0) {
			rList.add(sb.toString());
		}
		return rList.toArray(new String[0]);
	}
	
	public static String unescape(String s) {
		Checker.checkNull(s, _S);
		final StringBuilder sb = new StringBuilder();
		for(int i=0; i<s.length(); i++) {
			if(s.charAt(i) == '\\') {
				i++;
				// Regular escaped char:
				if(Constants.ESCAPE_CHARS.indexOf(s.charAt(i)) != -1) {
					char c = s.charAt(i);
					sb.append(c=='n'?'\n' : (c=='r'?'\r':(c=='t'?'\t':(c=='b'?'\b':(c=='f'?'\f':(c=='0'?'\0':'\\'))))));
				}
				// Custom escaped char:
				else {
					sb.append(s.charAt(i));
				}
			}
			else {
				sb.append(s.charAt(i));
			}
		}
		return sb.toString();
	}

	public static String valueOf(int i, int length) {
		return valueOf(new Integer(i), length);
	}

	public static String valueOf(Integer i, int length) {
		Checker.checkNull(i, "i");
		Checker.checkIndex(length, "length");
		StringBuilder result = new StringBuilder(i.toString());
		if(result.length() < length) {
			for(int j=result.length(); j<length; j++) {
				result.insert(0, '0');
			}
		}
		return result.toString();
	}

	public static String valueOf(double d, int l, int r) {
		return valueOf(new Double(d), l, r);
	}

	public static String valueOf(Double d, int llength, int rlength) {
		Checker.checkNull(d, "d");
		Checker.checkIndex(llength, "llength");
		Checker.checkIndex(rlength, "rlength");
		String number = d.toString();
		StringBuilder result = new StringBuilder(number);
		String[] array = number.split("[.]");
		if(llength != 0) {
			if(array[0].length() < llength) {
				for(int j=array[0].length(); j<llength; j++) {
					result.insert(0, '0');
				}
			}
		}
		if(rlength != 0) {
			if(array[1].length() > rlength) {
				return result.substring(0, result.indexOf(".")+rlength+1);
			}
			else if(array[1].length() < rlength) {
				for(int j=array[1].length(); j<rlength; j++) {
					result.append('0');
				}
			}
		}
		return result.toString();
	}

	public static int countOccurrences(String s, char toCount) {
		Checker.checkNull(s, _S);
		int count = 0;
		for(int i=0; i<s.length(); i++) {
			if(s.charAt(i) == toCount) {
				count++;
			}
		}
		return count;
	}
	
	public static int countOccurrences(String s, String toCount) {
		Checker.checkNull(s, _S);
		Checker.checkNull(toCount, "toCount");
		int count = 0;
		StringBuilder sb = new StringBuilder(s);
		while(true) {
			int index = sb.indexOf(toCount);
			if(index == -1) {
				return count;
			}
			else {
				count++;
				// TODO more efficient if we use sb.indexOf(toCount, lastIndex)
				sb.replace(index, index+toCount.length(), "");
			}
		}
	}
	
	public static String repeat(String s, int numTimes) {
		Checker.checkNull(s, _S);
		Checker.checkMinValue(numTimes, 1, "numTimes");
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<numTimes; i++) {
			sb.append(s);
		}
		return sb.toString();
	}

	public static String replaceAll(String s, String toReplace, String replacement) {
		Checker.checkEmpty(s, _S);
		Checker.checkEmpty(toReplace, "toReplace");
		replacement = replacement == null  ? Constants.EMPTY : replacement;
		StringBuilder sb = new StringBuilder(s);
		for(int index = sb.indexOf(toReplace); index != -1; index = sb.indexOf(toReplace, index+replacement.length())) {
			sb.replace(index, index+toReplace.length(), replacement);
		}
		return sb.toString();
	}

	public static String eliminateRepeatedChars(String s) {
		Checker.checkEmpty(s, _S);
		Set<Character> tmp = new HashSet<Character>();
		for(int i=0; i<s.length(); i++) {
			tmp.add(s.charAt(i));
		}
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = tmp.iterator();
		while(it.hasNext()) {
			sb.append(it.next());
		}
		return sb.toString();
	}
	
	public static int find(String s, String[] in, boolean ignoreCase) {
		if(ignoreCase) {
			return findIgnoreCase(s, in);
		}
		else {
			return Arrays.find(s, in);
		}
	}

	public static int find(String s, String[] in) {
		return find(s, in, false);
	}

	public static int findIgnoreCase(String s, String[] in) {
		Checker.checkNull(s, "s");
		Checker.checkNull(in, "in");
		for(int i=0; i<in.length; i++) {
			if(s.equalsIgnoreCase(in[i])) {
				return i;
			}
		}
		return -1;
	}

	//TODO document
	public static Boolean toBoolean(String s, boolean ignoreCase) {
		Checker.checkEmpty(s, "s");
		if(equals(s, Constants.TRUE, ignoreCase)) {
			return Boolean.TRUE;
		}
		else if(equals(s, Constants.FALSE, ignoreCase)) {
			return Boolean.FALSE;
		}
		else {
			return null;
		}
	}

	public static Boolean toBoolean(String s) {
		return toBoolean(s, false);
	}

	public static boolean equals(String a, String b, boolean ignoresCase) {
		Checker.checkNull(a, "a");
		Checker.checkNull(b, "b");
		if(ignoresCase) {
			return a.equalsIgnoreCase(b);
		}
		else {
			return a.equals(b);
		}
	}

	public static boolean equals(String a, String b) {
		return equals(a, b, false);
	}

	public static String toHTML(String source) {
		Checker.checkNull(source, "source");
		return HtmlFormatter.format(source);
	}

	public static String toSafeString(String source) {
		Checker.checkNull(source, "source");
		return AsciiFormatter.format(source);
	}
	
	public static String expand(String s, Object ... params) {
		Checker.checkEmpty(s, _S);
		for(Object p : params) {
			if(p == null) {
				p = Constants.NULL_LC;
			}
			s = s.replaceFirst(_EXPANSION_REGEX, p.toString());
		}
		return s;
	}
}
