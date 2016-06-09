// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.data;


public class ConfigValue extends Instance<Config> {

	public ConfigValue() {
	}
	
	public ConfigValue(String name, Object value) {
		this.setValue(getEntity().name, name);
		this.setValue(getEntity().value, value);
	}
	
	// Property getters / setters:
	public String getName() { return getValue(getEntity().name); }
	public Object getValue() { return getValue(getEntity().value); }
	public ConfigValue setValue(Object value) { setValue(getEntity().value, value); return this; }	
}
