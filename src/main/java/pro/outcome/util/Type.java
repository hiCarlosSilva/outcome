// All the information contained in this source code file is a property of Intersection Labs Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact us on listening@connector.im.
package pro.outcome.util;

public class Type {

	// TYPE:
	// primitive type helper:
	// type name | class | object class | array type name
	private static Object[][] _primitives = {
		{"boolean", boolean.class,	Boolean.class,		"Z"},
		{"char", 	char.class,		Character.class,	"C"},
		{"byte", 	byte.class,		Byte.class,			"B"},
		{"short", 	short.class,	Short.class,		"S"},
		{"int", 	int.class,		Integer.class,		"I"},
		{"long", 	long.class,		Long.class,			"J"},
		{"float", 	float.class,	Float.class,		"F"},
		{"double", 	double.class,	Double.class,		"D"}
	};

	// INSTANCE:
	private final String _name;
	private final Class<?> _class;
	private final boolean _isPrimitive;
	private final int _arrayDimensions;
	private final int _primitiveIndex;
	private String _nameBuffer;
	
	public Type(String typeName) {
		Checker.checkEmpty(typeName, "typeName");
		typeName = typeName.replaceAll(" ", "");
		typeName = typeName.replaceAll("\t", "");
		// check for array class:
		_arrayDimensions = Strings.countOccurrences(typeName, "[]");
		if(_arrayDimensions > 0) {
			_name = typeName.replaceAll("[\\[][\\]]", "");
		}
		else {
			_name = typeName;
		}
		// test whether the name has illegal arguments
		// (account for "$" character in class name):
		String tmpName = _name.replaceAll("[$]", "");
		Checker.checkCodeIdentifier(tmpName, "className", true);
		// check if this is a primitive type:
		boolean lclIsPrimitive = false;
		int lclPrimitiveIndex = -1;
		for(int i=0; i<_primitives.length; i++) {
			if(_name.equals(_primitives[i][0])) {
				lclIsPrimitive = true;
				lclPrimitiveIndex = i;
				break;
			}
		}
		_isPrimitive = lclIsPrimitive;
		_primitiveIndex = lclPrimitiveIndex;
		_nameBuffer = null;
		// Load Class object:
		if(_isPrimitive && _arrayDimensions==0) {
			_class = (Class<?>)_primitives[_primitiveIndex][1];
		}
		else {
			// Transform the input String to a Java Class string representation:
			StringBuilder javaClassName = new StringBuilder();
			if(_arrayDimensions > 0) {
				javaClassName.append(Strings.repeat("[", _arrayDimensions));
			}
			if(_isPrimitive) {
				javaClassName.append(_primitives[_primitiveIndex][3]);
			}
			else {
				if(_arrayDimensions > 0) {
					javaClassName.append("L");
				}
				if(!_name.contains(".")) {
					javaClassName.append("java.lang.");
				}
				javaClassName.append(_name);
				if(_arrayDimensions > 0) {
					javaClassName.append(";");
				}
			}
			// Load the Class object:
			try {
				_class = Class.forName(javaClassName.toString());
			}
			catch(ClassNotFoundException cnfe) {
				throw new ReflectionException(cnfe);
			}
		}
	}
	
	public Type(Class<?> c) {
		Checker.checkNull(c, "c");
		_class = c;
		int count = 0;
		while(c.isArray()) {
			c = c.getComponentType();
			count++;
		}
		_name = c.getName();
		_arrayDimensions = count;
		// check if this is a primitive type:
		boolean lclIsPrimitive = false;
		int lclPrimitiveIndex = -1;
		for(int i=0; i<_primitives.length; i++) {
			if(_name.equals(_primitives[i][0])) {
				lclIsPrimitive = true;
				lclPrimitiveIndex = i;
				break;
			}
		}
		_isPrimitive = lclIsPrimitive;
		_primitiveIndex = lclPrimitiveIndex;
		_nameBuffer = null;
	}

	public boolean isPrimitive() {
		return _isPrimitive;
	}
	
	public int getArrayDimensions() {
		return _arrayDimensions;
	}
	
	public Class<?> getJavaClass() {
		return _class;
	}
	
	public Class<?> getObjectClass() {
		if(_isPrimitive) {
			if(_arrayDimensions > 0) {
				// transform c into it's object counterpart:
				Class<?> objectClass = (Class<?>)_primitives[_primitiveIndex][2];
				return new Type(objectClass.getName()+Strings.repeat("[]", _arrayDimensions)).getJavaClass();
			}
			else {
				return (Class<?>)_primitives[_primitiveIndex][2];
			}
		}
		else {
			return _class;
		}
	}

	public Class<?> getBaseClass() {
		if(_arrayDimensions == 0) {
			return _class;
		}
		else {
			return _class.getComponentType();
		}
	}

	public String getName() {
		if(_nameBuffer == null) {
			if(_arrayDimensions > 0) {
				_nameBuffer = _name+Strings.repeat("[]", _arrayDimensions);
			}
			else {
				_nameBuffer = _name;
			}
		}
		return _nameBuffer;
	}

	public String getClassName() {
		String name = getName();
		return name.substring(name.lastIndexOf('.')+1);
	}

	public String toString() {
		return getName();
	}
}
