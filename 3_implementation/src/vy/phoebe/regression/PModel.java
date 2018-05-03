package vy.phoebe.regression;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import flanagan.analysis.Stat;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.util.DSUtil;

public class PModel {
	
	protected static double EPSILON = 0.0001f;
	
	
	protected double percent = 0;

	
	protected double[] xData = null;
	
	
	protected double[] yData = null;
	
	
	private PModel(double percent, double[] xData, double[] yData) {
		this.percent = percent;
		this.xData = xData;
		this.yData = yData;
	}
	
	
	public double[] getXData() {
		return xData;
	}
	
	
	public double[] getYData() {
		return yData;
	}

	
	public static PModel noneNormalCreate(
			Dataset dataset, 
			String regressor, 
			String response, 
			double percent,
			boolean inverse) {
		
		if (dataset == null || !dataset.contains(regressor) || !dataset.contains(response))
			return null;
		
		double[] xDataTemp = dataset.getMeasureSortedArrayByName(regressor);
		double[] yDataTemp = dataset.getMeasureSortedArrayByName(response);
		if (xDataTemp == null || xDataTemp.length == 0 || yDataTemp == null || yDataTemp.length == 0)
			return null;
		
		List<Double> xList = new ArrayList<Double>();
		List<Double> yList = new ArrayList<Double>();
		for (int i = 0; i < xDataTemp.length; i++) {
			double x = xDataTemp[i];
			
			int n = 0;
			for (int k = 0; k < dataset.getRows(); k++) {
				double xValue = dataset.getMeasure(k, regressor);
				if (xValue == x)
					n++;
			}
			
			for (int j = 0; j < yDataTemp.length; j++) {
				double y = yDataTemp[j];
				
				int count = 0;
				double yFound = Double.NaN;
				for (int k = 0; k < dataset.getRows(); k++) {
					double xValue = dataset.getMeasure(k, regressor);
					double yValue = dataset.getMeasure(k, response);
					
					if (xValue == x && (inverse ? yValue >= y : yValue <= y) ) {
						
						count++;
						double conditionProb = (double)count / (double)n;
						if (conditionProb >= percent ) { // or deviation less than epsilon
							yFound = y;
							break; // break k loop
						}
					}
				}
				
				if (!Double.isNaN(yFound)) {
					xList.add(x);
					yList.add(yFound);
					break; // break j loop
					
				}
					
			} // j
			
			
		} // i
		
		if (xList.size() == 0)
			return null;
		
		
		return new PModel(percent, DSUtil.toArray(xList), DSUtil.toArray(yList));
	}
	
	
	public static PModel normalCreate(
			Dataset dataset, 
			String regressor, 
			String response, 
			double percent) {
		
		if (dataset == null || !dataset.contains(regressor) || !dataset.contains(response))
			return null;
		
		double[] xTemp = dataset.getMeasureSortedArrayByName(regressor);
		if (xTemp == null || xTemp.length == 0)
			return null;
		
		double[] yArray = new double[xTemp.length];
		
		double z = Stat.inverseNormalCDF(0, 1, percent);
		for (int j = 0; j < xTemp.length; j++) {
			double x = xTemp[j];
			
			ArrayList<Double> yList = new ArrayList<Double>();
			for (int i = 0; i < dataset.getRows(); i++) {
				double xValue = dataset.getMeasure(i, regressor);
				double yValue = dataset.getMeasure(i, response);
				if (xValue == x)
					yList.add(yValue);
			}
			
			double[] yTemp = DSUtil.toArray(yList);
			Stat stat = new Stat(yTemp);
			double mean = stat.mean();
			double sd = stat.standardDeviation();
			stat.setDenominatorToN();
			sd = stat.standardDeviation();
			yArray[j] = mean + z * sd;
			
		}
		
		return new PModel(percent, xTemp, yArray);
	}


	public static PModel normalCreate(
			Dataset dataset, 
			RModel rmodel, 
			double[] xDomain,
			double percent) {
		if (rmodel == null || xDomain == null || xDomain.length == 0 || rmodel.getRegressorNames().size() != 1)
			return null;
		
		RModelAssoc assoc = new RModelAssoc(rmodel);
		
		// Note that removing df effect instead of n - 2
		double estiSd = Math.sqrt(assoc.getSumOfSquares(dataset) / dataset.getRows());
		double z = Stat.inverseNormalCDF(0, 1, percent);
		double[] yArray = new double[xDomain.length];
		
		String regressor = rmodel.getRegressorNames().get(0);
		for (int i = 0; i < xDomain.length; i++) {
			HashMap<String, Double> param = new HashMap<String, Double>();
			param.put(regressor, xDomain[i]);
			// The essence of regression model is mean estimation
			double mean = rmodel.eval(param);
			yArray[i] = mean + z * estiSd;
		}
		
		return new PModel(percent, xDomain, yArray);
	}
	
	
	
	
}
