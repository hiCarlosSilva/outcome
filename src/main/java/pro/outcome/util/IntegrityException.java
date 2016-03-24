// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.util;


public class IntegrityException extends RuntimeException {

	public IntegrityException() {
		super();
	}
	
	public IntegrityException(String message) {
		super(message);
	}

	public IntegrityException(Throwable cause) {
		super(cause);
	}

	public IntegrityException(String message, Throwable cause) {
		super(message, cause);
	}

	public IntegrityException(boolean b) {
		super(b+"");
	}

	public IntegrityException(char c) {
		super(c+"");
	}

	public IntegrityException(byte b) {
		super(b+"");
	}

	public IntegrityException(short s) {
		super(s+"");
	}

	public IntegrityException(int i) {
		super(i+"");
	}

	public IntegrityException(long l) {
		super(l+"");
	}

	public IntegrityException(float f) {
		super(f+"");
	}

	public IntegrityException(double d) {
		super(d+"");
	}

	public IntegrityException(Object o) {
		super(o+"");
	}

	private static final long serialVersionUID = 1L;
}
