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

import pro.outcome.data.QueryArg.Operator;
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
	private QueryArg _inequality;

	public Query(Entity<I> entity) {
		Checker.checkNull(entity);
		_entity = entity;
		_args = new ArrayList<>();
		_query = new com.google.appengine.api.datastore.Query(_entity.getName());
		_limit = -1;
		_savePosition = false;
		_position = null;
		_fetchSize = 10;
		_inequality = null;
	}
	
	public String toString() {
		return _query.toString();
	}

	public Query<I> addWhere(QueryArg ... args) {
		Checker.checkNullElements(args);
		for(QueryArg arg : args) {
			if(arg.getProperty().getEntity() != _entity) {
				throw new IllegalArgumentException(x("property '{}' cannot be used to query entity '{}'", arg.getProperty().getFullName(), _entity.getName()));
			}
			if(arg.getProperty() == _entity.id) {
				throw new IllegalArgumentException("cannot use 'id' property in multiple result query. Use findSingle instead.");
			}
			// All inequality filters must apply to the same property:
			if(arg.getOperator() != Operator.EQUAL) {
				if(_inequality == null) {
					_inequality = arg;
				}
				else {
					if(!arg.getProperty().equals(_inequality.getProperty())) {
						throw new IllegalArgumentException(x("query already has an inequality filter for property '{}'", _inequality.getProperty().getName()));
					}
				}
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
			throw new IllegalArgumentException(x("{}: cannot sort on non-indexed properties", p.getName()));
		}
		// Properties used in inequality filters must be sorted first:
		if(_query.getSortPredicates().isEmpty()) {
			if(_inequality != null && !p.equals(_inequality)) {
				throw new IllegalArgumentException(x("{}: a filter on '{}' is required first, because it has an inequality filter", p.getName(), _inequality.getProperty().getName()));
			}
		}

		// Sort orders are ignored on properties with equality filters:
		for(QueryArg arg : _args) {
			if(arg.getProperty().equals(p) && arg.getOperator() == Operator.EQUAL) {
				throw new IllegalArgumentException(x("{}: cannot sort on properties with equality filters", p.getName()));
			}
		}

		_query.addSort(p.getName(), direction);
		return this;
	}
}
