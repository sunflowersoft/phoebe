package vy.phoebe.regression;

import java.util.HashMap;
import java.util.List;

import vy.phoebe.Config;
import vy.phoebe.adapt.FlanaganRegression;
import vy.phoebe.math.MathUtil;

public final class RModelFactoryImpl extends RModelFactory {

	
	public RModelFactoryImpl() {
		super();
	}

	
	/**
	 * y = a0 + a1*x1 + a2*x2 + ... + an*xn
	 * 
	 * @param regressors
	 * @param response
	 * @return {@link RModel}
	 */
	private RModel createLinearModel(
			List<String> regressors, 
			String response) {
		
		double[][] xData = this.dataset.getMeasureMatrix(regressors); 
		double[]   yData = this.dataset.getMeasureArrayByName(response); 
		FlanaganRegression regression = new FlanaganRegression(xData, yData);
		regression.linear();
		if (!validateModel(regression))
			return null;
		
		DatasetRModel model = new DatasetRModel(
				dataset, 
				regression, 
				regressors, 
				response, 
				"linear",
				0) {
			
			@Override
			public double eval(HashMap<String, Double> regressorValues) {
				double[] estimators = this.getCoeffs();
				double result = estimators[0];
				for (int i = 0; i < regressorNames_.size(); i++) {
					String regressor = regressorNames_.get(i);
					double value = regressorValues.get(regressor).doubleValue();
					result += estimators[i + 1] * value;
				}
				return result;
			}
			
			@Override
			public String getSpec() {
				double[] estimators = this.getCoeffs();
				String text = response_ + "=" + 
					MathUtil.format(estimators[0]);
				
				if (regressorNames_.size() > 0 && estimators[1] > 0)
					text += "+";
				
				for (int i = 1; i <= regressorNames_.size(); i++) {
					
					if (i > 1 && estimators[i] > 0)
						text += "+";
					
					text += MathUtil.format(estimators[i]) + "*" + regressorNames_.get(i-1);
				}
				
				return text;
			}
			
		};
		
		model.complete();
		
		return model;
	}

	
	/**
	 * y = a0 + a1 * ((x1 + x2 + ... +xn)^k)
	 * 
	 * @param regressors
	 * @param response
	 * @param order
	 * @return {@link RModel}
	 */
	private RModel createPolynomialSumModel(
			List<String> regressors, 
			String response, 
			int order) {
		double[][] xDataTemp = this.dataset.getMeasureMatrix(regressors); 
		double[]   xData = MathUtil.sum(xDataTemp, 1);
		double[]   yData = this.dataset.getMeasureArrayByName(response);
		
		FlanaganRegression regression = new FlanaganRegression(xData, yData);
		regression.polynomial(order);
		if (!validateModel(regression))
			return null;
		
		DatasetRModel model = new DatasetRModel(
				dataset, 
				regression, 
				regressors, 
				response, 
				"polynomial_sum",
				order) {
			
			@Override
			public double eval(HashMap<String, Double> regressorValues) {
				double[] estimators = this.getCoeffs();
				double sum = 0;
				for (int i = 0; i < regressorNames_.size(); i++) {
					String regressor = regressorNames_.get(i);
					double value = regressorValues.get(regressor).doubleValue();
					sum += value;
				}
				
				
				double result = estimators[0];
				for (int i = 1; i <= base_; i++) {
					double v = estimators[i] * Math.pow(sum, i);
					result += v;
				}
				
				return result;
			}
			
			@Override
			public String getSpec() {
				double[] estimators = this.getCoeffs();
				String text = response_ + "=" + 
					MathUtil.format(estimators[0]);
				
				if (base_ > 0 && estimators[1] > 0)
					text += "+";
				
				for (int i = 1; i <= base_; i++) {
					if( i > 1 && estimators[i] > 0)
						text += "+";
					
					if (regressorNames_.size() > 1) {
						text += MathUtil.format(estimators[i]) + "*";
						
						String s = "";
						for (int j = 0; j < regressorNames_.size(); j++) {
							if (j > 0)
								s += "+";
							s += regressorNames_.get(j);
						}
						
						if (i == 1)
							text = text + "(" + s + ")";
						else
							text = text + "((" + s + ")" + "^" + i + ")";
							
					}
					else {
						text += MathUtil.format(estimators[i]) + "*";
						if (i == 1)
							text += regressorNames_.get(0);
						else
							text += "(" + regressorNames_.get(0) + "^" + i + ")";
					}
					
					
				}
				
				return text;
			}
			
		};
		
		model.complete();
		
		return model;
	}

	
	/**
	 * y = a0 + a1 * ((x1 + x2 + ... +xn)^2)
	 * 
	 * @param regressors
	 * @param response
	 * @return {@link RModel}
	 */
	private RModel createSquareSumModel(
			List<String> regressors, 
			String response) {
		RModel model = createPolynomialSumModel(regressors, response, 2);
		model.setType("square_sum");
		return model;
	}

	
	/**
	 * y = a0 + a1 * ((x1 + x2 + ... +xn)^3)
	 * 
	 * @param regressors
	 * @param response
	 * @return {@link RModel}
	 */
	private RModel createCubeSumModel(
			List<String> regressors, 
			String response) {
		RModel model = createPolynomialSumModel(regressors, response, 3);
		model.setType("cube_sum");
		return model;
	}
	

