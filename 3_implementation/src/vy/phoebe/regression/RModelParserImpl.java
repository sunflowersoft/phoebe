package vy.phoebe.regression;

import java.util.ArrayList;
import java.util.HashMap;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.math.MathUtil;


public class RModelParserImpl implements RModelParser {

	private static final String PARSE_MULTI_LINE_SEP = "[;\r\n]";

	
	protected Dataset dataset = null;

	
	protected HashMap<String, Object> props = new HashMap<String, Object>();
	
	
	public RModelParserImpl(Dataset dataset) {
		this.dataset = dataset;
	}

	
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
	}
	
	
	public void putProp(String key, Object value) {
		this.props.put(key, value);
	}

	
	public Object getProp(String key) {
		return this.props.get(key);
	}

	
	@SuppressWarnings("unused")
	@Deprecated
	private static String pruneTerminate(String spec) {
		spec = spec.trim();
		while (true) {
			int begin = spec.indexOf("(");
			if (begin == 0)
				spec = spec.substring(1);
			
			int end = spec.lastIndexOf(")");
			if (end == spec.length() - 1)
				spec = spec.substring(0, spec.length() - 1);
			
			spec = spec.trim();

			if (begin != 0 && end != 0)
				break;
		}
		
		return spec;
	}
	
	
	@Deprecated
	private static String pruneAll(String spec) {
		spec = spec.replaceAll("\\(", "");
		spec = spec.replaceAll("\\)", "");
		spec = spec.trim();
		
		return spec;
	}
	
	
	@Deprecated
	private int lookupMinusPlus(String spec, int start) {
		int idx = -1;
		
		int minus = spec.indexOf("-", start);
		if (minus == -1)
			idx = spec.indexOf("+", start);
		else {
			int plus = spec.indexOf("+", start);
			if (plus == -1)
				idx = minus;
			else 
				idx = Math.min(minus, plus);
		}
		
		return idx;
	}

	
	@Deprecated
	private RModel parseLinear(String modelSpec) {
		modelSpec = RModelAssoc.finetune(modelSpec);
		
		ArrayList<String> regressorNames = new ArrayList<String>();
		ArrayList<Double> coeffs = new ArrayList<Double>();
		
		int start = 0;
		int idx = modelSpec.indexOf("=");
		String response = modelSpec.substring(start, idx).trim();
		modelSpec = modelSpec.substring(idx + 1);
		
		int count = 0;
		start = 0;
		idx = -1;
		while (true) {
			if (start == 0 && count == 0 && 
					(modelSpec.charAt(0) == '+' || modelSpec.charAt(0) == '-'))
				start = 1;
			
			idx = lookupMinusPlus(modelSpec, start);
			
			String piece = ""; 
			if (idx == -1)
				piece = modelSpec.substring(start).trim();
			else
				piece = modelSpec.substring(start, idx).trim();
			
			piece = pruneAll(piece);
			
			double coeff = 0;
			if (count == 0) {
				if (start > 0 && modelSpec.charAt(start - 1) == '-')
					piece = "-" + piece;
				coeff = Double.parseDouble(piece);
				coeffs.add(coeff);
			}
			else {
				if (modelSpec.charAt(start - 1) == '-')
					piece = "-" + piece;
				
				int multiSign = piece.indexOf("*");
				coeff = Double.parseDouble(piece.substring(0, multiSign));
				coeffs.add(coeff);
				regressorNames.add(piece.substring(multiSign + 1).trim());
			}
			
			if (idx == -1)
				break;
			
			start = idx + 1;
			
			count++;
		}
		
		DatasetRModel model = new DatasetRModel(
				dataset, 
				coeffs, 
				regressorNames, 
				response,
				"linear",
				0) {

			@Override
			public String getSpec() {
				double[] coeffs_ = this.getCoeffs();
				
				String text = response_ + "=" + 
					MathUtil.format(coeffs_[0]);
				
				if (regressorNames_.size() > 0 && coeffs_[1] > 0)
					text += "+";
				
				for (int i = 1; i <= regressorNames_.size(); i++) {
					
					if (i > 1 && coeffs_[i] > 0)
						text += "+";
					
					text += MathUtil.format(coeffs_[i]) + "*" + regressorNames_.get(i-1);
				}
				
				return text;
			}

			@Override
			public double eval(HashMap<String, Double> regressorValues) {
				double[] coeffs_ = this.getCoeffs();
				
				double response = coeffs_[0];
				
				for (int i = 1; i <= regressorNames_.size(); i++) {
					String regressorName = regressorNames_.get(i - 1);
					double regressorValue = regressorValues.get(regressorName);
					response += coeffs_[i] * regressorValue;
				}
				
				return response;
			}
			
		};
		
		model.complete();
		
		return model;
	}
	
	
	@Deprecated
	private RModel parsePolynomialSum(String modelSpec) {
		modelSpec = RModelAssoc.finetune(modelSpec);

		ArrayList<String> regressorNames = new ArrayList<String>();
		ArrayList<Double> coeffs = new ArrayList<Double>();
		
		int start = 0;
		int idx = modelSpec.indexOf("=");
		String response = modelSpec.substring(start, idx).trim();
		modelSpec = modelSpec.substring(idx + 1);
		
		int count = 0;
		start = 0;
		idx = -1;
		while ( true ) {
			if (start == 0 && count == 0 && 
					(modelSpec.charAt(0) == '+' || modelSpec.charAt(0) == '-'))
				start = 1;
			
			String piece = "";
			double coeff = 0;
			if (count == 0) {
				idx = lookupMinusPlus(modelSpec, start);
				if (idx == -1)
					piece = modelSpec.substring(start).trim();
				else
					piece = modelSpec.substring(start, idx).trim();
				piece = pruneAll(piece);
					
				if (start > 0 && modelSpec.charAt(start - 1) == '-')
					piece = "-" + piece;

				coeff = Double.parseDouble(piece);
				coeffs.add(coeff);
				
			}
			else {
				int idx1 = modelSpec.indexOf(")", start);
				int idx2 = modelSpec.indexOf("^", start);
				int idx3 = modelSpec.indexOf(")^", start);
				
				int idxMin = Math.min(Math.min(idx1, idx2), idx3);
				if (idxMin == -1)
					idx = lookupMinusPlus(modelSpec, start);
				else
					idx = lookupMinusPlus(modelSpec, idxMin);
				
				if (idx == -1)
					piece = modelSpec.substring(start).trim();
				else
					piece = modelSpec.substring(start, idx).trim();
				
				piece = piece.replaceAll("(\\^(\\d*))", "");
				piece = pruneAll(piece);
				
				int multiSign = piece.indexOf("*");
				String scoeff = piece.substring(0, multiSign);
				scoeff = pruneAll(scoeff);
				if (modelSpec.charAt(start - 1) == '-')
					scoeff = "-" + scoeff;
				coeff = Double.parseDouble(scoeff);
				coeffs.add(coeff);
				
				String nameList = piece.substring(multiSign + 1);
				String[] names = nameList.split("\\+");
				for (String name : names) {
					name = name.trim();
					if (name.length() > 0 && 
							regressorNames.indexOf(name) == -1) {
						regressorNames.add(name);
					}
				}

			}
			
			if (idx == -1)
				break;
			
			start = idx + 1;
			count ++;
		}

		DatasetRModel model = new DatasetRModel(
				dataset, 
				coeffs, 
				regressorNames, 
				response,
				"polynomial",
				0) {

			@Override
			public String getSpec() {
				double[] coeffs_ = this.getCoeffs();
				
				String text = response_ + "=" + 
					MathUtil.format(coeffs_[0]);
				
				int order = coeffs_.length - 1;
				
				if (order > 0 && coeffs_[1] > 0)
					text += "+";
				
				for (int i = 1; i <= order; i++) {
					if( i > 1 && coeffs_[i] > 0)
						text += "+";
					
					if (regressorNames_.size() > 1) {
						text += MathUtil.format(coeffs_[i]) + "*";
						
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
						text += MathUtil.format(coeffs_[i]) + "*";
						if (i == 1)
							text += regressorNames_.get(0);
						else
							text += "(" + regressorNames_.get(0) + "^" + i + ")";
					}
					
					
				}
				
				return text;
			}

			@Override
			public double eval(HashMap<String, Double> regressorValues) {
				double sum = 0;
				for (int i = 0; i < regressorNames_.size(); i++) {
					String regressor = regressorNames_.get(i);
					double value = regressorValues.get(regressor).doubleValue();
					sum += value;
				}
				
				
				double[] coeffs_ = this.getCoeffs();

				double result = coeffs_[0];
				int order = coeffs_.length - 1;
				for (int i = 1; i <= order; i++) {
					result += coeffs_[i] * Math.pow(sum, i);
				}
				
				return result;
			}
			
		};
		
		model.complete();
		
		return model;
	}

	
	@Deprecated
	private RModel parseSquareSum(String modelSpec) {
		RModel model = parsePolynomialSum(modelSpec);
		model.setType("squaresum");
		return model;
	}

	
	@Deprecated
	private RModel parseCubeSum(String modelSpec) {
		RModel model = parsePolynomialSum(modelSpec);
		model.setType("cubesum");
		return model;
	}
	
	
	@Deprecated
	private RModel parseLogSum(String modelSpec, double base) {
		modelSpec = RModelAssoc.finetune(modelSpec);

		ArrayList<String> regressorNames = new ArrayList<String>();
		ArrayList<Double> coeffs = new ArrayList<Double>();
		
		int start = 0;
		int idx = modelSpec.indexOf("=");
		String response = modelSpec.substring(start, idx).trim();
		modelSpec = modelSpec.substring(idx + 1);

		start = 0;
		idx = -1;
		
		if (start == 0 && 
				(modelSpec.charAt(0) == '+' || modelSpec.charAt(0) == '-'))
			start = 1;
		idx = lookupMinusPlus(modelSpec, start);
		String scoeff = modelSpec.substring(start, idx);
		scoeff = pruneAll(scoeff);
		if (start > 0 && modelSpec.charAt(start - 1) == '-')
			scoeff = "-" + scoeff;
		double coeff = Double.parseDouble(scoeff);
		coeffs.add(coeff);
		start = idx + 1;
		
		int multiSign = modelSpec.indexOf("*", start);
		
		scoeff = modelSpec.substring(start, multiSign);
		scoeff = pruneAll(scoeff);
		if (modelSpec.charAt(start - 1) == '-')
			scoeff = "-" + scoeff;
		coeff = Double.parseDouble(scoeff);
		coeffs.add(coeff);
		
		String namesList = modelSpec.substring(multiSign + 1);
		namesList = namesList.replaceAll(MathUtil.logaritName(base), "");
		namesList = pruneAll(namesList);
		String[] names = namesList.split("\\+");
		
		for (String name : names) {
			name = name.trim();
			if (name.length() > 0)
				regressorNames.add(name);
		}
		
		
		DatasetRModel model = new DatasetRModel(
				dataset, 
				coeffs, 
				regressorNames, 
				response,
				MathUtil.logaritName(base) + "_sum",
				base) {

			@Override
			public String getSpec() {
				double[] coeffs_ = this.getCoeffs();
				
				String text = response_ + "=" + 
					MathUtil.format(coeffs_[0]);
				
				if (coeffs_[1] > 0)
					text += "+";
				text += MathUtil.format(coeffs_[1]) + "*" + 
					MathUtil.logaritName(base_) + "(";
				
				for (int i = 0; i < regressorNames_.size(); i++) {
					if (i > 0)
						text += "+";
					text += regressorNames_.get(i);
					
				}
				
				text += ")";
				
				return text;
			}

			@Override
			public double eval(HashMap<String, Double> regressorValues) {
				double sum = 0;
				for (int i = 0; i < regressorNames_.size(); i++) {
					String regressor = regressorNames_.get(i);
					double value = regressorValues.get(regressor).doubleValue();
					sum += value;
				}
				
				double[] coeffs_ = this.getCoeffs();
				
				return coeffs_[0] + coeffs_[1] * Math.log(sum);
			}
			
		};
		
		model.complete();
		
		return model;
	}
	
	
	@Deprecated
	private RModel parseExpSum(String modelSpec, double base) {
		modelSpec = RModelAssoc.finetune(modelSpec);

		ArrayList<String> regressorNames = new ArrayList<String>();
		ArrayList<Double> coeffs = new ArrayList<Double>();
		
		int idx = modelSpec.indexOf("=");
		String response = modelSpec.substring(0, idx).trim();
		modelSpec = modelSpec.substring(idx + 1);

		int multiSign = modelSpec.indexOf("*", 0);
		String scoeff = modelSpec.substring(0, multiSign);
		scoeff = pruneAll(scoeff);
		double coeff = Double.parseDouble(scoeff);
		coeffs.add(coeff);
		
		String expr = modelSpec.substring(multiSign + 1);
		expr = expr.replaceAll(MathUtil.powName(base), "");
		expr = pruneAll(expr);
		multiSign = expr.indexOf("*", 0);
		scoeff = expr.substring(0, multiSign);
		scoeff = pruneAll(scoeff);
		coeff = Double.parseDouble(scoeff);
		coeffs.add(coeff);
		
		String nameList = expr.substring(multiSign + 1);
		String[] names = nameList.split("\\+");
		
		for (String name : names) {
			name = name.trim();
			if (name.length() > 0)
				regressorNames.add(name);
		}

		DatasetRModel model = new DatasetRModel(
				dataset, 
				coeffs, 
				regressorNames, 
				response,
				MathUtil.powName(base) + "_sum",
				base) {

			@Override
			public String getSpec() {
				double[] coeffs_ = this.getCoeffs();
				
				String text = response_ + "=";
				
				text += MathUtil.format(coeffs_[0]) + "*" + 
					MathUtil.powName(base_) + "(" + 
					MathUtil.format(coeffs_[1]) + "*";
				
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
			public double eval(HashMap<String, Double> regressorValues) {
				double sum = 0;
				for (int i = 0; i < regressorNames_.size(); i++) {
					String regressor = regressorNames_.get(i);
					double value = regressorValues.get(regressor).doubleValue();
					sum += value;
				}
				
				double[] coeffs_ = this.getCoeffs();
	
				return coeffs_[0] * Math.pow(base_, coeffs_[1] * sum);
			}
			
		};

		model.complete();
		
		return model;
	}

	
	@Deprecated
	private RModel parseProduct(String modelSpec) {
		modelSpec = RModelAssoc.finetune(modelSpec);

		ArrayList<String> regressorNames = new ArrayList<String>();
		ArrayList<Double> coeffs = new ArrayList<Double>();
		
		int start = 0;
		int idx = modelSpec.indexOf("=");
		String response = modelSpec.substring(start, idx).trim();
		modelSpec = modelSpec.substring(idx + 1);

		String[] tokenList = modelSpec.split("\\*");
		for (int i = 0; i < tokenList.length; i++) {
			String token = tokenList[i];
			token = pruneAll(token);
			
			double coeff = 0;
			if (i == 0) {
				coeff = Double.parseDouble(token);
				coeffs.add(coeff);
			}
			else {
				String[] express = token.split("\\^");
				String name = express[0];
				coeff = Double.parseDouble(express[1]);
				
				coeffs.add(coeff);
				regressorNames.add(name);
			}
		}
		
		
		DatasetRModel model = new DatasetRModel(
				dataset, 
				coeffs, 
				regressorNames, 
				response,
				"product",
				0) {

			@Override
			public String getSpec() {
				String text = response_ + "=";
				
				double[] coeffs_ = this.getCoeffs();
				text += MathUtil.format(coeffs_[0]) + "*";
				for (int i = 1; i <= regressorNames_.size(); i++) {
					if (i > 1)
						text += "*";
					
					if (coeffs_[i] == 1)
						text += regressorNames_.get(i - 1);
					else {
						text += "(";
						text += regressorNames_.get(i - 1) + "^";
						if (coeffs_[i] > 0)
							text += MathUtil.format(coeffs_[i]);
						else
							text += "(" + MathUtil.format(coeffs_[i]) + ")";
						text += ")";
							
					}
				}
				
				return text;
			}

			@Override
			public double eval(HashMap<String, Double> regressorValues) {
				double[] coeffs_ = this.getCoeffs();
				double result = coeffs_[0];
				
				for (int i = 1; i < coeffs_.length; i++) {
					String regressor = regressorNames_.get(i - 1);
					double value = regressorValues.get(regressor).doubleValue();
					
					result *= coeffs_[i] * value;
				}
				
				return result;
			}
			
		};

		model.complete();
		
		return model;
	}

	
	@Deprecated
	private RModel parse(String modelSpec, String type) {
		if (type.equals("linear"))
			return parseLinear(modelSpec);
		
		else if (type.equals("square_sum"))
			return parseSquareSum(modelSpec);
		
		else if (type.equals("cube_sum"))
			return parseCubeSum(modelSpec);
		
		else if (type.equals("log_sum"))
			return parseLogSum(modelSpec, Math.E);
		
		else if (type.equals("log10_sum"))
			return parseLogSum(modelSpec, 10);
		
		else if (type.equals("exp_sum"))
			return parseExpSum(modelSpec, Math.E);
		
		else if (type.equals("10^_sum"))
			return parseExpSum(modelSpec, 10);
		
		else if (type.equals("product_log") || 
				type.equals("product_log10") ||
				type.equals("product"))
			return parseProduct(modelSpec);
		
		return null;
	}
	
	
	@Deprecated
	public RModel parse(String desc) {
		String[] specType = RModelAssoc.getSpecAndType(desc);
		
		return parse(specType[0], specType[1]);
	}
	
	
	public RModel parseExpr(String desc) {
		return new ExprRModel(dataset, desc);
	}
	
	
	public RModelList parseExprs(String text) {
		RModelList modelList = new RModelListEx();
		
		String[] descList = text.split(PARSE_MULTI_LINE_SEP);
		for (String desc : descList) {
			desc = desc.trim();
			if (desc.isEmpty())
				continue;
			
			try {
				RModel model = parseExpr(desc);
				if (model != null)
					modelList.add(model);
			}
			catch (Throwable e) {
				e.printStackTrace();
				System.out.println("Can't parse model description  \"" + desc + "\"");
			}
		}
		
		return modelList;
	}

	
	public static void main(String[] args) {
		String spec = "BW = Exp( 2.695 + 0.253*AC - 0.00275*(AC^2) )";
		//
		HashMap<String, Double> values = new HashMap<String, Double>();
		values.put("AC", 1.0);
		//
		RModel model = new RModelParserImpl(null).parseExpr(spec);
		double value = model.eval(values);
		//
		System.out.println(model.getResponse() + " = " + value);
		
		
		spec = "BW = 10 ^ (1.1134 + 0.05845*AC - 0.000604*(AC^2) - " +
			"0.007365*(BPD^2) + 0.000595*BPD*AC + 0.1694*BPD)";
		//
		values = new HashMap<String, Double>();
		values.put("AC", 1.0);
		values.put("BPD", 2.0);
		//
		model = new RModelParserImpl(null).parseExpr(spec);
		value = model.eval(values);
		//
		System.out.println(model.getResponse() + " = " + value);
		
		
		spec = "BW = 10 ^ (1.335 - 0.0034*AC*FL + 0.0316*BPD + " + 
			"0.0457*AC + 0.1623*FL)";
		//
		values = new HashMap<String, Double>();
		values.put("AC", 1.0);
		values.put("BPD", 2.0);
		values.put("FL", 2.0);
		//
		model = new RModelParserImpl(null).parseExpr(spec);
		value = model.eval(values);
		//
		System.out.println(model.getResponse() + " = " + value);
		

		spec = "BW = 10 ^ (1.3596 + 0.0064*HC + 0.0424*AC + 0.174*FL + " + 
			"0.00061*BPD*AC - 0.00386*AC*FL)";
		//
		values = new HashMap<String, Double>();
		values.put("AC", 1.0);
		values.put("BPD", 2.0);
		values.put("HC", 3.0);
		values.put("FL", 4.0);
		//
		model = new RModelParserImpl(null).parseExpr(spec);
		value = model.eval(values);
		//
		System.out.println(model.getResponse() + " = " + value);
	}
	
	
	
}
