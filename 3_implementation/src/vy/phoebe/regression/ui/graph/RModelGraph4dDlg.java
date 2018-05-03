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
import vy.phoebe.regression.ui.graph.RModelGraph4dConfigDlg.Config;
import vy.phoebe.util.Constants;
import vy.phoebe.util.MinMax;
import vy.phoebe.util.MiscUtil;



public class RModelGraph4dDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private RModelGraph4dDlg(Component comp, final RModelGraph4d graph) {
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
					graph.updateUI();
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
			double[] yDomain, 
			double[] zDomain,
			double[] fDomain) {
		
		RModelGraph4d graph = RModelGraph4d.create(f, xDomain, yDomain, zDomain, fDomain);
		if (graph == null) {
			JOptionPane.showMessageDialog(
					comp, 
					"Invalid regression model", 
					"Invalid regression model", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		
		new RModelGraph4dDlg(comp, graph);
	}
	
	
	public static void showDlg(
			Component comp, 
			RModel model,
			Dataset dataset) {
		
		if (model == null || model.getRegressorNames().size() != 3) {
			JOptionPane.showMessageDialog(
					comp, 
					"Invalid regression model", 
					"Invalid regression model", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		RModelGraph4dConfigDlg dlg = new RModelGraph4dConfigDlg(comp, model);
		if (dataset != null) {
			
			MinMax x = dataset.getMeasureRange(model.getRegressorNames().get(0));
			if (x != null)
				dlg.setXMinMax(x.min(), x.max());
			
			MinMax y = dataset.getMeasureRange(model.getRegressorNames().get(1));
			if (y != null)
				dlg.setYMinMax(y.min(), y.max());
			
			MinMax z = dataset.getMeasureRange(model.getRegressorNames().get(2));
			if (z != null)
				dlg.setZMinMax(z.min(), z.max());
			
			MinMax f = dataset.getMeasureRange(model.getResponse());
			if (f != null)
				dlg.setFMinMax(f.min(), f.max());
		}
		
		dlg.setVisible(true);
		
		Config config = dlg.getConfig();
		if (config == null)
			return;
		
		showDlg(comp, model, config.getXRange(), config.getYRange(), config.getZRange(), config.getFRange());
	}




}



class RModelGraph4dConfigDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	JFormattedTextField xMin = null;
	JFormattedTextField xMax = null;
	JFormattedTextField xSteps = null;
	
	
	JFormattedTextField yMin = null;
	JFormattedTextField yMax = null;
	JFormattedTextField ySteps = null;
	
	
	JFormattedTextField zMin = null;
	JFormattedTextField zMax = null;
	JFormattedTextField zSteps = null;
	
	JFormattedTextField fMin = null;
	JFormattedTextField fMax = null;
	JFormattedTextField fSteps = null;

	Config config = null;
	
	
	public RModelGraph4dConfigDlg(Component comp, RModel model) {
		super(MiscUtil.getFrameForComponent(comp), "Regressor range", true);
		
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

	
		JPanel yMinPanel = new JPanel();
		header.add(yMinPanel);
		yMinPanel.add(
				new JLabel(model == null ? "Y min:" : model.getRegressorNames().get(1) + " min:"));
		yMin = new JFormattedTextField(new NumberFormatter());
		yMin.setColumns(4);
		yMin.setValue(1.0);
		yMinPanel.add(yMin);
		//
		JPanel yMaxPanel = new JPanel();
		header.add(yMaxPanel);
		yMaxPanel.add(
				new JLabel(model == null ? "Y max:" : model.getRegressorNames().get(1) + " max:"));
		yMax = new JFormattedTextField(new NumberFormatter());
		yMax.setColumns(4);
		yMax.setValue(100.0);
		yMaxPanel.add(yMax);
		//
		JPanel yStepPanel = new JPanel();
		header.add(yStepPanel);
		yStepPanel.add(
				new JLabel(model == null ? "Y steps:" : model.getRegressorNames().get(1) + " steps:"));
		ySteps = new JFormattedTextField(new NumberFormatter());
		ySteps.setColumns(4);
		ySteps.setValue(10.0);
		yStepPanel.add(ySteps);

	
		JPanel zMinPanel = new JPanel();
		header.add(zMinPanel);
		zMinPanel.add(
				new JLabel(model == null ? "Z min:" : model.getRegressorNames().get(2) + " min:"));
		zMin = new JFormattedTextField(new NumberFormatter());
		zMin.setColumns(4);
		zMin.setValue(1.0);
		zMinPanel.add(zMin);
		//
		JPanel zMaxPanel = new JPanel();
		header.add(zMaxPanel);
		zMaxPanel.add(
				new JLabel(model == null ? "Z max:" : model.getRegressorNames().get(2) + " max:"));
		zMax = new JFormattedTextField(new NumberFormatter());
		zMax.setColumns(4);
		zMax.setValue(100.0);
		zMaxPanel.add(zMax);
		//
		JPanel zStepPanel = new JPanel();
		header.add(zStepPanel);
		zStepPanel.add(
				new JLabel(model == null ? "Z steps:" : model.getRegressorNames().get(2) + " steps:"));
		zSteps = new JFormattedTextField(new NumberFormatter());
		zSteps.setColumns(4);
		zSteps.setValue(10.0);
		zStepPanel.add(zSteps);

		
		JPanel fMinPanel = new JPanel();
		header.add(fMinPanel);
		fMinPanel.add(
				new JLabel(model == null ? "Model min:" : model.getResponse() + " min:"));
		fMin = new JFormattedTextField(new NumberFormatter());
		fMin.setColumns(4);
		fMin.setValue(1.0);
		fMinPanel.add(fMin);
		//
		JPanel fMaxPanel = new JPanel();
		header.add(fMaxPanel);
		fMaxPanel.add(
				new JLabel(model == null ? "Model max:" : model.getResponse() + " max:"));
		fMax = new JFormattedTextField(new NumberFormatter());
		fMax.setColumns(4);
		fMax.setValue(100.0);
		fMaxPanel.add(fMax);
		//
		JPanel fStepPanel = new JPanel();
		header.add(fStepPanel);
		fStepPanel.add(
				new JLabel(model == null ? "Model steps:" : model.getResponse() + " steps:"));
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
							((Number)yMin.getValue()).doubleValue(), ((Number)yMax.getValue()).doubleValue(), ((Number)ySteps.getValue()).intValue(),
							((Number)zMin.getValue()).doubleValue(), ((Number)zMax.getValue()).doubleValue(), ((Number)zSteps.getValue()).intValue(),
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
			double yMin, double yMax, int ySteps, 
			double zMin, double zMax, int zSteps, 
			double fMin, double fMax, int fSteps) { 
		
		this.xMin.setValue(xMin);
		this.xMax.setValue(xMax);
		this.xSteps.setValue(xSteps);
		
		this.yMin.setValue(yMin);
		this.yMax.setValue(yMax);
		this.ySteps.setValue(ySteps);
		
		this.zMin.setValue(xMin);
		this.zMax.setValue(xMax);
		this.zSteps.setValue(xSteps);
	}
	
	
	public void setXMinMax(double xMin, double xMax) { 
		this.xMin.setValue(xMin);
		this.xMax.setValue(xMax);
	}

	
	public void setYMinMax(double yMin, double yMax) { 
		this.yMin.setValue(yMin);
		this.yMax.setValue(yMax);
	}

	
	public void setZMinMax(double zMin, double zMax) { 
		this.zMin.setValue(zMin);
		this.zMax.setValue(zMax);
	}

	
	public void setFMinMax(double fMin, double fMax) { 
		this.fMin.setValue(fMin);
		this.fMax.setValue(fMax);
	}

	
	private boolean validateValues() {
		if (xMin.getValue() == null || xMax.getValue() == null || xSteps.getValue() == null ||
			yMin.getValue() == null || yMax.getValue() == null || ySteps.getValue() == null ||
			zMin.getValue() == null || zMax.getValue() == null || zSteps.getValue() == null ||
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
		
		double ymin = ((Number) yMin.getValue()).doubleValue();
		double ymax = ((Number) yMax.getValue()).doubleValue();
		double ysteps = ((Number) ySteps.getValue()).doubleValue();

		double zmin = ((Number) zMin.getValue()).doubleValue();
		double zmax = ((Number) zMax.getValue()).doubleValue();
		double zsteps = ((Number) zSteps.getValue()).doubleValue();

		double fmin = ((Number) fMin.getValue()).doubleValue();
		double fmax = ((Number) fMax.getValue()).doubleValue();
		double fsteps = ((Number) fSteps.getValue()).doubleValue();

		if (xmin < 0 || ymin < 0 || zmin < 0 || fmin < 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Minimum values must be greater than or equal to 0", 
					"Minimum values must be greater than or equal to 0", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if (xsteps < 0 || ysteps < 0 || zsteps < 0 || fsteps < 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Step values must be greater than 0", 
					"Step values must be greater than 0", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		
		if (xmax < xmin || ymax < ymin || zmax < zmin || fmax < fmin) {
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
		
		
		public double yMin = 0;
		public double yMax = 0;
		public int ySteps = 0;
		
		
		public double zMin = 0;
		public double zMax = 0;
		public int zSteps = 0;
		
		
		public double fMin = 0;
		public double fMax = 0;
		public int fSteps = 0;

		
		public Config(double xMin, double xMax, int xSteps, 
				double yMin, double yMax, int ySteps,
				double zMin, double zMax, int zSteps,
				double fMin, double fMax, int fSteps) {
			
			this.xMin = xMin;
			this.xMax = xMax;
			this.xSteps = xSteps;
			
			this.yMin = yMin;
			this.yMax = yMax;
			this.ySteps = ySteps;
			
			this.zMin = zMin;
			this.zMax = zMax;
			this.zSteps = zSteps;
			
			this.fMin = fMin;
			this.fMax = fMax;
			this.fSteps = fSteps;
		}
		
		
		public double[] getXRange() {
			return MinMax.createRange(xMin, xMax, xSteps);
		}
		
		
		public double[] getYRange() {
			return MinMax.createRange(yMin, yMax, ySteps);
		}
		
		
		public double[] getZRange() {
			return MinMax.createRange(zMin, zMax, zSteps);
		}
		
		
		public double[] getFRange() {
			return MinMax.createRange(fMin, fMax, fSteps);
		}

		
		
	}
	
	

}




