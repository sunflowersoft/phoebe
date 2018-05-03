package vy.phoebe.math;

import java.awt.Point;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import vy.phoebe.Config;
import vy.phoebe.util.Constants;
import vy.phoebe.util.MinMax;

public final class MathUtil {

	public static double[] sum(double[][] data, int order) {
		int n = data[0].length;
		double[] result = new double[n];
		for (int i = 0; i < n; i++) {
			result[i] = 0;
			for (int j = 0; j < data.length; j++)
				result[i] += data[j][i];
			
			result[i] = Math.pow(result[i], order);
		}
		
		return result;
	}

	
	public static double logarit(double value, double base) {
		if (base == Math.E)
			return Math.log(value);
		else if (base == 10)
			return Math.log10(value);
		else
			return Constants.UNUSED;
			
	}
	
	
	public static double[] logarit(double[] data, double base) {
		double[] result = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			if (base == Math.E)
				result[i] = Math.log(data[i]);
			else if (base == 10)
				result[i] = Math.log10(data[i]);
		}
		
		return result;
	}

	
	public static double[][] logarit(double[][] data, double base) {
		double[][] result = new double[data.length][];
		
		for (int i = 0; i < data.length; i++) {
			result[i] = logarit(data[i], base);
		}
		
		return result;
	}
	
	
	public static double[] pow(double[] data, double base) {
		double[] result = new double[data.length];
		for (int i = 0; i < data.length; i++) {
			result[i] = Math.pow(base, data[i]);
		}
		
		return result;
	}

	
	public static String logaritName(double base) {
		if (base == Math.E)
			return "log";
		else if (base == 10)
			return "log10";
		else
			return "log" + MathUtil.format(base);
	}
	
	
	public static String powName(double base) {
		if (base == Math.E)
			return "exp";
		else
			return "" + MathUtil.format(base) + "^";
	}

	
	public static <T> List<Set<T>> cartesianProduct(List<Set<T>> list) {
	    List<Iterator<T>> iterators = new ArrayList<Iterator<T>>(list.size());
	    List<T> elements = new ArrayList<T>(list.size());
	    List<Set<T>> toRet = new ArrayList<Set<T>>();
	    for (int i = 0; i < list.size(); i++) {
	            iterators.add(list.get(i).iterator());
	            elements.add(iterators.get(i).next());
	    }
	    for (int j = 1; j >= 0;) {
	            toRet.add(new HashSet<T>(elements));
	            for (j = iterators.size()-1; j >= 0 && !iterators.get(j).hasNext(); j--) {
	                    iterators.set(j, list.get(j).iterator());
	                    elements.set(j, iterators.get(j).next());
	            }
	            elements.set(Math.abs(j), iterators.get(Math.abs(j)).next());
	    }
	    return toRet;
	}


	public static String format(double number, int decimal) {
		String format = null;
		if (decimal > 0)
			format = "#.";
		else
			format = "#";
			
		for (int i = 0; i < decimal; i++)
			format += "#";
		
		DecimalFormat df = new DecimalFormat(format);
		return df.format(number);
	}


	public static String format(double number) {
		if (Double.isNaN(number))
			return "NaN";
		
		return format(number, Config.DECIMAL_PRECISION);
	}


	public static double round(double number, int n) {
		if (Double.isNaN(number))
			return number;
		return Double.parseDouble(format(number, n));
	}


	public static double round(double number) {
		if (Double.isNaN(number))
			return number;
		return Double.parseDouble(format(number));
	}


	public static int map(double value, MinMax valueRange, MinMax pixelRange) {
		double mapped = value * pixelRange.range() / valueRange.range() - 
				(valueRange.min() * pixelRange.max() - valueRange.max() * pixelRange.min()) / valueRange.range();
		
		return (int)mapped;
	}


	public static Point translate(
			Rectangle box,
			double xValue, 
			double yValue,
			MinMax xValueRange, 
			MinMax yValueRange) {
		
		MinMax xPixelRange = new MinMax(0, box.width);
		MinMax yPixelRange = new MinMax(0, box.height);
		
		int intX = map(xValue, xValueRange, xPixelRange);
		int intY = box.height - map(yValue, yValueRange, yPixelRange);
		
		return new Point(box.x + intX, box.y + intY);
	}

	
	
	
}
