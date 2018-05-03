/**
 * 
 */
package vy.phoebe.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import flanagan.analysis.Stat;

/**
 * @author Loc Nguyen
 * @version 2.0
 *
 */
public class MinMax extends Dual {

	
	/**
	 * 
	 * @param min
	 * @param max
	 */
	public MinMax(double min, double max) {
		super(min, max);
	}
	
	
	/**
	 * 
	 * @return mim value
	 */
	public double min() {
		return one();
	}


	/**
	 * 
	 * @return max value
	 */
	public double max() {
		return other();
	}
	
	
	/**
	 * 
	 * @param min
	 */
	public void setMin(double min) {
		this.one_ = min;
	}
	
	
	/**
	 * 
	 * @param max
	 */
	public void setMax(double max) {
		this.other_ = max;
	}
	
	
	public double range() {
		return max() - min();
	}
	
	
	public boolean contains(double value) {
		return min() <= value && value <= max();
	}
	
	
	public double[] normalize(double[] data) {
		if (data == null || data.length == 0)
			return new double[] { };
		
		double range = range();
		double min = min();
		double[] normalizedData = new double[data.length];
		Arrays.fill(normalizedData, 0);
		if (range == 0)
			return normalizedData;
		
		for (int i = 0; i < data.length; i++)
			normalizedData[i] = (data[i] - min) / range;
		
		return normalizedData;
	}
	
	
	public static MinMax create(double[] domain) {
		Stat stat = new Stat(domain);
		return new MinMax(stat.minimum(), stat.maximum());
	}
	
	
	public double[] createRange(int steps) {
		return createRange(min(), max(), steps);
	}
	
	
	public static double[] createRange(double min, double max, int steps) {
		double interval = (max - min) / (double)steps;
		min = Math.max(min, interval);
		
		List<Double> list = new ArrayList<Double>();
		for (int i = 0; i <= steps; i++) {
			double value = min + i * interval;
			if (value > max)
				break;
			
			list.add(value);
		}
		
		double[] array = new double[list.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = list.get(i);
		
		return array;
	}
	
	
	
}
