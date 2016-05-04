// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.rest;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.AbstractList;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import pro.outcome.util.Checker;


public class JsonObject {

	// TYPE:
	public static JsonObject parse(String s) {
		Checker.checkEmpty(s);
		try {
			return new JsonObject((JSONObject)new JSONParser().parse(s));
		}
		catch(ParseException pe) {
			throw new IllegalArgumentException(pe);
		}
	}
	
	// INSTANCE:
	private final JSONObject _json;
	
	private JsonObject(JSONObject json) {
		_json = json;
	}
	
	public JsonObject() {
		_json = new JSONObject();
	}
	
	public int hashCode() {
		return _json.hashCode();
	}
	
	public String toString() {
		return _json.toString();
	}

	@SuppressWarnings("unchecked")
	public JsonObject put(String name, Object value) {
		Checker.checkEmpty(name);
		if(value != null) {
			if(value.getClass() == JsonObject.class) {
				value = ((JsonObject)value)._json;
			}
		}
		_json.put(name, value);
		return this;
	}
	
	public Object remove(String name) {
		Checker.checkEmpty(name);
		Object value = _json.remove(name);
		if(value != null) {
			if(value instanceof JSONObject) {
				value = new JsonObject((JSONObject)value);
			}
		}
		return value;
	}

	@SuppressWarnings("unchecked")
	public JsonObject addChild(String name) {
		Checker.checkEmpty(name);
		JsonObject child = new JsonObject();
		_json.put(name, child._json);
		return child;
	}
	
	public String getString(String name) {
		Checker.checkEmpty(name);
		return (String)_json.get(name);
	}
	
	public JsonObject getJsonObject(String name) {
		Checker.checkEmpty(name);
		JSONObject json = (JSONObject)_json.get(name);
		return new JsonObject(json);
	}
	
	public <E> List<E> getListOf(String name, Class<E> type) {
		Checker.checkEmpty(name);
		Checker.checkNull(type);
		final JSONArray array = (JSONArray)_json.get(name);
		final boolean jsonObject = type == JsonObject.class;
		if(array == null) {
			return null;
		}
		return new AbstractList<E>() {
			
			public int size() {
				return array.size();
			}
			
			@SuppressWarnings("unchecked")
			public E get(int index) {
				if(jsonObject) {
					JsonObject json = new JsonObject((JSONObject)array.get(index));
					return (E)json;
				}
				else {
					return (E)array.get(index);
				}
			}
			
			@SuppressWarnings("unchecked")
			public E set(int index, E element) {
				if(jsonObject) {
					E json = get(index);
					array.set(index, ((JsonObject)element)._json);
					return json;
				}
				else {
					return (E)array.set(index, element);
				}
			}
			
			@SuppressWarnings("unchecked")
			public void add(int index, E element) {
				if(jsonObject) {
					array.add(((JsonObject)element)._json);
				}
				else {
					array.add(index, element);
				}
			}
			
			@SuppressWarnings("unchecked")
			public E remove(int index) {
				if(jsonObject) {
					E json = get(index);
					array.remove(index);
					return json;
				}
				else {
					return (E)array.remove(index);
				}
			}
		};
	}

	public void write(Writer out, boolean prettyPrint) throws IOException {
		Checker.checkNull(out);
		_json.writeJSONString(out);
	}

	public void write(Writer out) throws IOException {
		write(out, false);
	}
}
