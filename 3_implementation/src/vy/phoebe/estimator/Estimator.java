package vy.phoebe.estimator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.RModelGroupList;
import vy.phoebe.regression.RModel;
import vy.phoebe.regression.RModelList;
import vy.phoebe.regression.RModelListEx;
import vy.phoebe.util.Constants;


public abstract class Estimator {

	protected List<String> regressors = new ArrayList<String>();
	
	
	protected Dataset dataset = null; 

	
	protected double independentThreshold = Constants.UNUSED;

	
	protected double fitnessThreshold = Constants.UNUSED;
	
	
	protected int numberOfPossible = 5; 
	

	public Estimator() {
		
	}
	
	
	public Estimator(Dataset dataset, List<String> regressors) {
		this.dataset = dataset;
		this.setRegressor(regressors);
	}
	
	
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	
	
	public void setRegressor(List<String> regressors) {
		this.regressors.clear();
		this.regressors.addAll(regressors);
	}
	

	public void setParameters(
			double independentThreshold, 
			double fitThreshold,
			int numberOfPossible) {
		this.independentThreshold = independentThreshold;
		this.fitnessThreshold = fitThreshold;
		this.numberOfPossible = numberOfPossible; 
	}
	
	
	public double getFitnessThreshold() {
		return fitnessThreshold;
	}
	
	
	protected RModelList fitModel(Collection<RModel> modelList) {
		RModelList result = new RModelListEx();
		for (RModel model : modelList) {
			if (model != null && fitModel(model)) 
				result.add(model);
		}
		
		return result;
	}

	
	public RModelGroupList estimate(
			List<String> responses, 
			int maxChosenNumber) throws Exception {
		
		RModelGroupList result = new RModelGroupList();
		
		for (String response : responses) {
			RModelList bestModels = estimate(response);
			result.add(bestModels);
		}
		
		result.makeCommonChosen(maxChosenNumber);
		return result;
	}
	
	
	@Override
	public String toString() {
		return getDesc();
	}
	
	
	public abstract RModelList estimate(String response) throws Exception;
	
	
	public abstract RModelList chooseBestModels(
			RModelList inputModelList);

	
	public abstract boolean fitModel(RModel model);
	

	public abstract String getName();


	public abstract String getDesc();
}


