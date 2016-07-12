// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;


public class Dependency {

	private final Entity<Instance<?>> _entity;
	private final Property<?> _foreignKey;

	// For Entity:
	Dependency(Entity<Instance<?>> entity, Property<?> foreignKey) {
		_entity = entity;
		_foreignKey = foreignKey;
	}
	
	public Entity<Instance<?>> getEntity() {
		return _entity;
	}

	public Property<?> getForeignKey() {
		return _foreignKey;
	}

	public String toString() {
		return "Dependency: "+_foreignKey.getFullName();
	}
	
	public QueryResult<Instance<?>> findInstancesRelatedTo(Instance<?> i) {
		return _entity.findWhere(new QueryArg(_foreignKey, i, QueryArg.Operator.EQUAL));
	}
}
