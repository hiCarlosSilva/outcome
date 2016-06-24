package pro.outcome.util;

public class Path {

	final String _source;

	public Path(String source) {
		Checker.checkEmpty(source);
		_source = source;
	}

	public String[] split() {
		String s = _source.charAt(0)=='/' ? _source.substring(1) : _source;
		return s.split("\\/");
	}
	
	public String last() {
		return Arrays.last(split());
	}
}
