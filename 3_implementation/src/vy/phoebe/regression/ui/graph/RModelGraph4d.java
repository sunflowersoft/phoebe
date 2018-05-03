package vy.phoebe.regression.ui.graph;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import vy.phoebe.math.MathUtil;
import vy.phoebe.regression.RModel;
import vy.phoebe.util.DSUtil;
import vy.phoebe.util.FileUtil;
import vy.phoebe.util.MinMax;
import vy.phoebe.util.MiscUtil;


public class RModelGraph4d extends JPanel implements Graph {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected RModel f = null;
	
	
	protected double[] xDomain = null;
	
	
	protected double[] yDomain = null;
	
	
	protected double[] zDomain = null;
	
	
	protected double[] fDomain = null;

	
	protected Rectangle outerBox = new Rectangle();
	
	
	protected Rectangle innerBox = new Rectangle();

	
	protected GraphViewOption viewOption = new GraphViewOption(); 
	
	
	private RModelGraph4d(RModel f, double[] xDomain, double[] yDomain, double[] zDomain, double[] fDomain) {
		this.f = f;
		this.xDomain = xDomain;
		this.yDomain = yDomain;
		this.zDomain = zDomain;
		this.fDomain = fDomain;
	}
	
	
	public RModel getModel() {
		return f;
	
	}
	
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		updateBox();
		
