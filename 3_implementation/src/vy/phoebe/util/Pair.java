package vy.phoebe.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class Pair<T> implements Cloneable {
	
	private Object key = null;
	
	
	private double value = 0;
	
	
	public Pair(T key, double value) {
		this.key = key;
		this.value = value;
	}
	
	
    @SuppressWarnings("unchecked")
	public T key() {
		return (T)key;
	}
	
	
	public double value() {
		return value;
	}
	
	
	public boolean isUsed() {
		return (key != null); 
	}
	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Object clone() {
		return new Pair(key, value);
	}
	
	
	public static <T> ArrayList<T> getKeyList(List<Pair<T>> pairList) {
		ArrayList<T> keyList = new ArrayList<T>();
		for (Pair<T> pair : pairList) {
			keyList.add(pair.key());
		}
		
		return keyList;
	}
	
	
	public static <T> void sort(List<Pair<T>> pairList, int maxNumber, boolean descending) {
		sort(pairList, descending);
		
		List<Pair<T>> newPairList = new ArrayList<Pair<T>>();
		newPairList.addAll(
				pairList.subList(0, Math.min(maxNumber, pairList.size()))
				);
		
		pairList.clear();
		pairList.addAll(newPairList);
		
	}
	
	
	public static <T> void sort(List<Pair<T>> pairList, boolean descending) {
		Comparator<Pair<T>> comparator = null;
		
		if (descending) {
			comparator = new Comparator<Pair<T>>() {
					
				@Override
				public int compare(Pair<T> pair1, Pair<T> pair2) {
					double value1 = pair1.value;
					double value2 = pair2.value;
					
					if (value1 < value2)
						return 1;
					else if (value1 == value2)
						return 0;
					else
						return -1;
				}
			};
		}
		else {
			comparator = new Comparator<Pair<T>>() {
				
				@Override
				public int compare(Pair<T> pair1, Pair<T> pair2) {
					double value1 = pair1.value;
					double value2 = pair2.value;
					
					if (value1 < value2)
						return -1;
					else if (value1 == value2)
						return 0;
					else
						return 1;
				}
			};
		}
		
		Collections.sort(pairList, comparator);
		
	}
	

}

