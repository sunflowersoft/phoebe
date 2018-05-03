package vy.phoebe.regression.ui.graph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterJob;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.text.NumberFormatter;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.RModel;
import vy.phoebe.regression.ui.RModelTextArea;
import vy.phoebe.regression.ui.graph.RModelGraph2dConfigDlg.Config;
import vy.phoebe.util.Constants;
import vy.phoebe.util.MinMax;
import vy.phoebe.util.MiscUtil;


public class RModelGraph2dDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private RModelGraph2dDlg(final Component comp, final RModelGraph2d graph) {
		super(MiscUtil.getFrameForComponent(comp), "Regression model graph", false);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		header.add(new JLabel("Graph for model: "), BorderLayout.WEST);
		
		RModelTextArea txtModel = new RModelTextArea(graph.getModel());
		txtModel.setLineWrap(false);
		header.add(txtModel, BorderLayout.CENTER);
		
		JPanel toolbar = new JPanel();
		header.add(toolbar, BorderLayout.EAST);
		//
		JButton btnRefresh = MiscUtil.makeIconButton(
			getClass().getResource(Constants.IMAGES_DIR + "refresh-16x16.png"), 
			"refresh", "Refresh", "Refresh", 
				
			new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					graph.repaint();
				}
		});
		btnRefresh.setMargin(new Insets(0, 0 , 0, 0));
		toolbar.add(btnRefresh);
		//
		JButton btnOption = MiscUtil.makeIconButton(
			getClass().getResource(Constants.IMAGES_DIR + "option-16x16.png"), 
			"view option", "view option", "View option", 
				
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						graph.setupViewOption();
					}
			});
		btnOption.setMargin(new Insets(0, 0, 0, 0));
		toolbar.add(btnOption);

		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		body.add(new JScrollPane(graph), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		JButton print = new JButton("Print");
		print.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
    			try {
       			
	       			//get a PrintJob
	  				PrinterJob pjob = PrinterJob.getPrinterJob();
	    				//set a HelloPrint as the target to print
	  				pjob.setPrintable(graph, pjob.defaultPage());
	  				//get the print dialog, continue if canel
	  				//is not clicked
	  				
    				if (pjob.printDialog()) {
    					//print the target (HelloPrint)
    					pjob.print();
    				}
    			} 
    			catch (Exception ex) {
	        		ex.printStackTrace();
    			}
    			
			}
		});
		footer.add(print);
		
		JButton export = new JButton("Export image");
		export.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				graph.exportImage();
			}
		});
		footer.add(export);

		setVisible(true);
	}
	
	
	public static void showDlg(
			Component comp, 
			RModel f, 
			double[] xDomain, 
			double[] fDomain) {
		
		RModelGraph2d graph = RModelGraph2d.create(f, xDomain, fDomain);
		
		if (graph == null) {
			JOptionPane.showMessageDialog(
					comp, 
					"Invalid regression model", 
					"Invalid regression model", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		
		new RModelGraph2dDlg(comp, graph);
	}

	
	public static void showDlg(
			Component comp, 
			RModel f,
			Dataset dataset) {
		
		if (f == null || f.getRegressorNames().size() != 1) {
			JOptionPane.showMessageDialog(
					comp, 
					"Invalid regression model", 
					"Invalid regression model", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		RModelGraph2dConfigDlg dlg = new RModelGraph2dConfigDlg(comp, f);
		if (dataset != null) {
			
			MinMax x = dataset.getMeasureRange(f.getRegressorNames().get(0));
			if (x != null)
				dlg.setXMinMax(x.min(), x.max());
			
			MinMax fRange = dataset.getMeasureRange(f.getResponse());
			if (fRange != null)
				dlg.setFMinMax(fRange.min(), fRange.max());
		}
		
		dlg.setVisible(true);
		
		Config config = dlg.getConfig();
		if (config == null)
			return;
		
		showDlg(comp, f, config.getXRange(), config.getFRange());
	}


	public static void create(
			Component comp, 
			RModel f) {
	
		showDlg(comp, f, null);
	}

	
	
}




class RModelGraph2dConfigDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	JFormattedTextField xMin = null;
	JFormattedTextField xMax = null;
	JFormattedTextField xSteps = null;
	
	
	JFormattedTextField fMin = null;
	JFormattedTextField fMax = null;
	JFormattedTextField fSteps = null;
	
	
	Config config = null;
	
	
	public RModelGraph2dConfigDlg(Component comp, RModel model) {
		super(MiscUtil.getFrameForComponent(comp), "Regression model graph configuration", true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 200);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new GridLayout(0, 3));
		add(header, BorderLayout.NORTH);
		
		JPanel xMinPanel = new JPanel();
		header.add(xMinPanel);
		xMinPanel.add(
				new JLabel(model == null ? "X min:" : model.getRegressorNames().get(0) + " min:"));
		xMin = new JFormattedTextField(new NumberFormatter());
		xMin.setValue(1.0);
		xMin.setColumns(4);
		xMinPanel.add(xMin);
		//
		JPanel xMaxPanel = new JPanel();
		header.add(xMaxPanel);
		xMaxPanel.add(
				new JLabel(model == null ? "X max:" : model.getRegressorNames().get(0) + " max:"));
		xMax = new JFormattedTextField(new NumberFormatter());
		xMax.setColumns(4);
		xMax.setValue(100.0);
		xMaxPanel.add(xMax);
		//
		JPanel xStepPanel = new JPanel();
		header.add(xStepPanel);
		xStepPanel.add(
				new JLabel(model == null ? "X steps:" : model.getRegressorNames().get(0) + " steps:"));
		xSteps = new JFormattedTextField(new NumberFormatter());
		xSteps.setColumns(4);
		xSteps.setValue(10.0);
		xStepPanel.add(xSteps);

	
		JPanel fMinPanel = new JPanel();
		header.add(fMinPanel);
		fMinPanel.add(
				new JLabel(model == null ? "Z min:" : model.getResponse() + " min:"));
		fMin = new JFormattedTextField(new NumberFormatter());
		fMin.setColumns(4);
		fMin.setValue(1.0);
		fMinPanel.add(fMin);
		//
		JPanel fMaxPanel = new JPanel();
		header.add(fMaxPanel);
		fMaxPanel.add(
				new JLabel(model == null ? "Z max:" : model.getResponse() + " max:"));
		fMax = new JFormattedTextField(new NumberFormatter());
		fMax.setColumns(4);
		fMax.setValue(100.0);
		fMaxPanel.add(fMax);
		//
		JPanel fStepPanel = new JPanel();
		header.add(fStepPanel);
		fStepPanel.add(
				new JLabel(model == null ? "Z steps:" : model.getResponse() + " steps:"));
		fSteps = new JFormattedTextField(new NumberFormatter());
		fSteps.setColumns(4);
		fSteps.setValue(10.0);
		fStepPanel.add(fSteps);

		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		footer.add(ok);
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				config = null;
				if (validateValues()) {
					config = new Config(
							((Number)xMin.getValue()).doubleValue(), ((Number)xMax.getValue()).doubleValue(), ((Number)xSteps.getValue()).intValue(), 
							((Number)fMin.getValue()).doubleValue(), ((Number)fMax.getValue()).doubleValue(), ((Number)fSteps.getValue()).intValue());
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
				config = null;
				dispose();
			}
		});
		
	}
	
	
	public Config getConfig() {
		return config;
	}
	
	
	public void setDefaultValues(
			double xMin, double xMax, int xSteps, 
			double fMin, double fMax, int fSteps) { 
		
		this.xMin.setValue(xMin);
		this.xMax.setValue(xMax);
		this.xSteps.setValue(xSteps);
		
		this.fMin.setValue(fMin);
		this.fMax.setValue(fMax);
		this.fSteps.setValue(fSteps);
	}
	
	
	public void setXMinMax(double xMin, double xMax) { 
		this.xMin.setValue(xMin);
		this.xMax.setValue(xMax);
	}

	
	public void setFMinMax(double fMin, double fMax) { 
		this.fMin.setValue(fMin);
		this.fMax.setValue(fMax);
	}

	
	private boolean validateValues() {
		if (xMin.getValue() == null || xMax.getValue() == null || xSteps.getValue() == null ||
			fMin.getValue() == null || fMax.getValue() == null || fSteps.getValue() == null) {
			
			JOptionPane.showMessageDialog(
					this, 
					"Empty values", 
					"Empty values", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		double xmin = ((Number) xMin.getValue()).doubleValue();
		double xmax = ((Number) xMax.getValue()).doubleValue();
		double xsteps = ((Number) xSteps.getValue()).doubleValue();
		
		double fmin = ((Number) fMin.getValue()).doubleValue();
		double fmax = ((Number) fMax.getValue()).doubleValue();
		double fsteps = ((Number) fSteps.getValue()).doubleValue();

		if (xmin < 0 || fmin < 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Minimum values must be greater than or equal to 0", 
					"Minimum values must be greater than or equal to 0", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if (xsteps < 0 || fsteps < 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Step values must be greater than 0", 
					"Step values must be greater than 0", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		
		if (xmax < xmin || fmax < fmin) {
			JOptionPane.showMessageDialog(
					this, 
					"Maximum values less than minimum values", 
					"Maximum values less than minimum values", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		
		return true;
	}
	
	
	public static class Config {
		
		public double xMin = 0;
		public double xMax = 0;
		public int xSteps = 0;
		
		
		public double fMin = 0;
		public double fMax = 0;
		public int fSteps = 0;
		
		
		public Config(double xMin, double xMax, int xSteps, 
				double fMin, double fMax, int fSteps) {
			
			this.xMin = xMin;
			this.xMax = xMax;
			this.xSteps = xSteps;
			
			this.fMin = fMin;
			this.fMax = fMax;
			this.fSteps = fSteps;
		}
		
		
		public double[] getXRange() {
			return MinMax.createRange(xMin, xMax, xSteps);
		}
		
		
		public double[] getFRange() {
			return MinMax.createRange(fMin, fMax, fSteps);
		}
		
		
		
		
		
	}
	
	
	
	
	
	

}
