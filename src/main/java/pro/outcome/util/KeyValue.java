// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.util;


public class KeyValue<T> {

	private final String _name;
	private T _value;
	
	public KeyValue(String name, T value) {
		Checker.checkEmpty(name);
		_name = name;
		_value = value;
	}
	
	public String getName() {
		return _name;
	}
	
	public T getValue() {
		return _value;
	}
	
	public KeyValue<T> setValue(T value) {
		_value = value;
		return this;
	}
}
