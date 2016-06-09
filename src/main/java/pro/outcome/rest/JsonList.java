package pro.outcome.rest;
import java.util.AbstractList;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;


// For JsonObject:
class JsonList<E> extends AbstractList<E> {

	private final JSONArray _source;
	private final boolean _isJsonObject;
	
	public JsonList(JSONArray array, Class<E> type) {
		_source = array;
		_isJsonObject = (type == JsonObject.class);
	}

	public int size() {
		return _source.size();
	}
	
	@SuppressWarnings("unchecked")
	public E get(int index) {
		if(_isJsonObject) {
			JsonObject json = new JsonObject((JSONObject)_source.get(index));
			return (E)json;
		}
		else {
			return (E)_source.get(index);
		}
	}
	
	@SuppressWarnings("unchecked")
	public E set(int index, E element) {
		if(_isJsonObject) {
			E json = get(index);
			_source.set(index, ((JsonObject)element).getSource());
			return json;
		}
		else {
			return (E)_source.set(index, element);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void add(int index, E element) {
		if(_isJsonObject) {
			_source.add(((JsonObject)element).getSource());
		}
		else {
			_source.add(index, element);
		}
	}
	
	@SuppressWarnings("unchecked")
	public E remove(int index) {
		if(_isJsonObject) {
			E json = get(index);
			_source.remove(index);
			return json;
		}
		else {
			return (E)_source.remove(index);
		}
	}
	
	// For JsonObject:
	JSONArray getSource() {
		return _source;
	}
}