	/**
	 * y = a0 + a1*log(x1) + a2*log(x2) + ... + an*log(xn)
	 * @param regressors
	 * @param response
	 * @param base
	 * @return {@link RModel}
	 */
	private RModel createLogModel(
			List<String> regressors, 
			String response,
			double base) {
		
		double[][] xDataTemp = this.dataset.getMeasureMatrix(regressors); 
		double[][] xData = null; 
		double[]   yData = this.dataset.getMeasureArrayByName(response); 
		xData = MathUtil.logarit(xDataTemp, base);
		
		FlanaganRegression regression = new FlanaganRegression(xData, yData);
		regression.linear();
		if (!validateModel(regression))
			return null;
		
		DatasetRModel model = new DatasetRModel(
				dataset, 
				regression, 
				regressors, 
				response,
				MathUtil.logaritName(base),
				base) {
			
			@Override
			public double eval(HashMap<String, Double> regressorValues) {
				double[] estimators = this.getCoeffs();
				double result = estimators[0];
				for (int i = 0; i < regressorNames_.size(); i++) {
					String regressor = regressorNames_.get(i);
					
					double value = regressorValues.get(regressor).doubleValue();
					value = MathUtil.logarit(value, base_);
						
					result += estimators[i + 1] * value;
				}
				return result;
			}
			
			@Override
			public String getSpec() {
				double[] estimators = this.getCoeffs();
				String text = response_ + "=" + 
					MathUtil.format(estimators[0]);
				
				if (regressorNames_.size() > 0 && estimators[1] > 0)
					text += "+";
				
				for (int i = 1; i <= regressorNames_.size(); i++) {
					if (i > 1 && estimators[i] > 0)
						text += "+";

					text += MathUtil.format(estimators[i]) + "*";
					
					text += MathUtil.logaritName(base_) + "(" + regressorNames_.get(i-1) + ")";
					
				}
				
				return text;
			}
			
		};
		
		model.complete();
		
		return model;

	}

	
	/**
	 * y = a0 + a1 * (log(x1 + x2 +... + xn))
	 * 
	 * @param regressors
	 * @param response
	 * @param base
	 * @return {@link RModel}
	 */
	private RModel createLogSumModel(
			List<String> regressors, 
			String response,
			double base) {
		
		double[][] xDataTemp = this.dataset.getMeasureMatrix(regressors); 
		double[] xData = MathUtil.sum(xDataTemp, 1); 
		double[]   yData = this.dataset.getMeasureArrayByName(response); 
		xData = MathUtil.logarit(xData, base);
		
		FlanaganRegression regression = new FlanaganRegression(xData, yData);
		regression.linear();
		if (!validateModel(regression))
			return null;
		
		DatasetRModel model = new DatasetRModel(
				dataset, 
				regression, 
				regressors, 
				response,
				MathUtil.logaritName(base) + "_sum",
				base) {
			
			@Override
			public double eval(HashMap<String, Double> regressorValues) {
				
				double sum = 0;
				for (int i = 0; i < regressorNames_.size(); i++) {
					String regressor = regressorNames_.get(i);
					double value = regressorValues.get(regressor).doubleValue();
					sum += value;
				}
				
				double[] estimators = this.getCoeffs();
				
				return estimators[0] + estimators[1]* MathUtil.logarit(sum, base_);
			}
			
			@Override
			public String getSpec() {
				double[] estimators = this.getCoeffs();
				String text = response_ + "=" + 
					MathUtil.format(estimators[0]);
				
				if (estimators[1] > 0)
					text += "+";
				text += MathUtil.format(estimators[1]) + "*" + 
					MathUtil.logaritName(base_) + "(";
				
				for (int i = 0; i < regressorNames_.size(); i++) {
					if (i > 0)
						text += "+";
					text += regressorNames_.get(i);
					
				}
				
				text += ")";
				
				return text;
			}
			

		};
		
		model.complete();
		
		return model;

	}

	
	/**
	 * log(y) = a0 + a1*x1 + a2*x2 + ... + an*xn
	 * y = base ^ (a0 + a1*x1 + a2*x2 + ... + an*xn)
	 * 
	 * Note: base is e or 10
	 * @param regressors
	 * @param response
	 * @param base
	 * @return {@link RModel}
	 */
	private RModel createExponentModel(
			List<String> regressors, 
			String response,
			double base) {
		
		double[][] xData = this.dataset.getMeasureMatrix(regressors); 
		double[]   yDataTemp = this.dataset.getMeasureArrayByName(response); 
		double[]   yData = null;
		yData = MathUtil.logarit(yDataTemp, base);
		
		FlanaganRegression regression = new FlanaganRegression(xData, yData);
		regression.linear();
		if (!validateModel(regression))
			return null;
		
		DatasetRModel model = new DatasetRModel(dataset, 
				regression, 
				regressors, 
				response,
				MathUtil.powName(base),
				base) {
			
			@Override
			public double eval(HashMap<String, Double> regressorValues) {
				double[] estimators = this.getCoeffs();
				double result = estimators[0];
				for (int i = 0; i < regressorNames_.size(); i++) {
					String regressor = regressorNames_.get(i);
					double value = regressorValues.get(regressor).doubleValue();
					result += estimators[i + 1] * value;
				}
				return Math.pow(base_, result);
			}
			
			
			@Override
			public String getSpec() {
				double[] estimators = this.getCoeffs();
				String text = response_ + "=";
				
				double a = Math.pow(base_, estimators[0]);
				text += MathUtil.format(a) + "*" + MathUtil.powName(base_) + "(";
				
				for (int i = 1; i <= regressorNames_.size(); i++) {
					if (i > 1 && estimators[i] > 0)
						text += "+";
					text += MathUtil.format(estimators[i]) + "*" + regressorNames_.get(i-1);
				}

				text += ")";
				
				return text;
			}
			
			
			@Override
			public String getNiceForm() {
				double[] estimators = this.getCoeffs();
				String text = MathUtil.logaritName(base_) + "(" + response_ + ")=" + 
					MathUtil.format(estimators[0]);
				
				if (regressorNames_.size() > 0 && estimators[1] > 0)
					text += "+";
				
				for (int i = 1; i <= regressorNames_.size(); i++) {
					
					if (i > 1 && estimators[i] > 0)
						text += "+";
					
					text += MathUtil.format(estimators[i]) + "*" + regressorNames_.get(i-1);
				}
				
				return text;
			}
			
		};
		
		model.complete();
		
		return model;

	}

	
	/**
	 * log(y) = a0 + a1 * (x1 + x2 + ... + xn)
	 * y = base ^ (a0 + a1 * (x1 + x2 + ... + xn))
	 * 
	 * Note: base is e or 10
	 * 
	 * @param regressors
	 * @param response
	 * @param base
	 * @return {@link RModel}
	 */
	private RModel createExponentSumModel(
			List<String> regressors, 
			String response,
			double base) {
		
		double[][] xDataTemp = this.dataset.getMeasureMatrix(regressors); 
		double[]   xData = MathUtil.sum(xDataTemp, 1); 
		double[]   yDataTemp = this.dataset.getMeasureArrayByName(response); 
		double[]   yData = MathUtil.logarit(yDataTemp, base);
		
		FlanaganRegression regression = new FlanaganRegression(xData, yData);
		regression.linear();
		if (!validateModel(regression))
			return null;
		
		DatasetRModel model = new DatasetRModel(
				dataset, 
				regression, 
				regressors, 
				response,
				MathUtil.powName(base) + "_sum",
				base) {
			
			@Override
			public double eval(HashMap<String, Double> regressorValues) {
				double sum = 0;
				for (int i = 0; i < regressorNames_.size(); i++) {
					String regressor = regressorNames_.get(i);
					double value = regressorValues.get(regressor).doubleValue();
					sum += value;
				}

				double[] estimators = this.getCoeffs();
				double result = estimators[0] + estimators[1] * sum; 
				return Math.pow(base_, result);
				
			}
			
			
			@Override
			public String getSpec() {
				double[] estimators = this.getCoeffs();
				String text = response_ + "=";
				
				double a = Math.pow(base_, estimators[0]);
				text += MathUtil.format(a) + "*" + MathUtil.powName(base_) + "(" + 
					MathUtil.format(estimators[1]) + "*";
				
				if (regressorNames_.size() == 1)
					text += regressorNames_.get(0);
				else {
					text += "(";
					for (int i = 0; i < regressorNames_.size(); i++) {
						if (i > 0)
							text += "+";
						text += regressorNames_.get(i);
					}
					text += ")";
				}
				
				text += ")";
				
				return text;
			}
			
			
			@Override
			public String getNiceForm() {
				double[] estimators = this.getCoeffs();
				String text = MathUtil.logaritName(base_) + "(" + response_ + ")=" + 
					MathUtil.format(estimators[0]);
				
				if (regressorNames_.size() > 0 && estimators[1] > 0)
					text += "+";
				text += estimators[1] + "*";
				
				if (regressorNames_.size() == 1)
					text += regressorNames_.get(0);
				else {
					text += "(";
					for (int i = 0; i < regressorNames_.size(); i++) {
						if (i > 0)
							text += "+";
						text += regressorNames_.get(i);
					}
					text += ")";
				}
				
				return text;
			}
			
			
		};
		
		model.complete();
		
		return model;

	}

	
	/**
	 * log(y) = a0 + a1*log(x1) + a2*log(x2) + ... + an*log(xn)
	 * y = base ^ (a0 + a1*log(x1) + a2*log(x2) + ... + an*log(xn))
	 * y = b0 * (x1^b1) * (x2^b2) * ... * (xn^bn)
	 * 
	 * @param regressors
	 * @param response
	 * @param base
	 * @return {@link RModel}
	 */
	private RModel createProductModel(
			List<String> regressors, 
			String response,
			double base) {
		
		double[][] xDataTemp = this.dataset.getMeasureMatrix(regressors); 
		double[][] xData = null; 
		double[]   yDataTemp = this.dataset.getMeasureArrayByName(response); 
		double[]   yData = null;
		
		yData = MathUtil.logarit(yDataTemp, base);
		xData = MathUtil.logarit(xDataTemp, base);
		
		FlanaganRegression regression = new FlanaganRegression(xData, yData);
		regression.linear();
		if (!validateModel(regression))
			return null;
		
		DatasetRModel model = new DatasetRModel(
				dataset, 
				regression, 
				regressors, 
				response,
				"product_" + MathUtil.logaritName(base),
				base) {
			
			@Override
			public double eval(HashMap<String, Double> regressorValues) {
				double[] estimators = this.getCoeffs();
				double result = estimators[0];
				for (int i = 0; i < regressorNames_.size(); i++) {
					String regressor = regressorNames_.get(i);
					
					double value = regressorValues.get(regressor).doubleValue();
					value = MathUtil.logarit(value, base_);
						
				
					result += estimators[i + 1] * value;
				}
				return Math.pow(base_, result);
			}
			
			
			@Override
			public String getSpec() {
				double[] estimators = this.getCoeffs();
				String text = response_ + "=";
				
				double a = Math.pow(base_, estimators[0]);
				text += MathUtil.format(a) + "*";
				for (int i = 1; i <= regressorNames_.size(); i++) {
					if (i > 1)
						text += "*";
					
					if (estimators[i] == 1)
						text += regressorNames_.get(i - 1);
					else {
						text += "(";
						text += regressorNames_.get(i - 1) + "^";
						if (estimators[i] > 0)
							text += MathUtil.format(estimators[i]);
						else
							text += "(" + MathUtil.format(estimators[i]) + ")";
						text += ")";
							
					}
				}
				
				return text;
			}
			
			
			@Override
			public String getNiceForm() {
				double[] estimators = this.getCoeffs();
				String logName = MathUtil.logaritName(base_);
				String text = logName + "(" + response_ + ")=" + 
					MathUtil.format(estimators[0]);
				
				if (regressorNames_.size() > 0 && estimators[1] > 0)
					text += "+";
				
				for (int i = 1; i <= regressorNames_.size(); i++) {
					
					if (i > 1 && estimators[i] > 0)
						text += "+";
					
					text += MathUtil.format(estimators[i]) + "*" + 
							logName + "(" + regressorNames_.get(i-1) + ")";
				}
				
				return text;
			}

		};
		
		model.complete();
		
		return model;
	}

	
	private static boolean validateModel(FlanaganRegression regression) {
		double[] estimators = regression.getBestEstimates();
		
		for (double estimator : estimators) {
			if (Double.isNaN(estimator))
				return false;
		}
		
		return true;
	}

	
	private void addDistinctTo(RModel model, RModelList modelList) {
		if (model == null)
			return;
			
		String spec = model.getSpec();
		boolean found = false;
		for (RModel m : modelList) {
			String mspec = m.getSpec();
			if (mspec.equals(spec)) {
				found = true;
				break;
			}
		}
		
		if (!found)
			modelList.add(model);
	}
	
	
	@Override
	public RModelList createModelList(List<String> newRegressorList,
			String response) {
		
		RModelList modelList = new RModelListEx();

		if (Config.LINEAR_MODEL) {
			RModel linearModel = createLinearModel(newRegressorList, response);
			addDistinctTo(linearModel, modelList);
		}
		
		if (Config.SQUARE_MODEL) {
			RModel squareModel = createSquareSumModel(newRegressorList, response);
			addDistinctTo(squareModel, modelList);
		}
		
		if (Config.CUBE_MODEL) {
			RModel cubeModel = createCubeSumModel(newRegressorList, response);
			addDistinctTo(cubeModel, modelList);
		}
		
		if (Config.LOG_MODEL) {
			RModel logModel = createLogModel(newRegressorList, response, Math.E);
			RModel logSumModel = createLogSumModel(newRegressorList, response, Math.E);
			addDistinctTo(logModel, modelList);
			addDistinctTo(logSumModel, modelList);
			
			RModel log10Model = createLogModel(newRegressorList, response, 10);
			RModel log10SumModel = createLogSumModel(newRegressorList, response, 10);
			addDistinctTo(log10Model, modelList);
			addDistinctTo(log10SumModel, modelList);
		}
		
		if (Config.EXP_MODEL) {
			RModel expModel = createExponentModel(newRegressorList, response, Math.E);
			RModel expSumModel = createExponentSumModel(newRegressorList, response, Math.E);
			addDistinctTo(expModel, modelList);
			addDistinctTo(expSumModel, modelList);
			
			RModel exp10Model = createExponentModel(newRegressorList, response, 10);
			RModel exp10SumModel = createExponentSumModel(newRegressorList, response, 10);
			addDistinctTo(exp10Model, modelList);
			addDistinctTo(exp10SumModel, modelList);
		}
		
		
		if (Config.PRODUCT_MODEL) {
			RModel productEmodel = createProductModel(newRegressorList, response, Math.E);
			addDistinctTo(productEmodel, modelList);

			RModel product10model = createProductModel(newRegressorList, response, 10);
			addDistinctTo(product10model, modelList);
		}
		
		return modelList;
	}
	
	

	


}
