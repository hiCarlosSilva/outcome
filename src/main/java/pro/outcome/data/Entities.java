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
import pro.outcome.util.Reflection;


public abstract class Entities {

	// TYPE:
	private static final Map<String,Entity<?>> _byName = new HashMap<>();
	private static final Map<Class<?>,Entity<?>> _byInstance = new HashMap<>();
	// Entities for this package:
	public static final Config config = load(Config.class);

	public static <E extends Entity<?>> E load(Class<E> c) {
		@SuppressWarnings("unchecked")
		E e = (E)_byName.get(c.getSimpleName());
		if(e != null) {
			return e;
		}
		// Create:
		e = Reflection.createObject(c);
		// Register:
		_byName.put(e.getName(), e);
		if(_byInstance.containsKey(e.getInstanceClass())) {
			throw new IntegrityException();
		}
		_byInstance.put(e.getInstanceClass(), e);
		// Load:
		e.load();
		return e;
	}
	
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
		
	// INSTANCE:
	protected Entities() {}
}
