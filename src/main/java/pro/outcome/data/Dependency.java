// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;


class Dependency {

	public final Entity<Instance<?>> entity;
	public final Property<?> foreignKey;
	public final Property.OnDelete onDelete;

	public Dependency(Entity<Instance<?>> entity, Property<?> foreignKey, Property.OnDelete onDelete) {
		this.entity = entity;
		this.foreignKey = foreignKey;
		this.onDelete = onDelete;
	}
	
	public String toString() {
		return "Dependency: "+foreignKey.getFullName();
	}
	
	public Query<Instance<?>> findInstancesRelatedTo(Instance<?> i) {
		return entity.findWhere(new QueryArg(foreignKey, i));
	}
}
