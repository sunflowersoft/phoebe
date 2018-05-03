package vy.phoebe.regression;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.math.MathUtil;
import vy.phoebe.regression.ui.graph.Graph;
import vy.phoebe.regression.ui.graph.PlotGraphExt;
import vy.phoebe.regression.ui.graph.RModelMultiVarGraph;
import vy.phoebe.util.Constants;
import vy.phoebe.util.DSUtil;
import flanagan.analysis.Regression;
import flanagan.analysis.Stat;
import flanagan.math.Fmath;
import flanagan.plot.PlotGraph;

public class RModelAssoc {

	
	private RModel model = null;
	
	
	public RModelAssoc(RModel model) {
		this.model = model;
	}
	
	
	public static String prettySpec(String spec) {
		spec = spec.
			replaceAll("\\+", " + ").
			replaceAll("-", " - ").
			replaceAll("\\*", " * ");
		
		if (spec.contains("= - "))
			spec = spec.replace("= - ", " = -");
		else
			spec = spec.replaceAll("=", " = ");
		
		spec = spec.replaceAll("\\( - ", "(-");
			
		return spec;
	}

	
	public static String finetune(String rmDesc) {
		// RModelParserImpl uses to parse
		
		rmDesc = rmDesc.replaceAll("\\s", "");
		rmDesc = rmDesc.replaceAll(RModel.BEGIN_CHOSEN_SIGN, "");
		rmDesc = rmDesc.replaceAll(RModel.END_CHOSEN_SIGN, "");
		rmDesc = rmDesc.replaceAll("\\+-", "-");
		rmDesc = rmDesc.replaceAll("-\\+", "-");
		rmDesc = rmDesc.replaceAll("-+", "-");
		rmDesc = rmDesc.replaceAll("\\++", "+");
		rmDesc = rmDesc.replaceAll("\\(\\+", "(");
		rmDesc = rmDesc.replaceAll("\\*+", "*");
		rmDesc = rmDesc.replaceAll("\\{\\[\\]]", "");
		
		return rmDesc;
	}

	
	public static String[] getSpecAndType(String rmDesc) {
		String type = "";
		String spec = "";
		
		rmDesc = finetune(rmDesc);
		
		List<String> a_desc = DSUtil.split(rmDesc, RModel.DESC_DELIMITER_REGEX, null);
		if (a_desc.size() == 0)
			return new String[] {"", ""};
		
		if (a_desc.size() == 1) {
			spec = a_desc.get(0);
			type = "";
		}
		else {
			spec = a_desc.get(0);
			List<String> types = DSUtil.split(a_desc.get(1), "=", null);
			if(types.size() == 0)
				type = "";
			else if (types.size() == 1)
				type = types.get(0);
			else
				type = types.get(1);
		}
		
		return new String[] {spec, type};
	}

	
	public static HashMap<String, Object> parseDesc(String rmDesc) {
		HashMap<String, Object> partList = new HashMap<String, Object>();
		rmDesc = finetune(rmDesc);
		
		String[] a_desc = rmDesc.split(RModel.DESC_DELIMITER_REGEX);
		if (a_desc.length == 0)
			return partList;
		
		for (String part : a_desc) {
			String[] pair = part.split("=");
			if (pair.length < 2)
				continue;
			
			String attr = pair[0];
			String value = pair[1];
			if (attr.isEmpty() || value.isEmpty())
				continue;
			
			if (attr.equals("fitness") || 
					attr.equals("error")) {
				Double number = Double.parseDouble(value);
				partList.put(attr, number);
			}
			partList.put(attr, value);
		}
		
		return partList;
	}
	
	
	private static boolean testDataset(Dataset dataset) {
		return (dataset != null && dataset.getRows() > 0);
	}
	
	
	public double[] getYdata(Dataset dataset) {
		if (!testDataset(dataset))
			return new double[0];
		
		if (!dataset.contains(this.model.getResponse()))
			return new double[0];
			
		return dataset.getMeasureArrayByName(this.model.getResponse());
	}
	
	
	public double[] getYcalc(Dataset dataset) {
		if (!testDataset(dataset))
			return new double[0];

		List<String> regressors = this.model.getRegressorNames();
		if (!dataset.containsAll(regressors))
			return new double[0];
		
		int n = dataset.getRows();
		double[] ycalc = new double[n];
		for (int i = 0; i < n; i++) {
			HashMap<String, Double> regressorValues = new HashMap<String, Double>();
			
			for (String regressor : regressors) {
				double value = dataset.getMeasure(i, regressor);
				regressorValues.put(regressor, value);
			}
			
			ycalc[i] = model.eval(regressorValues);
		}
		
		return ycalc;
	}
	
	
	public double getSumOfSquares(Dataset dataset) {
		if (!testDataset(dataset))
			return Constants.UNUSED;

		double[] y = getYdata(dataset);
		double[] ycalc = getYcalc(dataset);
		int n = y.length;
		if (n == 0)
			return Constants.UNUSED;

		double sumOfSquares = 0;
		for (int i = 0; i < n; i++){
			sumOfSquares += Fmath.square(ycalc[i] - y[i]);
		}
		
		return sumOfSquares;
	}
	
	
	public double getMean(Dataset dataset) {
		if (!testDataset(dataset))
			return Constants.UNUSED;
		
		double[] ycalc = getYcalc(dataset);
		return Stat.mean(ycalc);
	}
	
	
	public double getSd(Dataset dataset) {
		if (!testDataset(dataset))
			return Constants.UNUSED;
		
		double[] ycalc = getYcalc(dataset);
		return Stat.standardDeviation(ycalc);
	}

	
	public double[] getError(Dataset dataset) {
		if (!testDataset(dataset))
			return new double[0];

		double[] y = getYdata(dataset);
		double[] ycalc = getYcalc(dataset);
		int n = y.length;
		if (n == 0)
			return new double[0];

		double[] errList = new double[n];
		for (int i = 0; i < n; i++){
			errList[i] = (ycalc[i] - y[i]);
		}
		
		return errList; 
	}
	
	
	public double getErrMean(Dataset dataset) {
		if (!testDataset(dataset))
			return Constants.UNUSED;
		
		double[] err = getError(dataset);
		if (err.length == 0)
			return Constants.UNUSED;
		
		return Stat.mean(err);
	}
	
	
	public double getErrSd(Dataset dataset) {
		if (!testDataset(dataset))
			return Constants.UNUSED;

		double[] err = getError(dataset);
		if (err.length == 0)
			return Constants.UNUSED;
		
		return Stat.standardDeviation(err);
	}
	
	
	public double[] getRatioErr(Dataset dataset) {
		if (!testDataset(dataset))
			return new double[0];

		double[] y = getYdata(dataset);
		double[] ycalc = getYcalc(dataset);
		int n = y.length;
		if (n == 0)
			return new double[0];

		double[] errList = new double[n];
		for (int i = 0; i < n; i++){
			if (y[i] == 0)
				continue;
			
			double deviation = (ycalc[i] - y[i]) / y[i];
			errList[i] = deviation;   
			
		}
		
		return errList; 
	}
	
	
	public double getRatioErrMean(Dataset dataset) {
		if (!testDataset(dataset))
			return Constants.UNUSED;
		
		double[] err = getRatioErr(dataset);
		if (err.length == 0)
			return Constants.UNUSED;
		
		return Stat.mean(err);
	}
	
	
	public double getRatioErrSd(Dataset dataset) {
		if (!testDataset(dataset))
			return Constants.UNUSED;

		double[] err = getRatioErr(dataset);
		if (err.length == 0)
			return Constants.UNUSED;
		
		return Stat.standardDeviation(err);
	}

	
	public double getR(Dataset dataset) {
		if (!testDataset(dataset))
			return Constants.UNUSED;

		double[] y = getYdata(dataset);
		double[] ycalc = getYcalc(dataset);
		if (y.length == 0)
			return Constants.UNUSED;
		
		return Stat.corrCoeff(y, ycalc);
	}
	
	
	public double getPvalue(Dataset dataset) {
		if (!testDataset(dataset))
			return Constants.UNUSED;

		return Constants.UNUSED;
	}
	
	
    public ArrayList<Graph> getGraphList(Dataset dataset) {
    	ArrayList<Graph> graphList = new ArrayList<Graph>();
    	
    	Graph multivariate = RModelMultiVarGraph.create(model, dataset);
    	if (multivariate != null)
    		graphList.add(multivariate);

    	Graph estimate = createEstiGraph(dataset);
    	if (estimate != null)
    		graphList.add(estimate);
    	
    	Graph diff = createErrGraph(dataset);
    	if (diff != null)
    		graphList.add(diff);
    	
    	Graph ratioDiff = createRatioDiffGraph(dataset);
    	if (ratioDiff != null)
    		graphList.add(ratioDiff);
    	
    	return graphList;
    }

    
    public Graph createEstiGraph(Dataset dataset) {
		if (!testDataset(dataset))
			return null;
    	
    	int ncurves = 2;
    	int npoints = dataset.getRows();
    	double[][] data = PlotGraph.data(ncurves, npoints);
    	double[] y = getYdata(dataset);
    	double[] ycalc = getYcalc(dataset);

    	for(int i = 0; i < npoints; i++) {
            data[0][i] = y[i];
            data[1][i] = ycalc[i];
        }

    	Regression regression = new Regression(y, ycalc);
    	regression.linear();
    	double[] coef = regression.getCoeff();
    	data[2][0] = Fmath.minimum(y);
    	data[3][0] = coef[0] + coef[1] * data[2][0];
    	data[2][1] = Fmath.maximum(y);
    	data[3][1] = coef[0] + coef[1] * data[2][1];

    	PlotGraphExt pg = new PlotGraphExt(data) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getGraphFeature() {
				// TODO Auto-generated method stub
				return "R=" + MathUtil.format(model.getR(), 2);
			}
    		
    	};

