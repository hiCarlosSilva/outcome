// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import pro.outcome.util.Checker;
import pro.outcome.util.ImmutableMap;
import pro.outcome.util.Logger;
import pro.outcome.util.Reflection;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;


public class Entities {

	// TYPE:
	public static final Entities ref = new Entities();
	
	// INSTANCE:
	private final Map<String,Model> _models;
	// TODO private
	public final Map<String,Facade<? extends Instance<?>>> _facades;
	private final Set<Package> _loadedPackages;
	private final List<String> _toLoad;
	private final Logger _logger;
	private boolean _readDatabase;

	private Entities() {
		_models = new HashMap<String,Model>();
		_facades = new HashMap<>();
		_loadedPackages = new HashSet<Package>();
		_logger = Logger.get(getClass());
		_toLoad = new ArrayList<String>();
		_readDatabase = false;
	}
	
	// TODO consider doing 2: getModelForEntity, getModelForInstance
	public Model getModel(String name) {
		Checker.checkEmpty(name, "name");
		return _models.get(name);
	}
	
	public ImmutableMap<String,Model> getModels() {
		return new ImmutableMap<String,Model>(_models);
	}
	
	public Facade<? extends Instance<?>> getEntity(String name) {
		Checker.checkEmpty(name, "name");
		return _facades.get(name);
	}
	
	public ImmutableMap<String,Facade<? extends Instance<?>>> getEntities() {
		return new ImmutableMap<String,Facade<? extends Instance<?>>>(_facades);
	}

	// For Model:
	void register(Model model) {
		Checker.checkNull(model, "model");
		if(_models.containsKey(model.getInstanceName())) {
			throw new IllegalArgumentException("model for entity instance '"+model.getInstanceName()+"' has already been registered");
		}
		_models.put(model.getInstanceName(), model);
	}

	// For Facade:
	@SuppressWarnings("unchecked")
	void load(Facade<? extends Instance<?>> f) {
		Checker.checkNull(f, "f");
		// Load entities from GAE if necessary:
		if(!_readDatabase) {
			_logger.info("reading entities stored in the database");
			DatastoreService ds = DatastoreServiceFactory.getDatastoreService(); 
			Query q = new Query(com.google.appengine.api.datastore.Entities.KIND_METADATA_KIND);
			for(Entity e : ds.prepare(q).asIterable()) {
				_toLoad.add(e.getKey().getName());
			}
			_readDatabase = true;
		}
		// This may be the first time the entity gets loaded, if it wasn't stored yet:
		// TODO remove this
		//String eName = f.getModel().getEntityName();
		//if(!_facades.containsKey(eName)) {
		//	_facades.put(eName, f);
		//}
		// Check if this facade's package has already been loaded:
		Package p = f.getClass().getPackage();
		if(_loadedPackages.contains(p)) {
			// Nothing else to do:
			return;
		}
		// Mark the package loaded so that other entities don't try to load it concurrently:
		_loadedPackages.add(p);
		// Load all entities that belong to this entity's package:
		_logger.info("loading entities in package {}", p);
		Iterator<String> it = _toLoad.iterator();
		while(it.hasNext()) {
			String name = it.next();
			String fullName = p.getName() + "." + name;
			Class<?> c = Reflection.load(true, fullName);
			if(c != null) {
				it.remove();
				if(c == f.getClass()) {
					_facades.put(name, f);
				}
				else {
					_facades.put(name, (Facade<Instance<?>>)Reflection.readField(c, "ref", null));
				}
				_logger.info("loaded entity {}", c.getCanonicalName());
			}
		}
		if(_toLoad.isEmpty()) {
			_logger.info("all entities loaded");
		}
	}
}
