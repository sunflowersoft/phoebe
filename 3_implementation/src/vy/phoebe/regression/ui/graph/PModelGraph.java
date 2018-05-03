package vy.phoebe.regression.ui.graph;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.ArrayList;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.PModel;
import vy.phoebe.regression.RModel;
import flanagan.analysis.Stat;
import flanagan.interpolation.CubicSpline;
import flanagan.math.Fmath;


public class PModelGraph extends PlotGraphExt {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected static class NameColor {
		
		
		public Color color = null;
		
		
		public String name = null;
			
		
		public NameColor(Color color, String name) {
			this.color = color;
			this.name = name;
		}
		
		
	}
	
	
	public static NameColor[] COLOR_LIST = new NameColor[] {
		new NameColor(Color.red, "Red"),
		new NameColor(Color.green, "Green"),
		new NameColor(Color.blue, "Blue"),
		new NameColor(Color.cyan, "Cyan"),
		new NameColor(Color.magenta, "Magenta"),
		new NameColor(Color.orange, "Orange"),
		new NameColor(Color.pink, "Pink"),
		new NameColor(Color.yellow, "Yellow")
	};
	
	
	protected double[] percent = null;
	
	
	private PModelGraph(double[][] data) {
		super(data);
	}
	
	
	public static PModelGraph create(
			Dataset dataset, 
			String regressor, 
			String response, 
			double[] percent,
			boolean normal) {
		
		if (dataset == null || percent.length == 0)
			return null;
		
		ArrayList<PModel> modelList = new ArrayList<PModel>();
		int maxPoints = Integer.MIN_VALUE;
    	for(int i = 0; i < percent.length; i++) {
    		PModel model = normal ? 
    			PModel.normalCreate(
    				dataset, 
    				regressor, 
    				response, 
    				percent[i]) : 
    			PModel.noneNormalCreate(
    				dataset, 
    				regressor, 
    				response, 
    				percent[i],
    				false);
    		
    		if (model != null) {
    			modelList.add(model);
    			maxPoints = Math.max(maxPoints, model.getXData().length);
    		}
    	}
		
    	int nCurves = modelList.size();
    	double[][] data = data(nCurves + 1, maxPoints);
    	
    	double maxX = Double.MIN_VALUE;
    	for(int i = 0; i < nCurves; i++) {
    		PModel model = modelList.get(i);
    		
    		double[] xData = model.getXData();
    		double[] yData = model.getYData();
    		for (int j = 0; j < xData.length; j++) {
    			data[2 * i][j] = xData[j];
    			data[2 * i + 1][j] = yData[j];
    			
    			maxX = Math.max(maxX, xData[j]);
    		}
        }
    	
    	double[] responseData = dataset.getMeasureArrayByName(response);
    	double sd = Stat.standardDeviation(responseData);
    	data[2 * nCurves][0] = 0;
    	data[2 * nCurves + 1][0] = sd;
    	data[2 * nCurves][1] = maxX;
    	data[2 * nCurves + 1][1] = sd;
    	
    	PModelGraph graph = new PModelGraph(data);
    	graph.setPoint(0);
    	graph.setLine(3);
    	graph.setGraphTitle("Percentile graph for regressor \"" + 
    		regressor + "\" and response \"" + response + "\"");
    	graph.setXaxisLegend(regressor);
    	graph.setYaxisLegend(response);
    	graph.percent = percent;
    	graph.setBackground(Color.WHITE);
    	
    	return graph;
	}
	
	
	@Override
	public String getGraphFeature() {
		// TODO Auto-generated method stub
		return "";
	}


