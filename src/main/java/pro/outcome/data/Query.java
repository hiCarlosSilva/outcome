package pro.outcome.data;
import java.util.List;
import java.util.ArrayList;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Query.CompositeFilter;
import com.google.appengine.api.datastore.Query.CompositeFilterOperator;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.SortDirection;
import pro.outcome.util.Checker;
import static pro.outcome.util.Shortcuts.*;


public class Query<I extends Instance<?>> {

	private final Entity<I> _entity;
	private final List<QueryArg> _args;
	private final com.google.appengine.api.datastore.Query _query;
	private int _limit;
	private boolean _savePosition;
	private String _position;
	private int _fetchSize;

	public Query(Entity<I> entity) {
		Checker.checkNull(entity);
		_entity = entity;
		_args = new ArrayList<>();
		_query = new com.google.appengine.api.datastore.Query(_entity.getName());
		_limit = -1;
		_savePosition = false;
		_position = null;
		_fetchSize = 10;
	}
	
	public String toString() {
		return _query.toString();
	}

	public Query<I> addWhere(QueryArg ... args) {
		Checker.checkNullElements(args);
		for(QueryArg arg : args) {
			if(arg.getProperty().getEntity() != _entity) {
				throw new IllegalArgumentException(x("property {} cannot be used to query entity {}", arg.getProperty().getFullName(), _entity.getName()));
			}
			_args.add(arg);
		}
		return this;
	}
	
	public Query<I> addSortByAsc(Property<?> p) {
		return _addSortBy(p, SortDirection.ASCENDING);
	}

	public Query<I> addSortByDesc(Property<?> p) {
		return _addSortBy(p, SortDirection.DESCENDING);
	}
	
	public Query<I> setLimit(int limit) {
		Checker.checkMinValue(limit, 1);
		_limit = limit;
		return this;
	}
	
	public Query<I> setSavePosition(boolean value) {
		_savePosition = value;
		return this;
	}
	
	public Query<I> setPosition(String position) {
		Checker.checkEmpty(position);
		_position = position;
		return this;
	}

	public int getFetchSize() {
		return _fetchSize;
	}
	
	public Query<I> setFetchSize(int fetchSize) {
		Checker.checkMinValue(fetchSize, 1);
		_fetchSize = fetchSize;
		return this;
	}
	
	public QueryResult<I> run() {
		// Prepare query:
		if(_args.size() > 0) {
			List<Filter> filters = new ArrayList<>(_args.size());
			for(QueryArg arg : _args) {
				filters.add(arg.toFilter());
			}
			Filter f = filters.size() > 1 ? new CompositeFilter(CompositeFilterOperator.AND, filters) : filters.get(0);
			_query.setFilter(f);
		}
		PreparedQuery pq = _entity.getDatastoreService().prepare(_query);
		// Prepare fetch options:
		FetchOptions options = FetchOptions.Builder.withChunkSize(_fetchSize);
		if(_limit != -1) {
			options.limit(_limit);
		}
		if(_position != null) {
			options.startCursor(Cursor.fromWebSafeString(_position));
		}
		// Return wrapper:
		_entity.getLogger().log(info("running query: {}", pq));
		return new QueryResult<I>(_entity.getInstanceClass(), pq, options, _savePosition);
	}
	
	private Query<I> _addSortBy(Property<?> p, SortDirection direction) {
		Checker.checkNull(p);
		if(p.getEntity() != _entity) {
			throw new IllegalArgumentException(x("property {} cannot be used to sort entity {}", p.getFullName(), _entity.getName()));
		}
		if(!p.isIndexed()) {
			throw new IllegalArgumentException(x("{}: cannot sort on non-indexed properties", p.getFullName()));
		}
		_query.addSort(p.getName(), direction);
		return this;
	}
}
