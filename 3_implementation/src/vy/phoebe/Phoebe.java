package vy.phoebe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.estimator.BruteForceEstimator;
import vy.phoebe.estimator.Estimator;
import vy.phoebe.regression.RModelGroupList;
import vy.phoebe.regression.RModelList;
import vy.phoebe.regression.RModelParser;
import vy.phoebe.regression.RModelParserImpl;
import vy.phoebe.util.Constants;
import vy.phoebe.util.FileUtil;
import vy.phoebe.util.MiscUtil;

public class Phoebe {
	
	private final static String DELIMITER_REGEX = "[;:]";
	
	
	private final static String OPT_CONSOLE = "console";
	
	private final static String OPT_FILE = "file";
	
	private final static String OPT_RM_FILE = "rmfile";
	
	private final static String OPT_METHOD = "method";

	private final static String OPT_ACT = "act";
	
	private final static String OPT_REG = "reg";

	private final static String OPT_RES = "res";
	
	private final static String OPT_OUT = "out";
	
	
	private final static String ACT_ESTIMATE = "estimate";
	
	private final static String ACT_COMPARE = "compare";


	protected CommandLine cmdline = null;
	
	
	protected File out = null;
	
	
	public Phoebe() {
		
	}
	
	
	public void process(String[] args) throws Exception {
		Config.read();
		
		if (args.length == 0) {
			new GUI();
			return;
		}
		
		Options options = buildOptions();
		CommandLine cmdline = MiscUtil.parseCmdLine(args, options);
		if (cmdline == null) {
			new GUI();
			return;
		}
	
		if (!cmdline.hasOption(OPT_CONSOLE)) {
			new GUI();
			return;
		}
		
		if (cmdline.hasOption(OPT_OUT))
			out = new File(cmdline.getOptionValue(OPT_OUT));
		
		String act = ACT_ESTIMATE;
		if (cmdline.hasOption(OPT_ACT))
			act = cmdline.getOptionValue(OPT_ACT).toLowerCase();
		
		if (act.equals(ACT_ESTIMATE)) {
			processEstimate();
		}
		else if (act.equals(ACT_COMPARE)) {
			processCompare();
		}
		
	}
	
	
	private void processEstimate() throws Exception {
		RModelGroupList resultList = new RModelGroupList();
		RModelGroupList groupList = new RModelGroupList();
		
		processEstimate(resultList, groupList);
		
        for (int i = 0; i < resultList.size(); i++) {
        	RModelList modelList = resultList.get(i);
        	String text = modelList.toString();
        	if (i > 0)
	        	print("\n\n");
        	
        	print(text);
        }
        
        if (groupList.size() > 0) {
        	print("\n\n\n\n");
        	print(groupList.toString());
        }
	}
	
	
	private void processCompare() throws Exception {
		RModelGroupList resultList = new RModelGroupList();
		
		Dataset dataset = processEstimate(
				resultList, 
				new RModelGroupList());
		if (resultList.size() == 0) {
			print("No result");
			return;
		}
		
		RModelParser parser = new RModelParserImpl(dataset);
		File rmFile = new File(
				cmdline.getOptionValue(OPT_RM_FILE)
			);
		
		String content = FileUtil.read(rmFile); 
		RModelList modelList = parser.parseExprs(content);
		
		RModelList comparedList = resultList.get(0).compare(modelList);
		print(comparedList.toString());
	}

	
	private Dataset processEstimate(
			RModelGroupList outResultList, 
			RModelGroupList outGroupList) throws Exception {
		
		File file = new File(cmdline.getOptionValue(OPT_FILE));
		Dataset dataset = Dataset.parse(file);
		
		ArrayList<String> regressorList = parseVarList(OPT_REG);
		String method = Config.ESTI_METHOD;
		if (cmdline.hasOption(OPT_METHOD))
			method = cmdline.getOptionValue(OPT_METHOD).toLowerCase();
		
		Estimator estimator = Config.getEstimator(method);
		
		estimator.setDataset(dataset);
		estimator.setRegressor(regressorList);
		estimator = new BruteForceEstimator(dataset, regressorList);
		estimator.setParameters(
				Constants.UNUSED, 
				Config.FITNESS, 
				Config.MAX_RESULTS);
		
		ArrayList<String> responseList = parseVarList(OPT_RES);
		outResultList.clear();
		
		long beginTime = System.currentTimeMillis();
		RModelGroupList result = estimator.estimate(
				responseList, Config.MAX_RESULTS); 
		long endTime = System.currentTimeMillis();
		System.out.println(new Date() + 
				": Time elapsed for estimation = " + (endTime - beginTime) + " (milliseconds)");
		
		outResultList.addAll(result);

		outGroupList.clear();
		outGroupList.addAll(outResultList);
		outGroupList.regroup(Config.GROUP_MAX_RESULTS, !Config.GROUP_FLEXIBLE);
		
		for (RModelList list1 : outGroupList) {
			for (RModelList list2 : outResultList) {
				list1.addChosenList(list2.getChosenList());
			}
		}

		return dataset;
	}

	
	private ArrayList<String> parseVarList(String option) {
		String vars = cmdline.getOptionValue(option);
		String[] a_var = vars.split(DELIMITER_REGEX);
		ArrayList<String> varList = new ArrayList<String>();
		for (String var : a_var) {
			var = var.trim();
			if (!var.isEmpty())
				varList.add(var);
		}
		
		return varList;
	}
	
	
	private void print(String text) {
		if (out == null)
			System.out.print(text);
		try {
			PrintWriter printer = new PrintWriter( new FileWriter(out, true));
			printer.print(text);
			printer.flush();
			printer.close();
			
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	private Options buildOptions() {
		Options options = new Options();
		
		options.addOption(OPT_CONSOLE, false, "whether using console");
		
		options.addOption(OPT_FILE, true, "dataset file");
		
		options.addOption(OPT_RM_FILE, true, "regression model file");

		options.addOption(OPT_METHOD, true, "method");

		options.addOption(OPT_ACT, true, "action");
		
		options.addOption(OPT_REG, true, "regressor list");
		
		options.addOption(OPT_RES, true, "response");

		options.addOption(OPT_OUT, true, "output file");

		return options;
	}
	

	public static void main(String[] args) throws Exception {
		new Phoebe().process(args);
	}

	
	
}
