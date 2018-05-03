package vy.phoebe.regression;

import java.util.ArrayList;
import java.util.HashMap;

import vy.phoebe.regression.ui.graph.Graph;

public interface RModel {

	final static String DESC_DELIMITER_REGEX = ":";

	
	final static String BEGIN_CHOSEN_SIGN = "<";
	
	
	final static String END_CHOSEN_SIGN = ">";

	
	double[] getCoeffs();
	
	
	double eval(HashMap<String, Double> regressorValues);
	
	
	double getR();
	
	
	String getResponse();
	
	
	double getFitness();

	
	double getSumOfSquares();
	
	
	double getMean();
	
	
	double getSd();


	double getErrMean();
	
	
	double getErrSd();

	
	double getRatioErrMean();
	
	
	double getRatioErrSd();

	
	double getPvalue();


	String getSpec();
	
	
	String getNiceForm();

	
	String getType();

	
	String setType(String type);

	
	ArrayList<Graph> getGraphList();
	
	
	ArrayList<String> getRegressorNames();

	
}
