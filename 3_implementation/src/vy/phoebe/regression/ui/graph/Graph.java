package vy.phoebe.regression.ui.graph;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.print.Printable;


public interface Graph extends Printable {

	
	void setupViewOption();
	
	
	void paint(Graphics g);
	
	
	void exportImage();
	
	
	Rectangle getOuterBox();
	
	
	GraphViewOption getViewOption();
}


class GraphViewOption {
	
	public static final double BUFFER_RATIO = 0.1;

	public static final int TICK = 10;

	public static final int ROUND = 2;
	
	protected static final int DECIMAL = 0; // Note number length = ROUND + DECIMAL

	
	public int xRound = ROUND;
	
	
	public int yRound = ROUND;

	
	public int zRound = ROUND;

	
	public int fRound = ROUND;

	
	public String graphTitle = "";
	
	
	public String[] legends = new String[0];

	
	public double bufferRatio = BUFFER_RATIO;

	
	public boolean simplest = false;
	
	
	public GraphViewOption() {
		
	}
	
	
	public GraphViewOption(int xRound, int fRound) {
		this(xRound, ROUND, fRound);
	}
	
	
	public GraphViewOption(int xRound, int yRound, int fRound) {
		this(xRound, yRound, ROUND, fRound);
	}

	
	public GraphViewOption(int xRound, int yRound, int zRound, int fRound) {
		this.xRound = xRound;
		this.yRound = yRound;
		this.zRound = zRound;
		this.fRound = fRound;
	}
	
	
	public void copy(GraphViewOption that) {
		this.xRound = that.xRound;
		this.yRound = that.yRound;
		this.zRound = that.zRound;
		this.fRound = that.fRound;
		
		this.graphTitle = that.graphTitle;
		if (this.graphTitle == null)
			this.graphTitle = "";
		
		this.legends = that.legends;
		if (this.legends == null)
			this.legends = new String[] { };
		
		this.bufferRatio = that.bufferRatio;
		this.simplest = that.simplest;
	}
	
	
}


class GraphLegendExp {
	
	
	public double v = 1;
	
	
	public String text = "";
	
	
	private GraphLegendExp(double v, String text) {
		this.v = v;
		this.text = text;
	}
	
	
	public static GraphLegendExp create(double number, int round) {
		
		int d = (int) (number + 0.5);
		String dText = String.valueOf(d);
		int k = dText.length() - round;
		
		if (k == 0) {
			return new GraphLegendExp(1, "");
		}
		else if ( k > 0) {
			return new GraphLegendExp(
					1.0 / Math.pow(10, k),
					k == 1 ? " / 10" : " / 10^" + k
				);
		}
		else {
			k = Math.abs(k);
			return new GraphLegendExp(
					Math.pow(10, k),
					k == 1 ? " * 10" : " * 10^" + k
				);
		}
		
	}
	
	
}