		Graphics2D g2d = (Graphics2D)g;
		drawFrame(g2d);
		drawCurve(g2d);
		
	}

	
	@Override
	public void setupViewOption() {
		RModelGraph4dViewOptionDlg dlg = new RModelGraph4dViewOptionDlg(this, this.viewOption);
		if (dlg.getViewOption() != null) {
			this.viewOption = dlg.getViewOption();
			
			updateUI();
		}
	}

	
	private void drawCurve(Graphics2D g) {
		
		MinMax xRange = MinMax.create(xDomain);
		MinMax yRange = MinMax.create(yDomain);
		
		int subWidth = innerBox.width / xDomain.length;
		int subHeight = innerBox.height / yDomain.length;
		
		for (int i = 1; i < xDomain.length - 1; i++) {
			final double xValue = xDomain[i];
			
			for (int j = 1; j < yDomain.length - 1; j++) {
				final double yValue = yDomain[j];
				Point coord = MathUtil.translate(innerBox, xDomain[0], yDomain[0], xRange, yRange);
				Point point = MathUtil.translate(innerBox, xValue, yValue, xRange, yRange);
				
				Stroke s = g.getStroke();
				Color c = g.getColor();
				BasicStroke stroke = new BasicStroke(
						0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 2 }, 0.0f);
				g.setStroke(stroke);
				g.setColor(new Color(0.5f, 0.5f, 0.5f));
				// vertical x line
				if (j == 1)
					g.drawLine(point.x, point.y + subHeight / 2, point.x, coord.y);
				else if (j == yDomain.length - 2)
					g.drawLine(point.x, point.y - subHeight / 2, point.x, coord.y - innerBox.height);
				// horizontal y line
				if (i == 1)
					g.drawLine(point.x - subWidth / 2, point.y, coord.x, point.y);
				else if (i == xDomain.length - 2)
					g.drawLine(point.x + subWidth / 2, point.y, coord.x + innerBox.width, point.y);
				//
				g.setColor(c);
				g.setStroke(s);

				
				Drawer2d drawer = new Drawer2d(zDomain, fDomain) {
					
					@Override
					public String getXLegend() {
						// TODO Auto-generated method stub
						if (this.viewOption.legends != null && this.viewOption.legends.length > 3)
							return this.viewOption.legends[2];
						else
							return f.getRegressorNames().get(2);
					}
					
					@Override
					public RModel getModel() {
						// TODO Auto-generated method stub
						return f;
					}
					
					@Override
					public String getFLegend() {
						// TODO Auto-generated method stub
						if (this.viewOption.legends != null && this.viewOption.legends.length > 3)
							return this.viewOption.legends[3];
						else
							return f.getResponse();
					}
					
					@Override
					public double eval(double zValue) {
						// TODO Auto-generated method stub
						HashMap<String, Double> params = new HashMap<String, Double>();
						params.put(f.getRegressorNames().get(0), xValue);
						params.put(f.getRegressorNames().get(1), yValue);
						params.put(f.getRegressorNames().get(2), zValue);
						
						return f.eval(params);
					}
				}; 
				Rectangle box2d = new Rectangle(point.x - subWidth / 2, point.y - subHeight / 2, subWidth, subHeight);
				drawer.updateBox(box2d);
				
				GraphViewOption option = new GraphViewOption(viewOption.xRound, viewOption.fRound);
				option.simplest = true;
				drawer.viewOption = option;
				drawer.draw(g);
			}
		}
		
		
	}
	
	
	private void drawFrame(Graphics2D g) {
		FontMetrics fontMetrics = g.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		
		MinMax xRange = MinMax.create(xDomain);
		MinMax yRange = MinMax.create(yDomain);

		// Drawing inner box
		g.drawRect(innerBox.x, innerBox.y, innerBox.width, innerBox.height);

		// Drawing x ticks
		GraphLegendExp xExp = GraphLegendExp.create(xRange.max(), viewOption.xRound);
		for (int i = 0; i < xDomain.length; i++) {
			double xValue = xDomain[i];
			String text = MathUtil.format(xValue * xExp.v, GraphViewOption.DECIMAL);
			Point point = MathUtil.translate(innerBox, xValue, yDomain[0], xRange, yRange);
			g.drawString(text, point.x - fontMetrics.stringWidth(text) / 2, point.y + textHeight);
			// long tick
			g.drawLine(point.x, point.y - 4, point.x, point.y + 4);
			
			if (i < xDomain.length - 1) {
				double interval = (xDomain[i + 1] - xValue) / GraphViewOption.TICK;
				for (int j = 1 ; j < GraphViewOption.TICK && interval > 0; j++) {
					Point p = MathUtil.translate(innerBox, xValue + j * interval, yDomain[0], xRange, yRange);
					// short tick
					g.drawLine(p.x, p.y - 2, p.x, p.y + 2);
				}
			}
			
		}
		//
		String xName = (viewOption.legends.length > 3 ? viewOption.legends[0] : f.getRegressorNames().get(0)) + xExp.text; 
		g.drawString(xName, innerBox.x + innerBox.width / 2 - fontMetrics.stringWidth(xName) / 2, 
				innerBox.y + innerBox.height + 2 * textHeight);

		
		// Drawing y ticks
		GraphLegendExp yExp = GraphLegendExp.create(yRange.max(), viewOption.yRound);
		for (int i = 0; i < yDomain.length; i++) {
			double yValue = yDomain[i];
			String text = MathUtil.format(yValue * yExp.v, GraphViewOption.DECIMAL);
			Point point = MathUtil.translate(innerBox, xDomain[0], yValue, xRange, yRange);
			g.drawString(text, point.x - fontMetrics.stringWidth(text) - 4, point.y);
			// long tick
			g.drawLine(point.x - 4, point.y, point.x + 4, point.y);
			
			if (i < yDomain.length - 1) {
				double interval = (yDomain[i + 1] - yValue) / GraphViewOption.TICK;
				for (int j = 1 ; j < GraphViewOption.TICK && interval > 0; j++) {
					Point p = MathUtil.translate(innerBox, xDomain[0], yValue + j * interval, xRange, yRange);
					
					// short tick
					g.drawLine(p.x - 2, p.y, p.x + 2, p.y);
					
				}
			}
			
		}
		//
		String yName = (viewOption.legends.length > 3 ? viewOption.legends[1] : f.getRegressorNames().get(1)) + yExp.text; 
		Drawer2d.drawText(g, yName, 
				outerBox.x + textHeight, 
				outerBox.y + outerBox.height / 2 - fontMetrics.stringWidth(yName) / 2,
				Math.PI / 2);		

	
	}
	
	
	private void updateBox() {
		updateBox(getWidth(), getHeight());
	}
	
	
	private void updateBox(int width, int height) {
		outerBox.setBounds(0, 0, width, height);
		
		int wBuffer = (int) (width * viewOption.bufferRatio);
		int hBuffer = (int) (height * viewOption.bufferRatio);
		innerBox.setBounds(
				wBuffer, 
				hBuffer, 
				width - 2 * wBuffer, 
				height - 2 * hBuffer);
	}

	
	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex) {
		// TODO Auto-generated method stub
		//assume the page exists until proven otherwise
		int retval = Printable.PAGE_EXISTS;
		
		//We only want to deal with the first page.
		//The first page is numbered '0'
		if (pageIndex > 0){
			retval = Printable.NO_SUCH_PAGE;
		} else {
			//setting up the Graphics object for printing
			g.translate((int)(pf.getImageableX()), (int)(pf.getImageableY()));
	    		//populate the Graphics object from HelloPrint's paint() method
			
			double width = pf.getImageableWidth();
			double height = pf.getImageableHeight(); 
			updateBox((int)width, (int)height);
			Graphics2D g2d = (Graphics2D)g;
			drawFrame(g2d);
			drawCurve(g2d);
			
			updateBox();
		}
	    return retval;
	}


	@Override
	public void exportImage() {
		File chooseFile = FileUtil.chooseFile2(
				this, false, new String[] { "png" }, new String[] { "PNG file (*.png)"}, "png");
		if (chooseFile == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Image not exported", 
					"Image not exported", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = image.createGraphics();
		paint(g);
		try {
			ImageIO.write(image, "png", chooseFile);
			JOptionPane.showMessageDialog(
					this, 
					"Image exported successfully", 
					"Image exported successfully", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	
	@Override
	public Rectangle getOuterBox() {
		// TODO Auto-generated method stub
		return outerBox;
	}


	@Override
	public GraphViewOption getViewOption() {
		// TODO Auto-generated method stub
		return viewOption;
	}


	public static RModelGraph4d create(
			RModel f, 
			double[] xDomain, 
			double[] yDomain, 
			double[] zDomain,
			double[] fDomain) {
		
		if (f == null || f.getRegressorNames().size() != 3 || 
				xDomain == null || yDomain == null || zDomain == null || fDomain == null ||
				xDomain.length == 0 || yDomain.length == 0 || zDomain.length == 0 || fDomain.length == 0)
			return null;
		
		return new RModelGraph4d(f, xDomain, yDomain, zDomain, fDomain);
	}
	
	
	

}



class RModelGraph4dViewOptionDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	JFormattedTextField xRound = null;
	
	
	JFormattedTextField yRound = null;

	
	JFormattedTextField zRound = null;

	
	JFormattedTextField fRound = null;

	
	JTextField legends = null;

	
	JFormattedTextField bufferRatio = null;

	
	GraphViewOption returnViewOption = null;
	
	
	public RModelGraph4dViewOptionDlg(Component comp) {
		this(comp, null);
	}
	
	
	public RModelGraph4dViewOptionDlg(Component comp, GraphViewOption option) {
		super (MiscUtil.getFrameForComponent(comp), "Dual graph option", true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 200);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("X round: "));
		left.add(new JLabel("Y round: "));
		left.add(new JLabel("Z round: "));
		left.add(new JLabel("Model round: "));
		left.add(new JLabel("Legends: "));
		left.add(new JLabel("Buffer ratio: "));
		
		JPanel center = new JPanel(new GridLayout(0, 1));
		header.add(center, BorderLayout.CENTER);

		xRound = new JFormattedTextField(new NumberFormatter());
		if (option != null)
			xRound.setValue(option.xRound);
		else
			xRound.setValue(GraphViewOption.ROUND);
		center.add(xRound);
		
		yRound = new JFormattedTextField(new NumberFormatter());
		if (option != null)
			yRound.setValue(option.yRound);
		else
			yRound.setValue(GraphViewOption.ROUND);
		center.add(yRound);
		
		zRound = new JFormattedTextField(new NumberFormatter());
		if (option != null)
			zRound.setValue(option.zRound);
		else
			zRound.setValue(GraphViewOption.ROUND);
		center.add(zRound);

		fRound = new JFormattedTextField(new NumberFormatter());
		if (option != null)
			fRound.setValue(option.fRound);
		else
			fRound.setValue(GraphViewOption.ROUND);
		center.add(fRound);
		
		legends = new JTextField();
		if (option != null && option.legends != null && option.legends.length > 3) {
			StringBuffer buffer = new StringBuffer();
			for (int i = 0; i < option.legends.length; i++) {
				if (i > 0)
					buffer.append(",");
				
				buffer.append(option.legends[i]);
			}
			legends.setText(buffer.toString());
		}
		else
			legends.setText("");
		center.add(legends);

		bufferRatio = new JFormattedTextField(new NumberFormatter());
		if (option != null)
			bufferRatio.setValue(option.bufferRatio);
		else
			bufferRatio.setValue(GraphViewOption.BUFFER_RATIO);
		center.add(bufferRatio);

		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		footer.add(ok);
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				returnViewOption = null;
				if (validateValues()) {
					returnViewOption = new GraphViewOption();
					returnViewOption.xRound = ((Number) xRound.getValue()).intValue();
					returnViewOption.yRound = ((Number) yRound.getValue()).intValue();
					returnViewOption.zRound = ((Number) zRound.getValue()).intValue();
					returnViewOption.fRound = ((Number) fRound.getValue()).intValue();
					
					String legendText = legends.getText().trim();
					List<String> list = DSUtil.split(legendText, ",", null);
					if (list.size() > 3)
						returnViewOption.legends = list.toArray(new String[] { });
					else
						returnViewOption.legends = new String[] { };
					
					returnViewOption.bufferRatio = ((Number) bufferRatio.getValue()).doubleValue();
				}
				dispose();
			}
		});
		
		
		JButton cancel = new JButton("Cancel");
		footer.add(cancel);
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				returnViewOption = null;
				dispose();
			}
		});

		
		setVisible(true);
	}
	
	
	public GraphViewOption getViewOption() {
		return returnViewOption;
	}
	
	
	private boolean validateValues() {
		if (xRound.getValue() == null || 
				yRound.getValue() == null || 
				fRound.getValue() == null) {
			
			JOptionPane.showMessageDialog(
					this, 
					"Empty values", 
					"Empty values", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		int xround = ((Number) xRound.getValue()).intValue();
		int yround = ((Number) yRound.getValue()).intValue();
		int zround = ((Number) zRound.getValue()).intValue();
		int fround = ((Number) fRound.getValue()).intValue();
		double bRatio = ((Number) bufferRatio.getValue()).doubleValue();

		if (xround <= 0 || yround <= 0 || fround <= 0 || zround <= 0 || bRatio <= 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Minimum values must be greater than 0", 
					"Minimum values must be greater than 0", 
					JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		return true;
	}
	
	
	
	
	
}
