package vy.phoebe.estimator;

import java.util.ArrayList;
import java.util.List;

import vy.phoebe.Config;
import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.RModel;
import vy.phoebe.regression.RModelFactory;
import vy.phoebe.regression.RModelList;
import vy.phoebe.regression.RModelListEx;
import vy.phoebe.util.DSUtil;
import vy.phoebe.util.Pair;
import flanagan.analysis.Stat;

public class SeedGerminationEstimator extends BasicEstimator {

	
	public SeedGerminationEstimator() {
		super();
	}

	
	public SeedGerminationEstimator(Dataset dataset,
			List<String> regressors) {
		super(dataset, regressors);
	}

	
	public RModelList estimate(String response) throws Exception {
		
		RModelFactory factory = Config.getRModelFactory();
		factory.setDataset(dataset);
		
		RModel bestModel = estimate(
				factory, 
				this.regressors, 
				response, 
				null);
		RModelList bestModelList = new RModelListEx();
		bestModelList.add(bestModel);
		bestModelList.setResponse(response);
		return bestModelList;
	}
	
	
	private RModel estimate(
			RModelFactory factory,
			List<String> regressors,
			String response,
			RModel seed
			) throws Exception {
		
		ArrayList<String> seedRegressorList = (seed == null ? 
				new ArrayList<String>() : seed.getRegressorNames());
		
		ArrayList<String> remainRegressorList = new ArrayList<String>();
		DSUtil.complement(regressors, seedRegressorList, remainRegressorList);
		remainRegressorList.remove(response);
		if (remainRegressorList.size() == 0)
			return seed;

		// Finding best regressors
		ArrayList<String> fitnessRegressors = 
			chooseBestRegressors(seedRegressorList, remainRegressorList, response);
		if (fitnessRegressors.size() == 0) 
			return seed;
			
		// Finding best models
		RModelList candidateModelList = new RModelListEx();
		for (String fitnessRegressor : fitnessRegressors) {
			ArrayList<String> newRegressorList = new ArrayList<String>(seedRegressorList);
			newRegressorList.add(fitnessRegressor);
			
			RModelList models = factory.createModelList(newRegressorList, response);
			RModelList fitModels = fitModel(models);
			
			if(fitModels.size() > 0) 
				candidateModelList.addAll(fitModels);
		}
		// There is no candidate model
		if (candidateModelList.size() == 0) 
			return seed;

		if (seed != null)
			candidateModelList.add(seed);
		// Chose best model
		RModelList bestModelList = chooseBestModels(candidateModelList);
		if (bestModelList.size() == 0) 
			return seed;
		RModel newSeed = bestModelList.get(0);
		if(newSeed == seed) 
			return newSeed;
		
		return estimate(factory, regressors, response, newSeed);
	}
	
	
	protected ArrayList<String> chooseBestRegressors(
			ArrayList<String> seedRegressorList, 
			ArrayList<String> inputRegressorList,
			String response) {
		
		// Independent
		ArrayList<String> independentList = new ArrayList<String>();
		if (seedRegressorList.size() == 0) {
			independentList.addAll(inputRegressorList);
		}
		else {
			for (String regressor : inputRegressorList) {
				if (independ(regressor, seedRegressorList))
					independentList.add(regressor);
			}
		}
		independentList.remove(response);
		if (independentList.size() == 0)
			return new ArrayList<String>();
		
		// Contribution high
		ArrayList<Pair<String>> pairList = new ArrayList<Pair<String>>();
		for (String regressor : independentList) {
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

	
	protected boolean independ(String regressor, ArrayList<String> otherRegressorList) {
		if (otherRegressorList.size() == 0)
			return true;
		if (!isNoneNegativeThreshold(this.independentThreshold)) 
			return true;
		
		double[] x = dataset.getMeasureArrayByName(regressor);
		for(String reg : otherRegressorList) {
			double[] y = dataset.getMeasureArrayByName(reg);
			if (!independ(x, y)) return false;
		}
		return true;
	}
	
	
	protected boolean independ(double[] x, double[] y) {
		if (!isNoneNegativeThreshold(independentThreshold))
			return true;
		else {
			double corr = Stat.corrCoeff(x, y);
			return (Math.abs(corr) <= independentThreshold);
		}
	}

	
	protected double contribute(String regressor, String response) {
		double[] x = dataset.getMeasureArrayByName(regressor);
		double[] y = dataset.getMeasureArrayByName(response);
		double corr = Stat.corrCoeff(x, y);
		
		if (!isNoneNegativeThreshold(fitnessThreshold))
			return corr > 0 ? corr : 0;
		else
			return corr >= fitnessThreshold && corr > 0 ? corr : 0;
	}
	
	
	public String getName() {
		return "sg";
	}


	public String getDesc() {
		return "Seed Germination";
	}

	
	private boolean isNoneNegativeThreshold(double threshold) {
		return DSUtil.isUsed(threshold) && threshold >= 0;
	}
	
}
