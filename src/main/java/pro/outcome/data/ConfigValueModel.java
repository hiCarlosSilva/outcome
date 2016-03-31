// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.data;
import pro.outcome.data.Field.Constraint;


public class ConfigValueModel extends Model {

	public final Field<String> name;
	public final Field<Object> value;
	
	public ConfigValueModel() {
		super("Config", "ConfigValue");
		name = addField(String.class, "name", true, Constraint.MANDATORY, Constraint.UNIQUE, Constraint.READ_ONLY);
		value = addField(Object.class, "value", false, (Object)null, Constraint.MANDATORY);
	}
	
	public Field<?>[] getNaturalKeyFields() {
		return new Field<?>[] { name };
	}
}
