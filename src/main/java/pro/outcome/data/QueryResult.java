package pro.outcome.data;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.QueryResultList;
import pro.outcome.util.IllegalUsageException;


public class QueryResult<I extends Instance<?>> {

	private final Class<I> _type;
	private final PreparedQuery _pq;
	private final FetchOptions _options;
	private final boolean _savePosition;
	private QueryResultList<Entity> _list;
	private boolean _positionRetrieved;

	// For Query:
	QueryResult(Class<I> type, PreparedQuery pq, FetchOptions options, boolean savePosition) {
		_type = type;
		_pq = pq;
		_options = options;
		_savePosition = savePosition;
		_list = null;
		_positionRetrieved = false;
	}
	
	public Iterator<I> iterate() {
		if(_savePosition) {
			return list().iterator();
		}
		return new _InstanceIterator(_pq.asIterator(_options));
	}
	
	public List<I> list() {
		if(_savePosition) {
			_list = _pq.asQueryResultList(_options);
			return new _InstanceList(_list);
		}
		return new _InstanceList(_pq.asList(_options));
	}
	
	public String getPosition() {
		if(!_savePosition) {
			throw new IllegalUsageException("position was not saved");
		}
		if(_positionRetrieved) {
			throw new IllegalUsageException("position was already retrieved");
		}
		_positionRetrieved = true;
		if(_list.size() < _options.getLimit()) {
			// No more results:
			return null;
		}
		return _list.getCursor().toWebSafeString();
	}

	// Converts from Iterator<Entity> to Iterator<I>:
	private class _InstanceIterator implements Iterator<I> {
		
		private final Iterator<Entity> _source;
		
		public _InstanceIterator(Iterator<Entity> source) {
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
	
	private class _InstanceList extends AbstractList<I> {
		
		private final List<Entity> _source;
		private final Object[] _cache;
		
		public _InstanceList(List<Entity> source) {
			_source = source;
			_cache = new Object[_source.size()];
		}
		
		public int size() {
			return _source.size();
		}
		
		@SuppressWarnings("unchecked")
		public I get(int index) {
			if(_cache[index] != null) {
				return (I)_cache[index];
			}
			I i = Instance.newFrom(_type, _source.get(index));
			_cache[index] = i;
			return i;
		}
	}
}
