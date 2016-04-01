// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;
import java.util.Map;
import java.util.HashMap;
import pro.outcome.util.Checker;
import pro.outcome.util.ImmutableMap;
import pro.outcome.util.IntegrityException;


public abstract class Entities {

	// TYPE:
	private static final Map<String,Entity<?>> _byName = new HashMap<>();
	private static final Map<Class<?>,Entity<?>> _byInstance = new HashMap<>();
	// Entities for this package:
	public static final Config config = new Config();
	
	public static Entity<?> getEntity(String name) {
		Checker.checkEmpty(name);
		return _byName.get(name);
	}

	public static Entity<?> getEntityForInstance(Class<?> c) {
		Checker.checkNull(c);
		return _byInstance.get(c);
	}

	public static ImmutableMap<String,Entity<? extends Instance<?>>> getEntities() {
		return new ImmutableMap<String,Entity<? extends Instance<?>>>(_byName);
	}

	// For Facade:
	static void register(Entity<?> e) {
		if(_byName.containsKey(e.getName())) {
			throw new IllegalArgumentException(e.getName()+": entity has already been registered");
		}
		_byName.put(e.getName(), e);
		if(_byInstance.containsKey(e.getInstanceClass())) {
			throw new IntegrityException();
		}
		_byInstance.put(e.getInstanceClass(), e);
	}
		
	// INSTANCE:
	protected Entities() {}
}
