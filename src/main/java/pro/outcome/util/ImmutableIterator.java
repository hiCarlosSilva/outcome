// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.util;
import java.util.Iterator;


public class ImmutableIterator<T> implements Iterator<T> {

	private final Iterator<T> _it;

	public ImmutableIterator(Iterator<T> it) {
		Checker.checkNull(it, "it");
		this._it = it;
	}

	public boolean hasNext() {
		return _it.hasNext();
	}

	public T next() {
		return _it.next();
	}

	public void remove() {
		throw new UnsupportedOperationException();
	}
}
