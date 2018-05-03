package vy.phoebe.estimator;

import java.util.ArrayList;
import java.util.List;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.RModel;
import vy.phoebe.regression.RModelList;
import vy.phoebe.regression.RModelListEx;
import vy.phoebe.util.DSUtil;
import vy.phoebe.util.Pair;

public abstract class BasicEstimator extends Estimator {

	public BasicEstimator() {
		super();
	}

	
	public BasicEstimator(
			Dataset dataset, 
			List<String> regressors) {
		super(dataset, regressors);
	}


	@Override
	public RModelList chooseBestModels(
			RModelList inputModelList) {
		
		// Sorting according to fitness (descending)
		ArrayList<Pair<RModel>> pairList = 
			new ArrayList<Pair<RModel>>();
		for (RModel model : inputModelList) {
			Pair<RModel> pair = 
				new Pair<RModel>(model, model.getFitness());
			pairList.add(pair);
		}
		if (this.numberOfPossible == 0)
			Pair.sort(pairList, true);
		else
			Pair.sort(pairList, this.numberOfPossible, true);
		
		if (pairList.size() == 0)
			return new RModelListEx();

		return new RModelListEx(Pair.getKeyList(pairList));
	}
	
	
	@Override
	public boolean fitModel(RModel model) {
		double fitnessThreshold = getFitnessThreshold();
		if (DSUtil.isUsed(fitnessThreshold)
				&& model.getFitness() < fitnessThreshold)
			return false;
		
		return true;
	}
	

}
