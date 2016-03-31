// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.util;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


public class Reflection {

	@SuppressWarnings("unchecked")
	public static <T> T createObject(Class<T> c, Class<?>[] params, Object[] args) {
		Checker.checkNull(c);
		Checker.checkNullElements(params);
		Checker.checkNull(args);
		// Check if the class is an interface:
		int modifiers = c.getModifiers();
		if(Modifier.isInterface(modifiers)) {
			throw new ConstructionTargetException("cannot instantiate an interface");
		}
		// Check if the class is abstract:
		// NOTE: this needs to be tested _after_ Modifier.isInterface,
		// because Modifier.isAbstract also returns true for interfaces.
		if(Modifier.isAbstract(modifiers)) {
			throw new ConstructionTargetException("cannot instantiate an abstract class");
		}
		try {
			// Create "regular" (no constructor) enum:
			if(c.isEnum() && params.length==1 && params[0]==String.class && args.length==1 && args[0].getClass()==String.class) {
				@SuppressWarnings("rawtypes")
				T result = (T)Enum.valueOf((Class<? extends Enum>)c, (String)args[0]); 
				return result;
			}
			else {
				try {
					Constructor<T> cons = c.getDeclaredConstructor(params);
					return cons.newInstance(args);
				}
				catch(NoSuchMethodException nsme) {
					// Did not find a direct match, let's look for a sub-class match:
					Constructor<T>[] constructors = (Constructor<T>[])c.getConstructors();
					for(Constructor<T> cons : constructors) {
						Class<?>[] consParams = cons.getParameterTypes();
						if(consParams.length == params.length) {
							boolean matchFound = true;
							for(int i=0; i<params.length; i++) {
								if(!consParams[i].isAssignableFrom(params[i])) {
									matchFound = false;
									break;
								}
							}
							if(matchFound) {
								return cons.newInstance(args);
							}
						}
					}
					// If it got here, no appropriate constructor has been found:
					throw new ConstructorNotFoundException();
				}
			}
		}
		catch(IllegalAccessException iae) {
			throw new ConstructorNotVisibleException(iae);
		}
		catch(InstantiationException ie) {
			// This can't happen, since we tested 
			// whether the class was abstract before:
			throw new IntegrityException(ie);
		}
		catch(InvocationTargetException ite) {
			throw new ConstructorInvocationException(ite.getCause());
		}
	}

	public static <T> T createObject(Class<T> c, Object ... args) {
		Checker.checkNull(c);
		Checker.checkNullElements(args);
		Class<?>[] params = new Class[args.length];
		for(int i=0; i<params.length; i++) {
			params[i] = args[i].getClass();
		}
		return createObject(c, params, args);
	}

	public static <T> T createObject(Class<T> c) {
		return createObject(c, new Class[0], new Object[0]);
	}
	
	public static Class<?> load(boolean lenient, String className) {
		Checker.checkEmpty(className);
		try {
			return Class.forName(className);
		}
		catch(ClassNotFoundException cnfe) {
			if(lenient) {
				return null;
			}
			else {
				throw new ReflectionException(cnfe);
			}
		}
	}
	
	public static Class<?> load(String className) {
		return load(false, className);
	}
	
	public static Method getDeclaredMethod(boolean lenient, Class<?> c, String name, Class<?> ... parameterTypes) {
		Checker.checkNull(c);
		Checker.checkEmpty(name);
		try {
			return c.getDeclaredMethod(name, parameterTypes);
		}
		catch(Exception e) {
			if(lenient) {
				return null;
			}
			throw new ReflectionException(e);
		}
	}

	public static Method getDeclaredMethod(Class<?> c, String name, Class<?> ... parameterTypes) {
		return getDeclaredMethod(false, c, name, parameterTypes);
	}

	// TODO change order of these params to c, fieldName, o
	public static Object readField(Class<?> c, String fieldName, Object o) {
		Checker.checkNull(c);
		Checker.checkEmpty(fieldName);
		try {
			return c.getField(fieldName).get(o);
		}
		catch(NoSuchFieldException nsfe) {
			throw new IllegalUsageException(Strings.expand("could not find field {} in class {}", fieldName, c.getName()));
		}
		catch (IllegalAccessException e) {
			throw new IllegalUsageException(Strings.expand("could not access field {} in class {}", fieldName, c.getName()));
		}
	}
}
