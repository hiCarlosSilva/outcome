// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.data;


public class ConfigValue extends Instance<ConfigValueModel> {

	// TYPE:
	public static final ConfigValueModel model = new ConfigValueModel();
	
	// INSTANCE:
	public ConfigValue() {
	}
	
	public ConfigValue(String name, Object value) {
		this.setValue(model.name, name);
		this.setValue(model.value, value);
	}
	
	public ConfigValueModel getModel() {
		return model;
	}
	
	// Field getters / setters:
	public String getName() { return getValue(model.name); }
	public Object getValue() { return getValue(model.value); }
	public ConfigValue setValue(Object value) { setValue(model.value, value); return this; }	
}
