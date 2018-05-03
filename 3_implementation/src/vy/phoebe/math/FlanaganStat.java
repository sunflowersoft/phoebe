package vy.phoebe.math;

import flanagan.analysis.Stat;
import flanagan.math.Fmath;
import flanagan.plot.PlotGraph;

public class FlanaganStat extends Stat {

    // Distribute data into bins to obtain histogram and plot histogram
    // zero bin position and upper limit provided
    public static PlotGraph histogramBinsPlot2(double[] data, double binWidth, double binZero, double binUpper){
        String xLegend = null;
        return histogramBinsPlot2(data, binWidth, binZero, binUpper, xLegend);
    }

    // Distribute data into bins to obtain histogram and plot histogram
    // zero bin position, upper limit and x-axis legend provided
    public static PlotGraph histogramBinsPlot2(double[] data, double binWidth, double binZero, double binUpper, String xLegend){
        int n = 0;              // new array length
        int m = data.length;    // old array length;
        for(int i=0; i<m; i++)if(data[i]<=binUpper)n++;
        if(n!=m){
            double[] newData = new double[n];
            int j = 0;
            for(int i=0; i<m; i++){
                if(data[i]<=binUpper){
                    newData[j] = data[i];
                    j++;
                }
            }
            System.out.println((m-n)+" data points, above histogram upper limit, excluded in Stat.histogramBins");
            return histogramBinsPlot2(newData, binWidth, binZero, xLegend);
        }
        else{
             return histogramBinsPlot2(data, binWidth, binZero, xLegend);

        }
    }

    // Distribute data into bins to obtain histogram and plot the histogram
    // zero bin position provided
    public static PlotGraph histogramBinsPlot2(double[] data, double binWidth, double binZero){
        String xLegend = null;
        return histogramBinsPlot2(data, binWidth, binZero, xLegend);
    }

    // Distribute data into bins to obtain histogram and plot the histogram
    // zero bin position and x-axis legend provided
    public static PlotGraph histogramBinsPlot2(double[] data, double binWidth, double binZero, String xLegend){
        double[][] results = histogramBins(data, binWidth, binZero);
        int nBins = results[0].length;
        int nPoints = nBins*3+1;
        double[][] cdata = PlotGraph.data(1, nPoints);
        cdata[0][0]=binZero;
        cdata[1][0]=0.0D;
        int k=1;
        for(int i=0; i<nBins; i++){
            cdata[0][k]=cdata[0][k-1];
            cdata[1][k]=results[1][i];
            k++;
            cdata[0][k]=cdata[0][k-1]+binWidth;
            cdata[1][k]=results[1][i];
            k++;
            cdata[0][k]=cdata[0][k-1];
            cdata[1][k]=0.0D;
            k++;
        }

        PlotGraph pg = new PlotGraph(cdata);
        pg.setGraphTitle("Histogram:  Bin Width = "+binWidth);
        pg.setLine(3);
        pg.setPoint(0);
        pg.setYaxisLegend("Frequency");
        if(xLegend!=null)pg.setXaxisLegend(xLegend);

        return pg;
    }

    // Distribute data into bins to obtain histogram and plot the histogram
    // zero bin position calculated
    public static PlotGraph histogramBinsPlot2(double[] data, double binWidth){
        String xLegend = null;
        return histogramBinsPlot2(data, binWidth, xLegend);
    }

    // Distribute data into bins to obtain histogram and plot the histogram
    // zero bin position calculated, x-axis legend provided
    public static PlotGraph histogramBinsPlot2(double[] data, double binWidth, String xLegend){
        double dmin = Fmath.minimum(data);
        double dmax = Fmath.maximum(data);
        double span = dmax - dmin;
        int nBins = (int) Math.ceil(span/binWidth);
        double rem = ((double)nBins)*binWidth-span;
        double binZero =dmin-rem/2.0D;
        return histogramBinsPlot2(data, binWidth, binZero, xLegend);
    }


}