    	String response = model.getResponse();
    	pg.setGraphTitle("Correlation plot:" + pg.getGraphFeature());
    	pg.setXaxisLegend("Real " + response);
    	pg.setYaxisLegend("Estimated " + response);
    	int[] popt = {1, 0};
    	pg.setPoint(popt);
    	int[] lopt = {0, 3};
    	pg.setLine(lopt);

    	pg.setBackground(Color.WHITE);
        return pg;
    }
	

    public Graph createErrGraph(Dataset dataset) {
		if (!testDataset(dataset))
			return null;
    	
    	int ncurves = 4;
    	int npoints = dataset.getRows();
    	double[][] data = PlotGraph.data(ncurves, npoints);
    	double[] y = getYdata(dataset);
    	double[] ycalc = getYcalc(dataset);

    	for(int i = 0; i < npoints; i++) {
            data[0][i] = ( y[i] + ycalc[i] ) / 2.0;
            data[1][i] = ycalc[i] - y[i];
        }
    	
    	// Mean - 1.96sd
    	data[2][0] = 0;
    	data[3][0] = model.getErrMean() - 1.96 * model.getErrSd();
    	data[2][1] = Fmath.maximum(data[0]);
    	data[3][1] = model.getErrMean() - 1.96 * model.getErrSd();

    	// Mean
    	data[4][0] = 0;
    	data[5][0] = model.getErrMean();
    	data[4][1] = Fmath.maximum(data[0]);
    	data[5][1] = model.getErrMean();

    	// Mean + 1.96sd
    	data[6][0] = 0;
    	data[7][0] = model.getErrMean() + 1.96 * model.getErrSd();
    	data[6][1] = Fmath.maximum(data[0]);
    	data[7][1] = model.getErrMean() + 1.96 * model.getErrSd();

    	PlotGraphExt pg = new PlotGraphExt(data) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getGraphFeature() {
				// TODO Auto-generated method stub
				return MathUtil.format(model.getErrMean(), 2) + " +/- 1.96*" + 
    				MathUtil.format(model.getErrSd(), 2);
			}
    		
    	};

    	String response = model.getResponse();
    	pg.setGraphTitle("Error plot:" + pg.getGraphFeature());
    	
    	pg.setXaxisLegend("Mean " + response);
    	pg.setYaxisLegend("Estimate error");
    	int[] popt = {1, 0, 0, 0};
    	pg.setPoint(popt);
    	int[] lopt = {0, 3, 3, 3};
    	pg.setLine(lopt);

    	pg.setBackground(Color.WHITE);
    	
        return pg;
    }

    
    public Graph createRatioDiffGraph(Dataset dataset) {
		if (!testDataset(dataset))
			return null;
    	
    	int ncurves = 4;
    	int npoints = dataset.getRows();
    	double[][] data = PlotGraph.data(ncurves, npoints);
    	double[] y = getYdata(dataset);
    	double[] ycalc = getYcalc(dataset);

    	for(int i = 0; i < npoints; i++) {
            data[0][i] = ( y[i] + ycalc[i] ) / 2.0;
            
			double deviation = ycalc[i] / y[i];
            data[1][i] = deviation > 1 ? (deviation - 1) : (1 - deviation);
        }
    	
    	// Mean - 1.96sd
    	data[2][0] = 0;
    	data[3][0] = (model.getRatioErrMean() - 1.96 * model.getRatioErrSd()) * 100.0;
    	data[2][1] = Fmath.maximum(data[0]);
    	data[3][1] = (model.getRatioErrMean() - 1.96 * model.getRatioErrSd()) * 100.0;

    	// Mean
    	data[4][0] = 0;
    	data[5][0] = model.getRatioErrMean() * 100.0;
    	data[4][1] = Fmath.maximum(data[0]);
    	data[5][1] = model.getRatioErrMean() * 100.0;

    	// Mean + 1.96sd
    	data[6][0] = 0;
    	data[7][0] = (model.getRatioErrMean() + 1.96 * model.getRatioErrSd()) * 100.0;
    	data[6][1] = Fmath.maximum(data[0]);
    	data[7][1] = (model.getRatioErrMean() + 1.96 * model.getRatioErrSd()) * 100.0;

    	PlotGraphExt pg = new PlotGraphExt(data) {

			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getGraphFeature() {
				// TODO Auto-generated method stub
				return MathUtil.format(model.getRatioErrMean() * 100.0, 2) + " +/- 1.96*" + 
		    			MathUtil.format(model.getRatioErrSd() * 100.0, 2);
			}
    		
    	};

    	String response = model.getResponse();
    	pg.setGraphTitle("Error plot (%):" + pg.getGraphFeature());
    	pg.setXaxisLegend("Mean " + response);
    	pg.setYaxisLegend("Estimate ratio error (%)");
    	int[] popt = {1, 0, 0, 0};
    	pg.setPoint(popt);
    	int[] lopt = {0, 3, 3, 3};
    	pg.setLine(lopt);

    	pg.setBackground(Color.WHITE);

        return pg;
    }

    
	
	public int countRegressorsExist(Collection<String> regressors) {
		int count = 0;
		ArrayList<String> regressorNames = model.getRegressorNames();
		for (String regressor : regressorNames) {
			if (regressors.contains(regressor))
				count ++;
			else
				count --;
		}
		
		return count;
	}

	
	
	
	
}
