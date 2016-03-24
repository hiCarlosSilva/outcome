// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.util;
import java.util.List;
import java.util.AbstractList;


public class ImmutableList<T> extends AbstractList<T> {

	private final List<T> _list;
	private final T[] _array;
	
	public ImmutableList(List<T> list) {
		Checker.checkNull(list, "list");
		_list = list;
		_array = null;
	}
	
	public ImmutableList(T[] array) {
		Checker.checkNull(array, "array");
		_list = null;
		_array = array;
	}

	public T get(int index) {
		return _list == null ? _array[index] : _list.get(index);
	}

	public int size() {
		return _list == null ? _array.length : _list.size();
	}
}
