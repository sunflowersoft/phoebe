package vy.phoebe.regression.ui.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
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

import vy.phoebe.regression.RModel;
import vy.phoebe.util.DSUtil;
import vy.phoebe.util.FileUtil;
import vy.phoebe.util.MiscUtil;


public class RModelGraph2d extends JPanel implements Graph {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected RModel f = null;
	
	
	protected Drawer2d drawer = null;
	
	
	private RModelGraph2d(RModel f, Drawer2d drawer) {
		this.f = f;
		this.drawer = drawer;
	}
	
	
	public void setupViewOption() {
		RModelGraph2dViewOptionDlg dlg = new RModelGraph2dViewOptionDlg(this, drawer.viewOption);
		if (dlg.getViewOption() != null) {
			drawer.viewOption.copy(dlg.getViewOption());
			
			updateUI();
		}
	}
	
	
	public RModel getModel() {
		return f;
	}
	
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		setBackground(Color.WHITE);
		
		drawer.updateBox(getWidth(), getHeight());
		drawer.draw(g);
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
			drawer.updateBox((int)width, (int)height);
			drawer.draw(g);
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
		return drawer.getOuterBox();
	}


	@Override
	public GraphViewOption getViewOption() {
		// TODO Auto-generated method stub
		return drawer.getViewOption();
	}


	public static RModelGraph2d create(final RModel f, double[] xDomain, double[] fDomain) {
		
		if (f == null || f.getRegressorNames().size() != 1 || 
				xDomain == null || fDomain == null ||
				xDomain.length == 0 || fDomain.length == 0)
			
			return null;
		
		Drawer2d drawer = new Drawer2d(xDomain, fDomain) {

			@Override
			public RModel getModel() {
				// TODO Auto-generated method stub
				return f;
			}

			
			@Override
			public String getXLegend() {
				// TODO Auto-generated method stub
				
				if (viewOption.legends != null && viewOption.legends.length > 1)
					return viewOption.legends[0];
				
				ArrayList<String> regressors = f.getRegressorNames();
				return regressors.get(0);
			}

			
			@Override
			public String getFLegend() {
				// TODO Auto-generated method stub
				if (viewOption.legends != null && viewOption.legends.length > 1)
					return viewOption.legends[1];
				else
					return f.getResponse();
			}

			
			@Override
			public double eval(double xValue) {
				// TODO Auto-generated method stub
				HashMap<String, Double> params = new HashMap<String, Double>();
				params.put(f.getRegressorNames().get(0), xValue);
				
				return f.eval(params);
			}
			
		};
		
		return new RModelGraph2d(f, drawer);
			
	}

	
	
}



class RModelGraph2dViewOptionDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	JFormattedTextField xRound = null;
	
	
	JFormattedTextField fRound = null;

	
	JTextField legends = null;
	
	
	JFormattedTextField bufferRatio = null;

	
	GraphViewOption returnViewOption = null;
	
	
	public RModelGraph2dViewOptionDlg(Component comp, GraphViewOption option) {
		super (MiscUtil.getFrameForComponent(comp), "Graph 2D option", true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 200);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("X round: "));
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
		
		fRound = new JFormattedTextField(new NumberFormatter());
		if (option != null)
			fRound.setValue(option.fRound);
		else
			fRound.setValue(GraphViewOption.ROUND);
		center.add(fRound);
		
		legends = new JTextField();
		if (option != null && option.legends != null && option.legends.length > 1) {
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
					returnViewOption.fRound = ((Number) fRound.getValue()).intValue();
					
					String legendText = legends.getText().trim();
					List<String> list = DSUtil.split(legendText, ",", null);
					if (list.size() > 1)
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
			fRound.getValue() == null) {
			
			JOptionPane.showMessageDialog(
					this, 
					"Empty values", 
					"Empty values", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		int xround = ((Number) xRound.getValue()).intValue();
		int fround = ((Number) fRound.getValue()).intValue();
		double bRatio = ((Number) bufferRatio.getValue()).doubleValue();

		if (xround <= 0 || fround <= 0 || bRatio <= 0) {
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


