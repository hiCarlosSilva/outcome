// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.rest;

class Parameter {

	public static Integer toInteger(String paramName, String value) {
		try {
			return new Integer(value);
		}
		catch(NumberFormatException nfe) {
			throw new ParameterValidationException(paramName, value);
		}
	}

	public static Long toLong(String paramName, String value) {
		try {
			return new Long(value);
		}
		catch(NumberFormatException nfe) {
			throw new ParameterValidationException(paramName, value);
		}
	}

	public static Float toFloat(String paramName, String value) {
		try {
			return new Float(value);
		}
		catch(NumberFormatException nfe) {
			throw new ParameterValidationException(paramName, value);
		}
	}
	public static Double toDouble(String paramName, String value) {
		try {
			return new Double(value);
		}
		catch(NumberFormatException nfe) {
			throw new ParameterValidationException(paramName, value);
		}
	}
}
