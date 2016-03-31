// All the information contained in this source code file is a property of Outcome Professional Services Limited,
// a company registered in the United Kingdom. Use and distribution of any part of the information 
// contained in this source code file without our prior consent is forbidden. If you have an interest 
// in using any part of this source code in your software, please contact hiCarlosSilva@gmail.com.
package pro.outcome.util;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.lang.reflect.Array;



public class Arrays {

	private Arrays() {
		super();
	}

	public static <T> T last(T[] array) {
		Checker.checkEmpty(array, "array");
		return array[array.length-1];
	}

	public static int find(Object o, Object[] in) {
		Checker.checkNull(o, "o");
		Checker.checkNull(in, "in");
		for(int i=0; i<in.length; i++) {
			if(o.equals(in[i])) {
				return i;
			}
		}
		return -1;
	}

	public static boolean contains(Object o, Object[] in) {
		return find(o, in) != -1;
	}

	public static Object[] toArrayOf(Class<?> c, Object[] array) {
		Checker.checkNull(c, "c");
		Checker.checkNull(array, "array");
		Object[] result = (Object[])Array.newInstance(array.getClass().getComponentType(), array.length);
		for(int i=0; i<array.length; i++) {
			result[i] = array[i];
		}
		return result;
	}
	
	public static Object[] toArrayOf(java.lang.Class<?> c, Iterator<?> it) {
		Checker.checkNull(c, "c");
		Checker.checkNull(it, "it");
		LinkedList<Object> list = new LinkedList<Object>();
		while(it.hasNext()) {
			list.add(it.next());
		}
		return toArrayOf(c, list.toArray());
	}

	public static Object[] toArrayOf(java.lang.Class<?> c, Iterable<?> it) {
		Checker.checkNull(it, "col");
		return toArrayOf(c, it.iterator());
	}

	public static Object[] toObjectArray(boolean[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Boolean(array[i]);
		}
		return objArray;
	}

	public static Object[] toObjectArray(char[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Character(array[i]);
		}
		return objArray;
	}

	public static Object[] toObjectArray(short[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Short(array[i]);
		}
		return objArray;
	}

	public static Object[] toObjectArray(int[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Integer(array[i]);
		}
		return objArray;
	}

	public static Object[] toObjectArray(long[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Long(array[i]);
		}
		return objArray;
	}

	public static Object[] toObjectArray(float[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Float(array[i]);
		}
		return objArray;
	}

	public static Object[] toObjectArray(double[] array) {
		Object[] objArray = new Object[array.length];
		for(int i=0; i<array.length; i++) {
			objArray[i] = new Double(array[i]);
		}
		return objArray;
	}

	public static <T> T[] concat(T[] array1, T[] array2) {
		Checker.checkNull(array1, "array1");
		Checker.checkNull(array2, "array2");
		List<T> result = new ArrayList<T>();
		for(T elem : array1) {
			result.add(elem);
		}
		for(T elem : array2) {
			result.add(elem);
		}
		// toArray may be used with array1, because it's
		// not big enougth to hold the results of concat.
		return result.toArray(array1);
	}

	@SuppressWarnings("unchecked")
	public static <T> T[] subtract(T[] array1, T[] array2) {
		List<T> result = new ArrayList<T>();
		for(int i=0; i<array2.length; i++) {
			boolean found = false;
			for(int j=0; j<array1.length; j++) {
				if(array1[j].equals(array2[i])) {
					found = true;
					break;
				}
			}
			if(!found) {
				result.add(array2[i]);
			}
		}
		return (T[])result.toArray((Object[])Array.newInstance(array1.getClass().getComponentType(), 0));
	}

	public static <T> void copy(T[] from, int fromIndex, T[] to, int toIndex, int length) {
		Checker.checkNull(to, "to");
		Checker.checkNull(from, "from");
		Checker.checkIndex(toIndex, "toIndex");
		Checker.checkIndex(fromIndex, "fromIndex");
		Checker.checkIndex(length, "length");
		if(toIndex+length > to.length) {
			throw new IndexOutOfBoundsException("index: "+(toIndex+length)+"; array length: "+to.length);
		}
		if(fromIndex+length > from.length) {
			throw new IndexOutOfBoundsException("index: "+(fromIndex+length)+"; array length: "+from.length);
		}
		System.arraycopy(from, fromIndex, to, toIndex, length);
	}

	public static <T> void copy(T[] from, int fromIndex, T[] to, int toIndex) {
		copy(from, fromIndex, to, toIndex, from.length);
	}

	public static <T> void copy(T[] from, T[] to) {
		copy(from, 0, to, 0);
	}

	public static String toString(Object[] array) {
		Checker.checkNull(array, "array");
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		for(int i=0; i<array.length; i++) {
			sb.append(array[i]);
			if(i < array.length-1) {
				sb.append(", ");
			}
		}
		sb.append("]");
		return sb.toString();
	}

	// TODO this implementation does not consider duplicates
	public static <T> boolean equalsIgnoreOrder(T[] a, T[] b) {
		Checker.checkNull(a, "a");
		Checker.checkNull(b, "b");
		if(a.length != b.length) {
			return false;
		}
		Set<T> set = new HashSet<>();
		set.addAll(java.util.Arrays.asList(a));
		for(int i=0; i<b.length; i++) {
			if(!set.remove(b[i])) {
				return false;
			}
		}
		return set.isEmpty();
	}
}