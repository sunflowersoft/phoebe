package vy.phoebe.estimator;

import java.util.ArrayList;
import java.util.List;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.util.Pair;

public class SeedGerminationEstimator2 extends SeedGerminationEstimator {

	public SeedGerminationEstimator2() {
		super();
	}

	
	public SeedGerminationEstimator2(Dataset dataset, List<String> regressors) {
		super(dataset, regressors);
	}

	
	@Override
	protected ArrayList<String> chooseBestRegressors(
			ArrayList<String> seedRegressorList,
			ArrayList<String> inputRegressorList, 
			String response) {
		
		// Contribution high
		ArrayList<Pair<String>> pairList = new ArrayList<Pair<String>>();
		for (String regressor : inputRegressorList) {
			double contribute = contribute(regressor, response);
			if (contribute <= 0) continue;
			
			Pair<String> pair = new Pair<String>(regressor, contribute);
			pairList.add(pair);
		}
		
		Pair.sort(pairList, this.numberOfPossible, true);
		if (pairList.size() == 0) 
			return new ArrayList<String>();
		
		return Pair.getKeyList(pairList);
	}

	
	public String getName() {
		return "sg2";
	}
	
	public String getDesc() {
		return "Seed Germination 2";
	}
	
	
}
