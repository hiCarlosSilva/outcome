// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.util;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;


public class ImmutableMap<K,V> extends AbstractMap<K,V> {

	private final Map<K,V> _map;
	
	public ImmutableMap(Map<K,V> map) {
		Checker.checkNull(map);
		_map = map;
	}

	public Set<Map.Entry<K,V>> entrySet() {
		return new ImmutableSet<Map.Entry<K,V>>(_map.entrySet());
	}
}
