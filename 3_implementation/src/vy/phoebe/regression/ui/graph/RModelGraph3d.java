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
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
import vy.phoebe.util.Constants;
import vy.phoebe.util.DSUtil;
import vy.phoebe.util.FileUtil;
import vy.phoebe.util.MinMax;
import vy.phoebe.util.MiscUtil;
import vy.phoebe.util.FileUtil.ChosenFileResult;


public class RModelGraph3d extends JPanel implements Graph, Pageable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected RModel f = null;
	
	
	protected double[] xDomain = null;
	
	
	protected double[] yDomain = null;
	
	
	protected double[] fDomain = null;

	
	protected Rectangle outerBox = new Rectangle();
	
	
	protected Rectangle innerBox = new Rectangle();

	
	protected GraphViewOption viewOption = new GraphViewOption();
	
	
	protected List<SubGraph3d> subGraphList = new ArrayList<SubGraph3d>();
	
	
	private RModelGraph3d(RModel f, double[] xDomain, double[] yDomain, double[] fDomain) {
		this.f = f;
		this.xDomain = xDomain;
		this.yDomain = yDomain;
		this.fDomain = fDomain;
		this.subGraphList = createSubGraphList();
	}
	
	
	public RModel getModel() {
		return f;
	}
	
	
	public void setupViewOption() {
		RModelGraph3dViewOptionDlg dlg = new RModelGraph3dViewOptionDlg(this, this.viewOption);
		if (dlg.getViewOption() != null) {
			this.viewOption = dlg.getViewOption();
			
			for (SubGraph3d subGraph : subGraphList) {
				subGraph.viewOption = this.viewOption;
			}
			
			updateUI();
		}
	}
	
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		updateBox();
		
		setBackground(Color.WHITE);
		
		Graphics2D g2d = (Graphics2D)g;
		drawFrame(g2d);
		drawCurve(g2d);
	}
	
	
	private void drawCurve(Graphics2D g) {
		MinMax xRange = MinMax.create(xDomain);
		MinMax yRange = MinMax.create(yDomain);
		MinMax fRange = MinMax.create(fDomain);
		
		for (int j = 0; j < yDomain.length; j++) {
			double yValue = yDomain[j];
			Rectangle yEachBox = getYEachBox(yValue);
			
			Point pre = null;
			for (int i = 0; i < xDomain.length; i++) {
				double xValue = xDomain[i];
				int x = MathUtil.translate(innerBox, xValue, yDomain[0], xRange, yRange).x;
				
				HashMap<String, Double> params = new HashMap<String, Double>();
				params.put(f.getRegressorNames().get(0), xValue);
				params.put(f.getRegressorNames().get(1), yValue);
				double fValue = f.eval(params);
				if (!DSUtil.isUsed(fValue) || !fRange.contains(fValue)) {
					pre = null;
					continue;
				}
				
				int y = MathUtil.translate(yEachBox, xDomain[0], fValue, xRange, fRange).y;
				
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
			
			
		} // j
			
	}
	
	
	private void drawFrame(Graphics2D g) {

		FontMetrics fontMetrics = g.getFontMetrics();
		int textHeight = fontMetrics.getHeight();
		
		MinMax xRange = MinMax.create(xDomain);
		MinMax yRange = MinMax.create(yDomain);
		MinMax fRange = MinMax.create(fDomain);
		
		// Drawing f frame
		GraphLegendExp fExp = GraphLegendExp.create(fRange.max(), viewOption.fRound);
		// For each y box
		for (int i = 0; i < yDomain.length; i++) {
			double yValue = yDomain[i];
			Rectangle yEachBox = getYEachBox(yValue);
			
			// Draw horizontal dash f line in each y box
			for (int j = 0; j < fDomain.length; j++) {
				double fValue = fDomain[j];
				
				Point point = MathUtil.translate(yEachBox, xDomain[0], fValue, xRange, fRange);
				
				// Drawing f value
				if ( (i == 0 && j == 0) || ( i == yDomain.length - 1 && j == fDomain.length - 1) ) {
					String text = MathUtil.format(fValue * fExp.v, GraphViewOption.DECIMAL);
					
					g.drawString(
							text, 
							point.x - fontMetrics.stringWidth(text) - 4, 
							point.y);
				}
				else if (j == 0) {
					String text = MathUtil.format(fDomain[fDomain.length - 1] * fExp.v, GraphViewOption.DECIMAL) + "/" +
							MathUtil.format(fDomain[0] * fExp.v, GraphViewOption.DECIMAL);
					
					g.drawString(
							text, 
							point.x - fontMetrics.stringWidth(text) - 4, 
							point.y);
				}
				
				Stroke s = g.getStroke();
				Color c = g.getColor();
				BasicStroke stroke = new BasicStroke(
						0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 2 }, 0.0f);
				g.setStroke(stroke);
				g.setColor(new Color(0.5f, 0.5f, 0.5f));
				// Drawing horizontal dash f line
				g.drawLine(point.x - 2, point.y, point.x + yEachBox.width, point.y);
				//
				g.setColor(c);
				g.setStroke(s);
				
				
			} // end j
			
			
		} // end i
		
		String fName = (viewOption.legends.length > 2 ? viewOption.legends[2] : f.getResponse()) + fExp.text;
		Drawer2d.drawText(g, fName, 
				outerBox.x + textHeight, 
				outerBox.y + outerBox.height / 2 - fontMetrics.stringWidth(fName) / 2,
				Math.PI/2);

		// Drawing inner box
		g.drawRect(innerBox.x, innerBox.y, innerBox.width, innerBox.height);

		// Drawing x frame
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
			
			if (point.x > innerBox.x && point.x < innerBox.x + innerBox.width) {
				Stroke s = g.getStroke();
				Color c = g.getColor();
				BasicStroke stroke = new BasicStroke(
						0.1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 10 }, 0.0f);
				g.setStroke(stroke);
				g.setColor(new Color(0.5f, 0.5f, 0.5f));
				// vertical x line
				g.drawLine(point.x, point.y - innerBox.height, point.x, point.y);
				//
				g.setColor(c);
				g.setStroke(s);
			}
		}
		//
		String xName = (viewOption.legends.length > 2 ? viewOption.legends[0] : f.getRegressorNames().get(0)) + xExp.text; 
		g.drawString(xName, innerBox.x + innerBox.width / 2 - fontMetrics.stringWidth(xName) / 2, 
				innerBox.y + innerBox.height + 2 * textHeight);
		
		// Draw y frame
		GraphLegendExp yExp = GraphLegendExp.create(yRange.max(), viewOption.yRound);
		for (int i = 0; i < yDomain.length; i++) {
			double yValue = yDomain[i];
			Rectangle yEachBox = getYEachBox(yValue);
			
			// Draw right text
			String text = MathUtil.format(yValue * yExp.v, GraphViewOption.DECIMAL);
			g.drawString(text, 
					yEachBox.x + yEachBox.width + 4, 
					yEachBox.y + yEachBox.height / 2 + fontMetrics.getDescent());
			
			// Draw each y box
			g.drawRect(yEachBox.x, yEachBox.y, yEachBox.width, yEachBox.height);
		}
		//
		
		String yName = (viewOption.legends.length > 2 ? viewOption.legends[1] : f.getRegressorNames().get(1)) + yExp.text;
		Drawer2d.drawText(g, yName, 
				outerBox.x + outerBox.width - textHeight, 
				outerBox.y + outerBox.height / 2 - fontMetrics.stringWidth(yName) / 2,
				Math.PI/2);		
	}
	
	
	private Rectangle getYEachBox(double yValue) {
		MinMax xRange = MinMax.create(xDomain);
		MinMax yRange = MinMax.create(yDomain);

		Rectangle yInnerBox = innerBox;
		int yEachHeight = getYEachHeight();
		if (yDomain.length > 1) {
			// Note important
			yInnerBox = new Rectangle(innerBox.x, innerBox.y + yEachHeight, innerBox.width, innerBox.height - yEachHeight);
		}
		// Note important
		Point point = MathUtil.translate(yInnerBox, xDomain[0], yValue, xRange, yRange);
		return new Rectangle(point.x, point.y - yEachHeight, innerBox.width, yEachHeight);
	}
	
	
	private int getYEachHeight() {
		// Note 0.9
		return (int) ((double) innerBox.height / (yDomain.length + 1));
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
	public int getNumberOfPages() {
		// TODO Auto-generated method stub
		return subGraphList.size();
	}


	@Override
	public PageFormat getPageFormat(int pageIndex)
			throws IndexOutOfBoundsException {
		// TODO Auto-generated method stub
		
		return new PageFormat();
	}


	@Override
	public Printable getPrintable(int pageIndex)
			throws IndexOutOfBoundsException {
		// TODO Auto-generated method stub
		
		
		return new Printable() {
			
			@Override
			public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
					throws PrinterException {
				// TODO Auto-generated method stub
				
				if (pageIndex >= subGraphList.size())
					return NO_SUCH_PAGE;
				
				SubGraph3d subGraph = subGraphList.get(subGraphList.size() - 1 - pageIndex);
				double x = pageFormat.getImageableX();
				double y = pageFormat.getImageableY();
				double width = pageFormat.getImageableWidth();
				double height = pageFormat.getImageableHeight(); 
				subGraph.updateBox( new Rectangle(0, 0, (int)width, (int)height));
				
				graphics.translate((int)x, (int)y);
				subGraph.draw(graphics);
				
				return PAGE_EXISTS;
			}
		};
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
		} 
		else {
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


	private List<SubGraph3d> createSubGraphList() {
		List<SubGraph3d> subGraphList = new ArrayList<SubGraph3d>();
		for (double yValue : yDomain) {
			SubGraph3d subGraph = SubGraph3d.create(f, xDomain, yValue, fDomain);
			if (subGraph != null) {
				subGraph.viewOption = this.viewOption;
				subGraphList.add(subGraph);
			}
		}
		return subGraphList;
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
	
	
	public void exportAllImages() {
		ChosenFileResult chooseFile = FileUtil.chooseFile(
				this, false, new String[] { "png" }, new String[] { "PNG file (*.png)"});
		if (chooseFile == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Image not exported", 
					"Image not exported", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		File file = chooseFile.getChosenFile();
		String ext = chooseFile.getChosenExt();
		String prefix = file.getPath();
		if (ext != null) {
			int index = prefix.lastIndexOf(ext);
			if (index != -1)
				prefix = prefix.substring(0, index);
		}
		

		for (int i = 0; i < subGraphList.size(); i++) {
			SubGraph3d subGraph =  subGraphList.get(i);
			BufferedImage image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g = image.createGraphics();
			g.setColor(new Color(0, 0, 0));
			
			subGraph.updateBox(getWidth(), getHeight());
			subGraph.draw(g);
			try {
				ImageIO.write(image, "png", new File(prefix + "_" + (i + 1) + ".png"));
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		
		JOptionPane.showMessageDialog(
				this, 
				"Image exported successfully", 
				"Image exported successfully", 
				JOptionPane.INFORMATION_MESSAGE);
		
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


	public static RModelGraph3d create(RModel f, double[] xDomain, double[] yDomain, double[] fDomain) {
		if (f == null || f.getRegressorNames().size() != 2 || 
				xDomain == null || yDomain == null || fDomain == null ||
				xDomain.length == 0 || yDomain.length == 0 || fDomain.length == 0)
			return null;
		
		return new RModelGraph3d(f, xDomain, yDomain, fDomain);
	}
	
	
	
}



class SubGraph3d extends Drawer2d {

	
	/**
	 * 
	 */
	protected RModel f = null;
	
	
	/**
	 * 
	 */
	protected double yValue = Constants.UNUSED;
	
	
	/**
	 * 
	 * @param f
	 * @param xDomain
	 * @param yValue
	 * @param fDomain
	 */
	private SubGraph3d(RModel f, double[] xDomain, double yValue, double[] fDomain) {
		super(xDomain, fDomain);
		this.f = f;
		this.yValue = yValue;
	}


	@Override
	protected void drawFrame(Graphics2D g) {
		super.drawFrame(g);
		
		FontMetrics fontMetrics = g.getFontMetrics();
		int textHeight = fontMetrics.getHeight();

		// Draw right text
//		GraphLegendExp yExp = GraphLegendExp.create(yValue, viewOption.yRound);
//		String yName = (viewOption.legends.length > 2 ? viewOption.legends[1] : f.getRegressorNames().get(1)) + yExp.text; 
//		String text = yName + " = " + MathUtil.format(yValue * yExp.v, GraphViewOption.DECIMAL);
		String yName = viewOption.legends.length > 2 ? viewOption.legends[1] : f.getRegressorNames().get(1); 
		String text = yName + " = " + MathUtil.format(yValue, GraphViewOption.DECIMAL);
		Drawer2d.drawText(g, text, 
				outerBox.x + outerBox.width - textHeight, 
				outerBox.y + outerBox.height / 2 - fontMetrics.stringWidth(text) / 2, 
				Math.PI / 2);		
		
	}

	
	@Override
	public RModel getModel() {
		// TODO Auto-generated method stub
		return f;
	}


	@Override
	public String getXLegend() {
		// TODO Auto-generated method stub
		if (viewOption.legends.length > 2)
			return viewOption.legends[0];
		else
			return f.getRegressorNames().get(0);
	}


	@Override
	public String getFLegend() {
		// TODO Auto-generated method stub
		if (viewOption.legends.length > 2)
			return viewOption.legends[2];
		else
			return f.getResponse();
	}


	@Override
	public double eval(double xValue) {
		// TODO Auto-generated method stub
		HashMap<String, Double> params = new HashMap<String, Double>();
		params.put(f.getRegressorNames().get(0), xValue);
		params.put(f.getRegressorNames().get(1), yValue);
		
		return f.eval(params);
	}
	
	
	public static SubGraph3d create(RModel f, double[] xDomain, double yValue, double[] fDomain) {
		
		if (f == null || f.getRegressorNames().size() != 2 || 
				xDomain == null || fDomain == null ||
				xDomain.length == 0 || fDomain.length == 0 || !DSUtil.isUsed(yValue))
			
			return null;
		
		return new SubGraph3d(f, xDomain, yValue, fDomain);
	}
	
	
}


class RModelGraph3dViewOptionDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	JFormattedTextField xRound = null;
	
	
	JFormattedTextField yRound = null;

	
	JFormattedTextField fRound = null;

	
	JTextField legends = null;

	
	JFormattedTextField bufferRatio = null;

	
	GraphViewOption returnViewOption = null;
	
	
	public RModelGraph3dViewOptionDlg(Component comp, GraphViewOption option) {
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
		
		fRound = new JFormattedTextField(new NumberFormatter());
		if (option != null)
			fRound.setValue(option.fRound);
		else
			fRound.setValue(GraphViewOption.ROUND);
		center.add(fRound);
		
		legends = new JTextField();
		if (option != null && option.legends != null && option.legends.length > 2) {
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
					returnViewOption.fRound = ((Number) fRound.getValue()).intValue();
					
					String legendText = legends.getText().trim();
					List<String> list = DSUtil.split(legendText, ",", null);
					if (list.size() > 2)
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
		int fround = ((Number) fRound.getValue()).intValue();
		double bRatio = ((Number) bufferRatio.getValue()).doubleValue();

		if (xround <= 0 || yround <= 0 || fround <= 0 || bRatio <= 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Values must be greater than 0", 
					"Values must be greater than 0", 
					JOptionPane.ERROR_MESSAGE);
			
			return false;
		}
		
		return true;
	}
	
	
	
	
}

