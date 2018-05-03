package vy.phoebe.regression;

import java.util.List;

import vy.phoebe.dataset.Dataset;

public abstract class RModelFactory {


	protected Dataset dataset = null;
	
	
	public RModelFactory() {
	}

	
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	
	
	public abstract RModelList createModelList(
			List<String> newRegressorList, 
			String response);
	
	
}
