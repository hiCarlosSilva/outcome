// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.util;
import java.util.AbstractSet;
import java.util.Set;
import java.util.Iterator;


public class ImmutableSet<T> extends AbstractSet<T> {

	private final Set<T> _set;

	public ImmutableSet(Set<T> c) {
		Checker.checkNull(c, "c");
		_set = c;
	}

	public Iterator<T> iterator() {
		return new ImmutableIterator<T>(_set.iterator());
	}
	
	public int size() {
		return _set.size();
	}
}
