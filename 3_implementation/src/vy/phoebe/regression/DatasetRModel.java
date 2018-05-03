package vy.phoebe.regression;

import java.util.ArrayList;
import java.util.List;

import vy.phoebe.adapt.FlanaganRegression;
import vy.phoebe.dataset.Dataset;
import vy.phoebe.math.MathUtil;
import vy.phoebe.regression.ui.graph.Graph;
import vy.phoebe.util.DSUtil;

public abstract class DatasetRModel extends BasicRModel {

	
	protected Dataset dataset_ = null;
	
	
	protected double base_ = 0;
	
	
	public DatasetRModel(
			Dataset dataset,
			double[] coeffs, 
			List<String> regressors, 
			String response,
			String type,
			double base) {
		
		this.dataset_ = dataset;
		this.base_ = base;
		
		this.regressorNames_.clear();
		this.regressorNames_.addAll(regressors);
		this.response_ = response;
		
		this.setCoeffs(coeffs);
		this.setType(type);
	}

	
	public DatasetRModel(
			Dataset dataset,
			List<Double> coeffs, 
			List<String> regressors, 
			String response,
			String type,
			double base) {
		
		this(
				dataset, 
				DSUtil.toArray(coeffs), 
				regressors, 
				response, 
				type, 
				base
			);
	}

	
	public DatasetRModel(
			Dataset dataset,
			FlanaganRegression regression, 
			List<String> regressors, 
			String response,
			String type,
			double base) {
	
		this(
			dataset, 
			regression.getBestEstimates(), 
			regressors, 
			response, 
			type, 
			base
			);
		
	}
	
	
	public ArrayList<Graph> getGraphList() {
		return new RModelAssoc(this).getGraphList(dataset_);
	}
	

	public void complete() {
		if (dataset_ == null)
			return;
		
		RModelAssoc assoc = new RModelAssoc(this);
		
		for (int i = 0; i < this.coeffs_.size(); i++)
			this.coeffs_.set(i, MathUtil.round(this.coeffs_.get(i)));
		
		this.r_ = MathUtil.round(assoc.getR(dataset_));
		this.sumOfSquares_ = MathUtil.round(assoc.getSumOfSquares(dataset_));
		this.mean_ = MathUtil.round(assoc.getMean(dataset_));
		this.sd_ = MathUtil.round(assoc.getSd(dataset_));
		this.errMean_ = MathUtil.round(assoc.getErrMean(dataset_));
		this.errSd_ = MathUtil.round(assoc.getErrSd(dataset_));
		this.ratioErrMean_ = MathUtil.round(assoc.getRatioErrMean(dataset_));
		this.ratioErrSd_ = MathUtil.round(assoc.getRatioErrSd(dataset_));
		this.pValue_ = MathUtil.round(assoc.getPvalue(dataset_));
		
	}

	
	public void setDataset(Dataset dataset) {
		if (this.dataset_ == dataset)
			return;
		
		this.dataset_ = dataset;
		this.complete();
	}
	
	
}
