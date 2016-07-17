package pro.outcome.util;

public class Util {

	public static boolean equals(Object a, Object b) {
		if(a == null) {
			return b == null;
		}
		if(b == null) {
			return a == null;
		}
		return a.equals(b);
	}
}
