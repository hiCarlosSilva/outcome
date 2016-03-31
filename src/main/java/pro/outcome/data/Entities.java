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
	private static final Map<String,Model> _models = new HashMap<>();
	private static final Map<String,Entity<?>> _entities = new HashMap<>();
	// Entities for this package:
	public static final Config config = new Config();
	
	public static Entity<?> getEntity(String name) {
		Checker.checkEmpty(name);
		return _entities.get(name);
	}

	public static ImmutableMap<String,Entity<? extends Instance<?>>> getEntities() {
		return new ImmutableMap<String,Entity<? extends Instance<?>>>(_entities);
	}

	public static Model getModel(String name) {
		Checker.checkEmpty(name);
		return _models.get(name);
	}

	public static ImmutableMap<String,Model> getModels() {
		return new ImmutableMap<String,Model>(_models);
	}

	// For Facade:
	static void register(Entity<?> f) {
		Model m = f.getModel();
		if(_entities.containsKey(m.getEntityName())) {
			throw new IllegalArgumentException(m.getEntityName()+": entity has already been registered");
		}
		_entities.put(m.getEntityName(), f);
		if(_models.containsKey(m.getInstanceName())) {
			throw new IntegrityException();
		}
		_models.put(m.getInstanceName(), m);
	}
		
	// INSTANCE:
	protected Entities() {}
}
