package vy.phoebe.adapt;

import java.awt.Color;
import java.util.ArrayList;

import flanagan.analysis.Regression;
import flanagan.math.Fmath;
import flanagan.plot.PlotGraph;

public class FlanaganRegression extends Regression {

	public FlanaganRegression() {
		super();
		// TODO Auto-generated constructor stub
	}

	public FlanaganRegression(double[] xxData, double binWidth, double binZero) {
		super(xxData, binWidth, binZero);
		// TODO Auto-generated constructor stub
	}

	public FlanaganRegression(double[] xxData, double binWidth) {
		super(xxData, binWidth);
		// TODO Auto-generated constructor stub
	}

	public FlanaganRegression(double[] xxData, double[] data, double[] weight) {
		super(xxData, data, weight);
		// TODO Auto-generated constructor stub
	}

	public FlanaganRegression(double[] xxData, double[] data) {
		super(xxData, data);
		// TODO Auto-generated constructor stub
	}

	public FlanaganRegression(double[] xxData, double[][] yyData,
			double[][] weight) {
		super(xxData, yyData, weight);
		// TODO Auto-generated constructor stub
	}

	public FlanaganRegression(double[] xxData, double[][] yyData) {
		super(xxData, yyData);
		// TODO Auto-generated constructor stub
	}

	public FlanaganRegression(double[][] data, double[] data2, double[] weight) {
		super(data, data2, weight);
		// TODO Auto-generated constructor stub
	}

	public FlanaganRegression(double[][] data, double[] data2) {
		super(data, data2);
		// TODO Auto-generated constructor stub
	}

	public FlanaganRegression(double[][] xxData, double[][] yyData,
			double[][] weight) {
		super(xxData, yyData, weight);
		// TODO Auto-generated constructor stub
	}

	public FlanaganRegression(double[][] xxData, double[][] yyData) {
		super(xxData, yyData);
		// TODO Auto-generated constructor stub
	}

	
    public ArrayList<PlotGraph> plotYY2(String title){
        this.graphTitle = title;
        int ncurves = 2;
        int npoints = this.nData0;
        double[][] data = PlotGraph.data(ncurves, npoints);

        ArrayList<PlotGraph> result = new ArrayList<PlotGraph>();
        
        int kk = 0;
        for(int jj=0; jj<this.nYarrays; jj++){

            // fill first curve with experimental versus best fit values
            for(int i=0; i<nData0; i++){
                data[0][i]=this.yData[kk];
                data[1][i]=this.yCalc[kk];
                kk++;
            }

            // Create a title
            String title0 = this.setGandPtitle(this.graphTitle);
            if(this.multipleY)title0 = title0 + "y array " + jj;
            String title1 = "Calculated versus experimental y values";

            // Calculate best fit straight line between experimental and best fit values
            Regression yyRegr = new Regression(this.yData, this.yCalc, this.weight);
            yyRegr.linear();
            double[] coef = yyRegr.getCoeff();
            data[2][0]=Fmath.minimum(this.yData);
            data[3][0]=coef[0]+coef[1]*data[2][0];
            data[2][1]=Fmath.maximum(this.yData);
            data[3][1]=coef[0]+coef[1]*data[2][1];

            PlotGraph pg = new PlotGraph(data);

            pg.setGraphTitle(title0);
            pg.setGraphTitle2(title1);
            pg.setXaxisLegend("Experimental y value");
            pg.setYaxisLegend("Calculated y value");
            int[] popt = {1, 0};
            pg.setPoint(popt);
            int[] lopt = {0, 3};
            pg.setLine(lopt);

            pg.setBackground(Color.WHITE);
            result.add(pg);
        }
        
        return result;
    }

    
	
	
}