	public static PModelGraph create(
			Dataset dataset, 
			RModel rmodel, 
			double[] xDomain,
			double[] percent) {
		
		if (dataset == null || percent.length == 0 || 
				rmodel == null ||
				xDomain == null || xDomain.length == 0 || 
				rmodel.getRegressorNames().size() != 1)
			return null;
		
		ArrayList<PModel> modelList = new ArrayList<PModel>();
		int maxPoints = Integer.MIN_VALUE;
    	for(int i = 0; i < percent.length; i++) {
    		PModel model = PModel.normalCreate(
    				dataset, 
    				rmodel, 
    				xDomain, 
    				percent[i]);
    		
    		if (model != null) {
    			modelList.add(model);
    			maxPoints = Math.max(maxPoints, model.getXData().length);
    		}
    	}
		
    	int nCurves = modelList.size();
    	double[][] data = data(nCurves + 1, maxPoints);
    	
    	double maxX = Double.MIN_VALUE;
    	for(int i = 0; i < nCurves; i++) {
    		PModel model = modelList.get(i);
    		
    		double[] xData = model.getXData();
    		double[] yData = model.getYData();
    		for (int j = 0; j < xData.length; j++) {
    			data[2 * i][j] = xData[j];
    			data[2 * i + 1][j] = yData[j];
    			
    			maxX = Math.max(maxX, xData[j]);
    		}
        }
    	
    	double[] responseData = dataset.getMeasureArrayByName(rmodel.getResponse());
    	double sd = Stat.standardDeviation(responseData);
    	data[2 * nCurves][0] = 0;
    	data[2 * nCurves + 1][0] = sd;
    	data[2 * nCurves][1] = maxX;
    	data[2 * nCurves + 1][1] = sd;
    	
    	PModelGraph graph = new PModelGraph(data);
    	graph.setPoint(0);
    	graph.setLine(3);
    	graph.setGraphTitle("Percentile graph for regressor \"" + 
    		rmodel.getRegressorNames().get(0) + "\" and response \"" + rmodel.getResponse() + "\"");
    	graph.setXaxisLegend(rmodel.getRegressorNames().get(0));
    	graph.setYaxisLegend(rmodel.getResponse());
    	graph.percent = percent;
    	graph.setBackground(Color.WHITE);
    	
    	return graph;
	}

	
	@Override
	// Draw graph
	public void graph(Graphics g){

    	// Set font type and size
    	g.setFont(new Font("serif", Font.PLAIN, this.fontSize));
    	FontMetrics fm = g.getFontMetrics();

    	// calculation of all graphing parameters and data scaling
    	axesScaleOffset();

    	// Draw title, legends and axes
    	String xoffstr = offsetString(xOffset);
    	String yoffstr = offsetString(yOffset);
    	String bunit1 = "  /( ";
    	String bunit2 = " )";
    	String bunit3 = "  / ";
    	String bunit4 = " ";
    	String bunit5 = " x 10";
    	String bunit6 = "10";
    	String nounit = " ";
    	String xbrack1 = bunit1;
    	String xbrack2 = bunit2;
    	String xbrack3 = bunit5;
    	if(this.xFac==0){
    	    xbrack1 = bunit3;
    	    xbrack2 = "";
    	    xbrack3 = "";
        }
        String ybrack1 = bunit1;
    	String ybrack2 = bunit2;
    	String ybrack3 = bunit5;
    	if(this.yFac==0){
    	    ybrack1 = bunit3;
    	    ybrack2 = "";
    	    ybrack3 = "";
        }
     	if(noXunits){
    	    if(xFac==0){
        		xbrack1=nounit;
        		xbrack2=nounit;
        		xbrack3=nounit;
        	}
        	else{
        	    xbrack1=bunit3;
        		xbrack2=bunit4;
        		xbrack3=bunit6;
    		}
    	}
    	if(noYunits){
    	    if(yFac==0){
        		ybrack1=nounit;
        		ybrack2=nounit;
        		ybrack3=nounit;
        	}
        	else{
        	    ybrack1=bunit3;
        	    ybrack2=bunit4;
        	    ybrack3=bunit6;
        	}
    	}

    	double xLen=xTop-xBot;
    	double yLen=yBot-yTop;

    	// Print title
    	String sp = " + ", sn = " - ";
    	String ss=sn;
    	g.drawString(this.graphTitle+" ", 15,15);
    	g.drawString(this.graphTitle2+" ", 15,35);
    	if(this.xOffset<0){
        		ss=sp;
        		xOffset=-xOffset;
    	}

    	// Print legends
        int sw=0;
    	String ssx="", ssy="", sws1="", sws2="";
    	if(this.xFac==0 && this.xOffset==0){
            	g.drawString(this.xAxisLegend+xbrack1+this.xAxisUnits+xbrack2, xBot-4,yBot+32);
    	}
    	else{
        		if(this.xOffset==0){
            		ssx = this.xAxisLegend + xbrack1 + this.xAxisUnits + xbrack3;
                	sw = fm.stringWidth(ssx);
    	        	g.drawString(ssx, xBot-4,yBot+42);
            		sws1=String.valueOf(-this.xFac-1);
            		g.drawString(sws1, xBot-4+sw+1,yBot+32);
            		sw += fm.stringWidth(sws1);
            		g.drawString(xbrack2, xBot-4+sw+1,yBot+42);
        		}
        		else{
            		if(this.xFac==0){
                			g.drawString(this.xAxisLegend + ss + xoffstr + xbrack1+this.xAxisUnits+xbrack2, xBot-4,yBot+30);
            		}
            		else{
                			ssx = this.xAxisLegend + ss + xoffstr + xbrack1+this.xAxisUnits+xbrack3;
                			sw = fm.stringWidth(ssx);
                			g.drawString(ssx, xBot-4,yBot+37);
                			sws1 = String.valueOf(-this.xFac-1);
                			g.drawString(sws1, xBot-4+sw+1,yBot+32);
                			sw += fm.stringWidth(sws1);
                			g.drawString(xbrack2, xBot-4+sw+1,yBot+37);
            		}
        		}
    	}

    	ss=sn;
    	if(yOffset<0){
        		ss=sp;
        		yOffset=-yOffset;
    	}

    	if(yFac==0 && yOffset==0){
        		g.drawString(this.yAxisLegend+" ", 15,yTop-25);
        		g.drawString(ybrack1+this.yAxisUnits+ybrack2, 15,yTop-10);
    	}
    	else{
        		if(yOffset==0){
            		g.drawString(this.yAxisLegend, 15,yTop-35);
                	sws1 = ybrack1+this.yAxisUnits + ybrack3;
    	        	g.drawString(sws1, 15,yTop-15);
            		sw = fm.stringWidth(sws1);
            		sws2=String.valueOf(-this.yFac-1);
                	g.drawString(sws2, 15+sw+1,yTop-20);
    	        	sw += fm.stringWidth(sws2);
            		g.drawString(ybrack2, 15+sw+1,yTop-15);
       		}
        		else{
            		if(yFac==0){
                			g.drawString(this.yAxisLegend + ss + yoffstr, 15,yTop-25);
                			g.drawString(ybrack1+this.yAxisUnits+ybrack2, 15,yTop-10);
            		}
            		else{
	                    ssy = this.yAxisLegend + ss + yoffstr;
    			  	    g.drawString(ssy, 15,yTop-35);
			                sws1 = ybrack1+this.yAxisUnits + ybrack3;
                			g.drawString(sws1, 15,yTop-15);
                			sw = fm.stringWidth(sws1);
                    		sws2=String.valueOf(-this.yFac-1);
    	            		g.drawString(sws2, 15+sw+1,yTop-20);
            	    		sw += fm.stringWidth(sws2);
            		    	g.drawString(ybrack2, 15+sw+1,yTop-15);
            		}
        		}
    	}

        // Draw axes
        int zdif=0, zold=0, znew=0, zzer=0;
    	double csstep=0.0D;
        double xdenom=(xHigh-xLow);
    	double ydenom=(yHigh-yLow);

    	g.drawLine(xBot, yBot, xTop, yBot);
    	g.drawLine(xBot, yTop, xTop, yTop);
    	g.drawLine(xBot, yBot, xBot, yTop);
    	g.drawLine(xTop, yBot, xTop, yTop);


    	// Draw zero lines if drawn axes are not at zero and a zero value lies on an axis
    	if(xZero){
        		zdif=8;
        		zzer=xBot+(int)(((0.0-xLow)/xdenom)*xLen);
        		g.drawLine(zzer,yTop,zzer,yTop+8);
        		g.drawLine(zzer,yBot,zzer,yBot-8);
        		zold=yTop;
        		while(zold+zdif<yBot){
            		znew=zold+zdif;
            		g.drawLine(zzer, zold, zzer, znew);
            		zold=znew+zdif;
        		}
    	}

    	if(yZero){
        		zdif=8;
        		zzer=yBot-(int)(((0.0-yLow)/ydenom)*yLen);
        		g.drawLine(xBot,zzer,xBot+8,zzer);
        		g.drawLine(xTop,zzer,xTop-8,zzer);
        		zold=xBot;
        		while(zold+zdif<xTop){
            		znew=zold+zdif;
            		g.drawLine(zold, zzer, znew, zzer);
            		zold=znew+zdif;
        		}
    	}

     	// Draw tick marks and axis numbers
        int xt=0;
    	//double xtep=(double)(xTop-xBot)/((double)(this.xTicks-1));
    	for(int ii=0; ii<this.xTicks; ii++)
    	{
    	        xt=xBot+(int)(((this.xAxisNo[ii]-xLow)/xdenom)*xLen);
          		g.drawLine(xt,yBot,xt,yBot-8);
        		g.drawLine(xt,yTop,xt,yTop+8);
        		g.drawString(xAxisChar[ii]+" ",xt-4,yBot+18);
    	}

    	int yt=0;
    	int yCharLenMax=yAxisChar[0].length();
    	for(int ii=1; ii<this.yTicks; ii++)if(yAxisChar[ii].length()>yCharLenMax)yCharLenMax=yAxisChar[ii].length();
    	int shift = (yCharLenMax-3)*5;
    	double ytep=(double)(-yTop+yBot)/((double)(this.yTicks-1));
    	for(int ii=0; ii<this.yTicks; ii++)
    	{
        		yt=yBot-(int)Math.round(ii*ytep);
        		yt=yBot-(int)(((this.yAxisNo[ii]-yLow)/ydenom)*yLen);
        		g.drawLine(xBot,yt,xBot+8,yt);
        		g.drawLine(xTop,yt,xTop-8,yt);
        		g.drawString(yAxisChar[ii]+" ",xBot-30-shift,yt+4);
    	}

    	int dsum=0; // dashed line counter
    	boolean dcheck=true; // dashed line check

    	// Draw curves
    	int kk=0;
	    int xxp=0, yyp=0, yype=0;
	    int xoldpoint=0, xnewpoint=0, yoldpoint=0, ynewpoint=0;
    	int ps=0, psh=0, nxpoints=0;
    	double ics[]= new double[niPoints];
    	boolean btest2=true;

    	for(int i=0; i<this.nCurves; i++){
    		
    			// @new code
    			Color c = g.getColor();
    			if (i < this.nCurves - 1)
    				g.setColor(COLOR_LIST[i % COLOR_LIST.length].color);
    			else
    				g.setColor(Color.gray);
    			
        		// cubic spline interpolation option
        		nxpoints=this.nPoints[i];
        		double xcs[]= new double[nxpoints];
        		double ycs[]= new double[nxpoints];

 	       		if(lineOpt[i]==1 || lineOpt[i]==2){
            		CubicSpline cs = new CubicSpline(this.nPoints[i]);
            		for(int ii=0; ii<nxpoints; ii++){
                			xcs[ii]=this.data[kk][ii];
            		}
            		csstep=(xcs[nxpoints-1]-xcs[0])/(niPoints-1);
            		ics[0]=xcs[0];
            		for(int ii=1; ii<niPoints; ii++){
                			ics[ii]=ics[ii-1]+csstep;
            		}
            		ics[niPoints-1] = xcs[nxpoints-1];
             		for(int ii=0; ii<nxpoints; ii++){
                			ycs[ii]=this.data[kk+1][ii];
            		}

            		cs.resetData(xcs, ycs);
            		cs.calcDeriv();
            		xoldpoint=xBot+(int)(((xcs[0]-xLow)/xdenom)*xLen);
            		yoldpoint=yBot-(int)(((ycs[0]-yLow)/ydenom)*yLen);
            		for(int ii=1; ii<niPoints; ii++){
                 			xnewpoint=xBot+(int)(((ics[ii]-xLow)/xdenom)*xLen);
                			ynewpoint=yBot-(int)(((cs.interpolate(ics[ii])-yLow)/ydenom)*yLen);
                			btest2=printCheck(trimOpt[i], xoldpoint, xnewpoint, yoldpoint, ynewpoint);
                			if(btest2){
                    			if(this.lineOpt[i]==2){
                        				dsum++;
                        				if(dsum>dashLength[i]){
                            				dsum=0;
                            				if(dcheck){
                                					dcheck=false;
                            				}
							                else{
                                					dcheck=true;
                            				}
                        				}
                    			}
                    			if(dcheck)g.drawLine(xoldpoint,yoldpoint,xnewpoint,ynewpoint);
                			}
                			xoldpoint=xnewpoint;
                			yoldpoint=ynewpoint;
            		}
        		}

        		if(lineOpt[i]==-1 || lineOpt[i]==-2){
            		CubicSpline cs = new CubicSpline(this.nPoints[i]);
            		for(int ii=0; ii<nxpoints; ii++){
                			xcs[ii]=this.data[kk][ii];
            		}
            		for(int ii=0; ii<nxpoints; ii++){
                			ycs[ii]=this.data[kk+1][ii];
            		}
            		csstep=(ycs[nxpoints-1]-ycs[0])/(niPoints-1);
            		ics[0]=ycs[0];
            		for(int ii=1; ii<niPoints; ii++){
                			ics[ii]=ics[ii-1]+csstep;
            		}
            		ics[niPoints-1] = ycs[nxpoints-1];

            		cs.resetData(ycs, xcs);
            		cs.calcDeriv();
            		xoldpoint=xBot+(int)(((xcs[0]-xLow)/xdenom)*xLen);
            		yoldpoint=yBot-(int)(((ycs[0]-yLow)/ydenom)*yLen);
            		for(int ii=1; ii<niPoints; ii++){
                			ynewpoint=yBot+(int)(((ics[ii]-yLow)/ydenom)*yLen);
                			xnewpoint=xBot-(int)(((cs.interpolate(ics[ii])-xLow)/xdenom)*xLen);
                			btest2=printCheck(trimOpt[i], xoldpoint, xnewpoint, yoldpoint, ynewpoint);
                			if(btest2){
                    			if(this.lineOpt[i]==2){
                        				dsum++;
                        				if(dsum>dashLength[i]){
                            				dsum=0;
                            				if(dcheck){
                                					dcheck=false;
                            				}
							else{
                                					dcheck=true;
                            				}
                        				}
                    			}
                    			if(dcheck)g.drawLine(xoldpoint,yoldpoint,xnewpoint,ynewpoint);
                			}
                			xoldpoint=xnewpoint;
                			yoldpoint=ynewpoint;
            		}
        		}

            	if(lineOpt[i]==3){
            		// Join points option
            		dsum=0;
            		dcheck=true;
            		xoldpoint=xBot+(int)((((this.data[kk][0])-xLow)/xdenom)*xLen);
            		yoldpoint=yBot-(int)((((this.data[kk+1][0])-yLow)/ydenom)*yLen);
            		for(int ii=1; ii<nxpoints; ii++){
                			xnewpoint=xBot+(int)((((this.data[kk][ii])-xLow)/xdenom)*xLen);
                			ynewpoint=yBot-(int)((((this.data[kk+1][ii])-yLow)/ydenom)*yLen);
                			btest2=printCheck(trimOpt[i], xoldpoint, xnewpoint, yoldpoint, ynewpoint);
                			if(btest2)g.drawLine(xoldpoint,yoldpoint,xnewpoint,ynewpoint);
                			xoldpoint=xnewpoint;
                			yoldpoint=ynewpoint;
            		}
        		}

                if(lineOpt[i]==4){
            	    // Join points with dotted line option

            		// lines between points
            		int[] lengths = new int[nxpoints-1];
            		double[] gradients = new double[nxpoints-1];
            		double[] intercepts = new double[nxpoints-1];
            		int totalLength = 0;
            		xoldpoint=xBot+(int)((((this.data[kk][0])-xLow)/xdenom)*xLen);
            		yoldpoint=yBot-(int)((((this.data[kk+1][0])-yLow)/ydenom)*yLen);
            		for(int ii=1; ii<nxpoints; ii++){
                			xnewpoint=xBot+(int)((((this.data[kk][ii])-xLow)/xdenom)*xLen);
                			ynewpoint=yBot-(int)((((this.data[kk+1][ii])-yLow)/ydenom)*yLen);
                			lengths[ii-1] = (int)Fmath.hypot((double)(xnewpoint-xoldpoint), (double)(ynewpoint-yoldpoint));
                			totalLength += lengths[ii-1];
                			gradients[ii-1] = (double)(ynewpoint-yoldpoint)/(double)(xnewpoint-xoldpoint);
                			intercepts[ii-1] = (double)yoldpoint - gradients[ii-1]*xoldpoint;
                			xoldpoint=xnewpoint;
                			yoldpoint=ynewpoint;
            		}

            		// number of points
            		int incrmt = totalLength/(4*niPoints-1);
            		int nlpointsold = 0;
            		int nlpointsnew = 0;
           		    int totalLpoints = 1;
            		for(int ii=1; ii<nxpoints; ii++){
            		    totalLpoints++;
            		    nlpointsnew = lengths[ii-1]/incrmt;
              	        for(int jj = nlpointsold+1; jj<(nlpointsnew + nlpointsold); jj++)totalLpoints ++;
                	    nlpointsold = nlpointsold + nlpointsnew;
                    }

                    // fill arrays
            		int[] xdashed = new int[totalLpoints];
            		int[] ydashed = new int[totalLpoints];
            		nlpointsold = 0;
            		nlpointsnew = 0;
            		xdashed[0] = xBot+(int)((((this.data[kk][0])-xLow)/xdenom)*xLen);
            		ydashed[0] = yBot-(int)((((this.data[kk+1][0])-yLow)/ydenom)*yLen);
            		for(int ii=1; ii<nxpoints; ii++){
            		    nlpointsnew = lengths[ii-1]/incrmt;
            		    xdashed[nlpointsnew + nlpointsold] = xBot+(int)((((this.data[kk][ii])-xLow)/xdenom)*xLen);
                		ydashed[nlpointsnew + nlpointsold] = yBot-(int)((((this.data[kk+1][ii])-yLow)/ydenom)*yLen);
                	    if(Math.abs(gradients[ii-1])>0.5){
                	        int diff = (ydashed[nlpointsnew + nlpointsold] - ydashed[nlpointsold])/nlpointsnew;
                	        for(int jj = nlpointsold+1; jj<(nlpointsnew + nlpointsold); jj++){
                	            ydashed[jj] = ydashed[jj-1]+diff;
                	            if(Fmath.isInfinity(Math.abs(gradients[ii-1]))){
                	                xdashed[jj] = xdashed[nlpointsnew + nlpointsold];
                	            }
                	            else{
                	                xdashed[jj] = (int)(((double)ydashed[jj] - intercepts[ii-1])/gradients[ii-1]);
                	            }
                	        }
                	    }
                	    else{
                	       int diff = (xdashed[nlpointsnew + nlpointsold] - xdashed[nlpointsold])/nlpointsnew;
                	       for(int jj = nlpointsold+1; jj<(nlpointsnew + nlpointsold); jj++){
                	            xdashed[jj] = xdashed[jj-1]+diff;
                	            ydashed[jj] = (int)(gradients[ii-1]*ydashed[jj] + intercepts[ii-1]);
                	        }
                	    }
                	    nlpointsold = nlpointsold + nlpointsnew;
                	}

                	dsum=0;
                    dcheck=true;
           	        for(int ii=1; ii<totalLpoints; ii++){
       			    dsum++;
                        if(dsum>dashLength[i]){
                            dsum=0;
                            if(dcheck){
                                dcheck=false;
                            }
							else{
                                dcheck=true;
                            }
                        }
                    	if(dcheck)g.drawLine(xdashed[ii-1],ydashed[ii-1],xdashed[ii],ydashed[ii]);
            	    }
        		}



        		// Plot points
        		if(pointOpt[i]>0){
            		for(int ii=0; ii<nxpoints; ii++){
                			ps=this.pointSize[i];
                			psh=ps/2;
                 			xxp=xBot+(int)(((this.data[kk][ii]-xLow)/xdenom)*xLen);
                			yyp=yBot-(int)(((this.data[kk+1][ii]-yLow)/ydenom)*yLen);
                			switch(pointOpt[i]){
                    			case 1: g.drawOval(xxp-psh, yyp-psh, ps, ps);
                        				break;
                    			case 2: g.drawRect(xxp-psh, yyp-psh, ps, ps);
                        				break;
                    			case 3: g.drawLine(xxp-psh, yyp, xxp, yyp+psh);
                        				g.drawLine(xxp, yyp+psh, xxp+psh, yyp);
                        				g.drawLine(xxp+psh, yyp, xxp, yyp-psh);
                        				g.drawLine(xxp, yyp-psh, xxp-psh, yyp);
                        				break;
                      			case 4: g.fillOval(xxp-psh, yyp-psh, ps, ps);
                        				break;
                    			case 5: g.fillRect(xxp-psh, yyp-psh, ps, ps);
                        				break;
                    			case 6: for(int jj=0; jj<psh; jj++)g.drawLine(xxp-jj, yyp-psh+jj, xxp+jj, yyp-psh+jj);
                        				for(int jj=0; jj<=psh; jj++)g.drawLine(xxp-psh+jj, yyp+jj, xxp+psh-jj, yyp+jj);
                        				break;
                    			case 7: g.drawLine(xxp-psh, yyp-psh, xxp+psh, yyp+psh);
                        				g.drawLine(xxp-psh, yyp+psh, xxp+psh, yyp-psh);
                        				break;
                    			case 8: g.drawLine(xxp-psh, yyp, xxp+psh, yyp);
                        				g.drawLine(xxp, yyp+psh, xxp, yyp-psh);
                        				break;
                    			default:g.drawLine(xxp-psh, yyp-psh, xxp+psh, yyp+psh);
                        				g.drawLine(xxp-psh, yyp+psh, xxp+psh, yyp-psh);
                        				break;
                			}

				if(this.errorBar[i]){
                    			yype=yBot-(int)(((errors[i][ii]-yLow)/ydenom)*yLen);
                    			g.drawLine(xxp, yyp, xxp, yype);
                    			g.drawLine(xxp-4, yype, xxp+4, yype);
                    			yype=2*yyp-yype;
                    			g.drawLine(xxp, yyp, xxp, yype);
                    			g.drawLine(xxp-4, yype, xxp+4, yype);
                			}
            		}
        		}
        		kk+=2;
        		
        		g.setColor(c);
    	}
	}
	
	
	
}
