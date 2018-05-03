package vy.phoebe.regression.ui.graph;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;

import vy.phoebe.math.MathUtil;
import vy.phoebe.regression.RModel;
import vy.phoebe.util.DSUtil;
import vy.phoebe.util.MinMax;


public abstract class Drawer2d {

	
	protected double[] xDomain = null;
	
	
	protected double[] fDomain = null;

	
	protected Rectangle outerBox = new Rectangle();
	
	
	protected Rectangle innerBox = new Rectangle();

	
	protected GraphViewOption viewOption = new GraphViewOption();
	
	
	public Drawer2d(double[] xDomain, double[] fDomain) {
		this.xDomain = xDomain;
		this.fDomain = fDomain;
	}
	
	
	public abstract RModel getModel();
	
	
	public abstract String getXLegend();
	
	
	public abstract String getFLegend();
	
	
	public abstract double eval(double xValue);
	
	
	public void draw(Graphics g) {
		// TODO Auto-generated method stub
		
		Graphics2D g2d = (Graphics2D)g;
		drawFrame(g2d);
		drawCurve(g2d);
	}
	
	
	protected void drawCurve(Graphics2D g) {
		MinMax xRange = MinMax.create(xDomain);
		MinMax fRange = MinMax.create(fDomain);

		Point pre = null;
		for (int i = 0; i < xDomain.length; i++) {
			double xValue = xDomain[i];
			int x = MathUtil.translate(innerBox, xValue, fDomain[0], xRange, fRange).x;
			
			double fValue = eval(xValue);
			if (!DSUtil.isUsed(fValue) || !fRange.contains(fValue)) {
				pre = null;
				continue;
			}
			
			int y = MathUtil.translate(innerBox, xDomain[0], fValue, xRange, fRange).y;
			
			Color c = g.getColor();
			Stroke s = g.getStroke();
			//
			BasicStroke stroke = new BasicStroke(1.5f);
			g.setStroke(stroke);
			g.setColor(new Color(1f, 0f, 0f));
			if (pre != null)
				g.drawLine(pre.x, pre.y, x, y);
			else
				g.drawLine(x, y, x, y);
			//
			g.setStroke(s);
			g.setColor(c);
			
			pre = new Point(x, y);
			
		} // i
	}
	
	
	protected void drawFrame(Graphics2D g) {
		if (viewOption.simplest) {
			// Drawing inner box
			g.drawRect(innerBox.x, innerBox.y, innerBox.width, innerBox.height);
			return;
		}
		
		FontMetrics fontMetrics = g.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		
		MinMax xRange = MinMax.create(xDomain);
		MinMax fRange = MinMax.create(fDomain);
		
		// Drawing x frame
		GraphLegendExp xExp = GraphLegendExp.create(xRange.max(), viewOption.xRound);
		for (int i = 0; i < xDomain.length; i++) {
			double xValue = xDomain[i];
			String text = MathUtil.format(xValue * xExp.v, GraphViewOption.DECIMAL);
			Point point = MathUtil.translate(innerBox, xValue, fDomain[0], xRange, fRange);
			g.drawString(text, point.x - fontMetrics.stringWidth(text) / 2, point.y + textHeight);
			// long tick
			g.drawLine(point.x, point.y - 4, point.x, point.y + 4);
			
			if (i < xDomain.length - 1) {
				double interval = (xDomain[i + 1] - xValue) / GraphViewOption.TICK;
				for (int j = 1 ; j < GraphViewOption.TICK && interval > 0; j++) {
					Point p = MathUtil.translate(innerBox, xValue + j * interval, fDomain[0], xRange, fRange);
					
					// short tick
					g.drawLine(p.x, p.y - 2, p.x, p.y + 2);
					
					Stroke s = g.getStroke();
					Color c = g.getColor();
					BasicStroke stroke = new BasicStroke(0.1f);
					g.setStroke(stroke);
					g.setColor(new Color(0.5f, 0.5f, 0.5f));
					// Drawing vertical dash x line
					g.drawLine(p.x, p.y - innerBox.height, p.x, p.y);
					//
					g.setColor(c);
					g.setStroke(s);

					
				}
			}
			
			if (point.x > innerBox.x && point.x < innerBox.x + innerBox.width)
				g.drawLine(point.x, point.y - innerBox.height, point.x, point.y);
		}
			
		String xName = getXLegend() + xExp.text; 
		g.drawString(xName, innerBox.x + innerBox.width / 2 - fontMetrics.stringWidth(xName) / 2, 
				innerBox.y + innerBox.height + 2 * textHeight);

		
		// Drawing f frame
		GraphLegendExp fExp = GraphLegendExp.create(fRange.max(), viewOption.fRound);
		for (int i = 0; i < fDomain.length; i++) {
			double fValue = fDomain[i];
			String text = MathUtil.format(fValue * fExp.v, GraphViewOption.DECIMAL);
			Point point = MathUtil.translate(innerBox, xDomain[0], fValue, xRange, fRange);
			g.drawString(text, point.x - fontMetrics.stringWidth(text) - 4, point.y);
			// long tick
			g.drawLine(point.x - 4, point.y, point.x + 4, point.y);
			
			if (i < fDomain.length - 1) {
				double interval = (fDomain[i + 1] - fValue) / GraphViewOption.TICK;
				for (int j = 1 ; j < GraphViewOption.TICK && interval > 0; j++) {
					Point p = MathUtil.translate(innerBox, xDomain[0], fValue + j * interval, xRange, fRange);
					
					// short tick
					g.drawLine(p.x - 2, p.y, p.x + 2, p.y);
					
					Stroke s = g.getStroke();
					Color c = g.getColor();
					BasicStroke stroke = new BasicStroke(0.1f);
					g.setStroke(stroke);
					g.setColor(new Color(0.5f, 0.5f, 0.5f));
					// Drawing horizontal dash f line
					g.drawLine(p.x - 2, p.y, p.x + innerBox.width, p.y);
					//
					g.setColor(c);
					g.setStroke(s);

				}
			}
			
			if (point.y > innerBox.y && point.y < innerBox.y + innerBox.height)
				g.drawLine(point.x, point.y, point.x + innerBox.width, point.y);
		}

		
		//
		String fName = getFLegend() + fExp.text; 
		Drawer2d.drawText(g, fName, 
				outerBox.x + textHeight, 
				outerBox.y + outerBox.height / 2 - fontMetrics.stringWidth(fName) / 2,
				Math.PI/2);		

		// Drawing inner box
		g.drawRect(innerBox.x, innerBox.y, innerBox.width, innerBox.height);
	}
	
	
	public void updateBox(int width, int height) {
		updateBox(new Rectangle(0, 0, width, height));
		
	}

	
	public void updateBox(Rectangle box) {
		outerBox.setBounds(box.x, box.y, box.width, box.height);
		
		int wBuffer = (int) (box.width * viewOption.bufferRatio);
		int hBuffer = (int) (box.height * viewOption.bufferRatio);
		innerBox.setBounds(
				box.x + wBuffer, 
				box.y + hBuffer, 
				box.width - 2 * wBuffer, 
				box.height - 2 * hBuffer);
	}
	
	
	public Rectangle getOuterBox() {
		return outerBox;
	}
	
	
	public GraphViewOption getViewOption() {
		return viewOption;
	}
	
	
	public static void drawText(Graphics2D g, String text, int x, int y, double rotateAngle) {
		AffineTransform  saveTf = g.getTransform();
		g.rotate(rotateAngle, x, y);
		g.drawString(text, x, y);
		g.setTransform(saveTf);
	}
	
	
}






