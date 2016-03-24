// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.data;


class Dependency {

	public final Model entity;
	public final Field<Long> foreignKey;
	public final Field.OnDelete onDelete;

	public Dependency(Model entity, Field<Long> foreignKey, Field.OnDelete onDelete) {
		this.entity = entity;
		this.foreignKey = foreignKey;
		this.onDelete = onDelete;
	}
	
	public String toString() {
		return "Dependency:"+foreignKey.getFullName();
	}
}
