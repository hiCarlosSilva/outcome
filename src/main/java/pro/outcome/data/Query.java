// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;



// TODO what should the fetch options be?
public class Query<I extends Instance<?>> {

	private final PreparedQuery _pq;
	private final Class<I> _type;
	private int _fetchSize;
	
	// For Facade:
	Query(PreparedQuery pq, Class<I> type) {
		_pq = pq;
		_type = type;
		_fetchSize = 10;
	}
	
	public int getFetchSize() { return _fetchSize; }
	public Query<I> setFetchSize(int fetchSize) { _fetchSize = fetchSize; return this; }
	
	public Iterator<I> iterate() {
		return new _EntityIterator(_pq.asIterator(FetchOptions.Builder.withChunkSize(_fetchSize)));
	}
	
	public List<I> list() {
		// TODO implement this with an interface wrapper for better performance
		List<I> list = new ArrayList<I>();
		for(Entity e : _pq.asList(FetchOptions.Builder.withChunkSize(_fetchSize))) {
			list.add(Instance.newFrom(_type, e));
		}
		return list;
	}

	// Converts from Iterator<Entity> to Iterator<I>:
	private class _EntityIterator implements Iterator<I> {
		
		private final Iterator<Entity> _source;
		
		public _EntityIterator(Iterator<Entity> source) {
			_source = source;
		}
		
		public boolean hasNext() {
			return _source.hasNext();
		}

		public void remove() {
			_source.remove();
		}
		
		public I next() {
			return Instance.newFrom(_type, _source.next());
		}
	}
}
