package vy.phoebe.regression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.math.MathUtil;
import vy.phoebe.math.Parser;
import vy.phoebe.util.DSUtil;

public class ExprRModel extends DatasetRModel {

	private final static String OPERATORS_PATTERN = "[-/%\\+\\*\\^\\(\\)]";
	
	
	private final static String[] FUNCTIONS = {
		"abs", "exp", "sign", "sqrt", "log", "log10",
		"sin", "cos", "tan", "asin", "acos", "atan", "factorial"
	}; 
	
	
	private final static String[] MATH_CONSTANTS = {
		"E", "PI"
	};

	
	private final static Map<String, String> FUNC_MAP = new HashMap<String, String>();
	
	static {
		Arrays.sort(FUNCTIONS);
		Arrays.sort(MATH_CONSTANTS);
		
		FUNC_MAP.put("log", "E^");
		FUNC_MAP.put("E^", "log");
		
		FUNC_MAP.put("log10", "10^");
		FUNC_MAP.put("10^", "log10");
	}

	
	private String expr_ = "";
	
	
	public ExprRModel(Dataset dataset, String desc) {
		super(dataset, new double[0], new ArrayList<String>(), "", "expression", 0);
		
		String[] specType = RModelAssoc.getSpecAndType(desc);
		
		String modelSpec = specType[0];
		this.type_ = specType[1];
		if (type_ == null || type_.isEmpty())
			this.type_ = "expression";
		
		int idx = modelSpec.indexOf("=");
		this.response_ = modelSpec.substring(0, idx).trim();
		this.expr_ = modelSpec.substring(idx + 1).trim();
		this.expr_ = this.expr_.replaceAll(",", ".");
		
		int start = this.response_.indexOf('(');
		int end = this.response_.indexOf(')');
		if (start != -1 && end != -1 && start < end) {
			String response = this.response_.substring(start + 1, end);
			String func = this.response_.substring(0, start).trim().toLowerCase();
			if (FUNC_MAP.containsKey(func)) {
				String rFunc = FUNC_MAP.get(func);
				this.response_ = response;
				this.expr_ = rFunc + "(" + this.expr_ + ")"; 
			}
		}
		
		List<String> varList = splitVars(this.expr_);
		this.regressorNames_.clear();
		this.regressorNames_.addAll(varList);
		
		this.complete();
	}

	
	private static List<String> splitVars(String expr) {
		Set<String> varSet = new HashSet<String>();
		splitVars(expr, varSet);
		
		List<String> varList = new ArrayList<String>();
		varList.addAll(varSet);
		
		return varList;
	}
	
	
	private static void splitVars(String expr, Set<String> varSet) {
		List<String> varList = DSUtil.split(expr, OPERATORS_PATTERN, null);
		for (String var : varList) {
			var = RModelAssoc.finetune(var);
			if (!isVariable(var))
				continue;
				
			List<String> varList2 = DSUtil.split(var, OPERATORS_PATTERN, null);
			if (varList2.size() == 0)
				continue;
			else if (varList2.size() == 1) {
				varSet.add(var);
			}
			else
				splitVars(var, varSet);
		}
	}
	
	
	@Override
	public double eval(HashMap<String, Double> regressorValues) {
		String expression = this.expr_;
		for (String var : this.regressorNames_) {
			String value = MathUtil.format(regressorValues.get(var));
			expression = expression.replaceAll(var, value);
		}
		
		Parser parser = new Parser();
		
		String result = parser.parse(expression);
		
		result = result.substring(result.indexOf("=") + 1).trim();
		
		return Double.parseDouble(result);
	}

	
	@Override
	public String getSpec() {
		return response_ + "=" + expr_;
	}


	private static boolean isVariable(String var) {
		var = var.trim().toLowerCase();
		if (var.length() == 0)
			return false;
		
		if (Arrays.binarySearch(FUNCTIONS, var) >= 0)
			return false;
		
		if (Arrays.binarySearch(FUNCTIONS, var.toUpperCase()) >= 0)
			return false;

		if (Arrays.binarySearch(MATH_CONSTANTS, var) >= 0)
			return false;
		
		if (Arrays.binarySearch(MATH_CONSTANTS, var.toUpperCase()) >= 0)
			return false;

		String patt = "0123456789.";
		boolean allfound = true;
		for (int i = 0; i < var.length(); i++) {
			char c = var.charAt(i);
			if (patt.indexOf(c) == -1) {
				allfound = false;
				break;
			}
		}
		
		if (allfound)
			return false;
		
		return true;
	}

	
	
}
