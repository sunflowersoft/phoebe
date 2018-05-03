package vy.phoebe.estimator;

import java.util.ArrayList;
import java.util.List;

import vy.phoebe.Config;
import vy.phoebe.dataset.Dataset;
import vy.phoebe.math.CombinationGenerator;
import vy.phoebe.regression.RModelFactory;
import vy.phoebe.regression.RModelList;
import vy.phoebe.regression.RModelListEx;

public class BruteForceEstimator extends BasicEstimator {

	public BruteForceEstimator() {
		super();
	}

	
	public BruteForceEstimator(Dataset dataset, List<String> regressors) {
		super(dataset, regressors);
	}

	
	@Override
	public RModelList estimate(String response)
			throws Exception {
		RModelList result = new RModelListEx();
		
		ArrayList<String> inputRegressorList = new ArrayList<String>(this.regressors);
		inputRegressorList.remove(response);
		
		RModelFactory factory = Config.getRModelFactory();
		factory.setDataset(dataset);
		
		int n = inputRegressorList.size();
		for (int i = 1; i <= n; i++) {
			CombinationGenerator gen = new CombinationGenerator (n, i);
			while (gen.hasMore ()) {
				int[] indices = gen.getNext ();
				
				ArrayList<String> newRegressorList = new ArrayList<String>();
				for (int j = 0; j < indices.length; j++) {
					newRegressorList.add(inputRegressorList.get(indices[j]));
				}
				
				RModelList models = factory.createModelList(newRegressorList, response);
				RModelList fitModels = fitModel(models);
				result.addAll(fitModels);
				result = chooseBestModels(result);
			}
			
		}
		
		result.setResponse(response);
		return result;
	}

	
	public String getName() {
		return "bf";
	}

	
	public String getDesc() {
		return "Brute Force (default)";
	}
	
}
