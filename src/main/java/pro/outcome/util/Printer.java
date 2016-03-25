package pro.outcome.util;

public class Printer {

	public static void print(String s, Object ... params) {
		System.out.print(Strings.expand(s, params));
	}

	public static void println(String s, Object ... params) {
		System.out.println(Strings.expand(s, params));
	}
}
