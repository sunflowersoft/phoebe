package vy.phoebe.regression;

import java.util.ArrayList;

import vy.phoebe.math.MathUtil;
import vy.phoebe.util.Constants;
import vy.phoebe.util.DSUtil;

public abstract class BasicRModel implements RModel {
	
	protected String type_ = "";
	
	
	protected String response_ = "";
	
	
	protected ArrayList<String> regressorNames_ = new ArrayList<String>();

	
	protected ArrayList<Double> coeffs_ = new ArrayList<Double>();
	
	
	protected double r_ = Constants.UNUSED;

	
	protected double sumOfSquares_ = Constants.UNUSED;
	
	
	protected double mean_ = Constants.UNUSED;
	
	
	protected double sd_ = Constants.UNUSED;

	
	protected double errMean_ = Constants.UNUSED;
	
	
	protected double errSd_ = Constants.UNUSED;
	
	
	protected double ratioErrMean_ = Constants.UNUSED;
	
	
	protected double ratioErrSd_ = Constants.UNUSED;

	
	protected double pValue_ = Constants.UNUSED;

	
	protected BasicRModel() {
		
	}

	
	@Override
	public double getR() {
		return r_;
	}
	
	
	@Override
	public double getFitness() {
		return getR();
	}
	
	
	@Override
	public double getMean() {
		return mean_;
	}
	
	
	@Override
	public double getSd() {
		return sd_;
	}
	
	
	@Override
	public double getPvalue() {
		return pValue_;
	}

	
	@Override
	public String toString() {
		String desc = "";
		desc += RModelAssoc.prettySpec(getSpec()) + " : ";
		desc += "type = " + getType() + " : ";
		desc += "fitness = " + MathUtil.format(getFitness()) + " : ";
		desc += "r = " + MathUtil.format(getR()) + " : ";
		desc += "ss = " + MathUtil.format(getSumOfSquares()) + " : ";
		desc += "mean = " + MathUtil.format(getMean()) + " : ";
		desc += "sd = " + MathUtil.format(getSd()) + " : ";
		desc += "errMean = " + MathUtil.format(getErrMean()) + " : ";
		desc += "errSd = " + MathUtil.format(getErrSd()) + " : ";
		desc += "ratioErrMean = " + MathUtil.format(getRatioErrMean() * 100.0) + "% : ";
		desc += "ratioErrSd = " + MathUtil.format(getRatioErrSd() * 100.0) + "% : ";
		desc += "pvalue = " + MathUtil.format(getPvalue());
		
		return desc;
	}
	
	
	@Override
	public String getResponse() {
		return response_;
	}
	
	
	@Override
	public double getSumOfSquares() {
		return sumOfSquares_;
	}
	
	
	@Override
	public double getErrMean() {
		return errMean_;
	}
	
	
	@Override
	public double getErrSd() {
		return errSd_;
	}

	
	@Override
	public double getRatioErrMean() {
		return ratioErrMean_;
	}
	
	
	@Override
	public double getRatioErrSd() {
		return ratioErrSd_;
	}

	
	@Override
	public String getType() {
		return type_;
	}

	
	@Override
	public String setType(String type) {
		return this.type_ = type;
	}

	
	@Override
	public double[] getCoeffs() {
		return DSUtil.toArray(coeffs_);
	}

	
	@Override
	public ArrayList<String> getRegressorNames() {
		return new ArrayList<String>(this.regressorNames_);
	}

	
	protected void setRegressorNames(ArrayList<String> regressorNames) {
		this.regressorNames_.clear();
		this.regressorNames_.addAll(regressorNames);
	}

	
	protected void setCoeffs(ArrayList<Double> coeffs) {
		this.coeffs_.clear();
		this.coeffs_.addAll(coeffs);
	}

	
	protected void setCoeffs(double[] coeffs) {
		this.coeffs_.clear();
		for (int i = 0; i < coeffs.length; i++) {
			this.coeffs_.add(coeffs[i]);
		}
	}
	
	
	@Override
	public String getNiceForm() {
		return getSpec();
	}



	
}

