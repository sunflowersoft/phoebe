package vy.phoebe.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import vy.phoebe.math.MathUtil;

public final class DSUtil {

	public static <T> List<T> newList() {
	    return new ArrayList<T>();
	}

	
	@SuppressWarnings("unused")
	@Deprecated
	private static <T> boolean contains(
			Collection<T> container, 
			Collection<T> child) {
		
		for (T obj : child) {
			if (!container.contains(obj))
				return false;
		}
		
		return true;
	}

	
	@SuppressWarnings("unused")
	@Deprecated
	private static boolean contains2(
			Collection<ArrayList<String>> container, 
			ArrayList<String> child) {
		
		for (ArrayList<String> array : container) {
			if (equals(child, array))
				return true;
		}
		
		return false;
	}

	
	@Deprecated
	private static <T> boolean equals(
			Collection<T> container, 
			Collection<T> child) {
		if (container.size() != child.size())
			return false;
		
		for (T obj : child) {
			if (!container.contains(obj))
				return false;
		}
		
		return true;
	}

	
	@SuppressWarnings("unused")
	@Deprecated
	private static <T extends ArrayList<String>> boolean collectionContains(
			Collection<T> container, Collection<T> child) {
		
		for (T obj1 : child) {
			for (T obj2 : container) {
				if (!equals(obj1, obj2))
					return false;
			}
		}
		
		return true;
	}

	
	@Deprecated
	private static <T> void compact(
			Collection<T> collection, 
			Collection<T> result) {
		
		result.clear();
		for (T element : collection) {
			if (element != null && !result.contains(element))
				result.add(element);
		}
	}

	
	@SuppressWarnings("unused")
	@Deprecated
	private ArrayList<ArrayList<String>> union(Collection<ArrayList<String>> array1,
			Collection<ArrayList<String>> array2) {
		
		ArrayList<ArrayList<String>> result = new ArrayList<ArrayList<String>>();
		result.addAll(array1);
		result.addAll(array2);
		
		return result;
	}
	
	
	public static <T extends Object> void complement(
			Collection<T> container, 
			Collection<T> child,
			Collection<T> result) {
		
		result.clear();
		
		for (T element : container) {
			if (!child.contains(element))
				result.add(element);
		}
	}
	
	
	@SuppressWarnings("unused")
	@Deprecated
	private static <T> void union(
			Collection<T> collection1, 
			Collection<T> collection2,
			Collection<T> result) {
		
		ArrayList<T> list = new ArrayList<T>();
		list.addAll(collection1);
		list.addAll(collection2);
		
		result.clear();
		DSUtil.compact(list, result);
	}
	
	
	public static double[] toArray(List<Double> list) {
		int n = list.size();
		
		double[] array = new double[n];
		for (int i = 0; i < n; i++)
			array[i] = list.get(i);
		
		return array;
	}

	
	public static StringBuffer toDataColBuffer(double[] datarow) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < datarow.length; i++) {
			if (i > 0)
				buffer.append("\n");
			buffer.append(MathUtil.format(datarow[i]));
		}
		
		return buffer;
	}
	
	
	public static StringBuffer toDataRowBuffer(double[] datarow) {
	
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < datarow.length; i++) {
			if (i > 0)
				buffer.append(", ");
			buffer.append(MathUtil.format(datarow[i]));
		}
		
		return buffer;
	}
	
	
	public static boolean isUsed(double value) {
		return (!(Double.isNaN(value))) && (value != Constants.UNUSED);
	}


	public final static List<String> split(String source, String sep, String remove) {
		String[] array = source.split(sep);
		
		List<String> result = newList();
		for (String str : array) {
			if (str == null) continue;
			
			if (remove != null && remove.length() > 0)
				str = str.replaceAll(remove, "");
			str = str.trim();
			if (str.length() > 0)
				result.add(str);
		}
		
		return result;
	}


	public static <T extends Object> String toText(T[] array, String sep) {
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < array.length; i++) {
			if ( i > 0)
				buffer.append(sep + " ");

			T value = array[i];
			buffer.append(value);
		}
		
		return buffer.toString();
		
	}

	
	/**
	 * 
	 * @param list
	 * @param sep
	 * @return text form of collection
	 */
	public static <T extends Object> String toText(Collection<T> list, String sep) {
		StringBuffer buffer = new StringBuffer();
		
		int i = 0;
		for (T value : list) {
			if (i > 0)
				buffer.append(sep + " ");
			
			buffer.append(value);
			
			i++;
		}
			
		return buffer.toString();
	}
	


}
