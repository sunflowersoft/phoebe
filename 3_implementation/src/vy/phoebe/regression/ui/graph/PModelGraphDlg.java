package vy.phoebe.regression.ui.graph;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.RModel;
import vy.phoebe.regression.ui.graph.PModelGraphConfigDlg.Config;
import vy.phoebe.util.Constants;
import vy.phoebe.util.DSUtil;
import vy.phoebe.util.MinMax;
import vy.phoebe.util.MiscUtil;


public class PModelGraphDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private PModelGraphDlg(Component comp, final PModelGraph graph) {
		super(MiscUtil.getFrameForComponent(comp), "Percentile model graph", false);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JTextArea info = new JTextArea();
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < graph.percent.length; i++) {
			String color = PModelGraph.COLOR_LIST[i % PModelGraph.COLOR_LIST.length].name;
			buffer.append( (int) (graph.percent[i] * 100) + " % is drawed as " + color + " line");
			buffer.append("\n");
		}
		buffer.append("SD is drawed as gray line \n");
		//
		info.setRows(4);
		info.setText(buffer.toString());
		info.setEditable(false);
		info.setCaretPosition(0);
		header.add(new JScrollPane(info), BorderLayout.CENTER);
		
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
		body.add(graph, BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		JButton print = new JButton("Print");
		print.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				try {
					Printable printable = new Printable() {
						
						@Override
						public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
								throws PrinterException {
							// TODO Auto-generated method stub
							if (pageIndex > 0)
								return NO_SUCH_PAGE;
							
							double x = pageFormat.getImageableX();
							double y = pageFormat.getImageableY();
							graphics.translate((int)x, (int)y);
							((PlotGraphExt) graph).paint(graphics, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
							
							return PAGE_EXISTS;
						}
					};
					
	  				PrinterJob pjob = PrinterJob.getPrinterJob();
	  				pjob.setPrintable(printable);
	  				
    				if (pjob.printDialog())
    					pjob.print();
				}
				catch (Throwable ex) {
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


	public static void showDlg(Component comp, Dataset dataset, boolean normal) {
		
		PModelGraphConfigDlg dlg = new PModelGraphConfigDlg(comp, dataset);
		
		dlg.setVisible(true);
		
		Config config = dlg.getConfig();
		if (config == null)
			return;
		
		PModelGraph graph = PModelGraph.create(
				dataset, 
				config.regressor, 
				config.response, 
				config.percent,
				normal);
		
		if (graph == null) {
			JOptionPane.showMessageDialog(
					comp, 
					"Graph invalid", 
					"Graph invalid", 
					JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		new PModelGraphDlg(comp, graph);
	}
	
	
	public static void showDlg(
			Component comp,
			Dataset dataset, 
			RModel rmodel) {
		
		if (dataset == null || rmodel == null || rmodel.getRegressorNames().size() != 1) {
			JOptionPane.showMessageDialog(
					comp, 
					"Invalid prameters", 
					"Invalid prameters", 
					JOptionPane.ERROR_MESSAGE);
			
			return;
		}

		PModelGraphConfigDlg2 dlg = new PModelGraphConfigDlg2(comp, rmodel);
		if (dataset != null) {
			
			MinMax x = dataset.getMeasureRange(rmodel.getRegressorNames().get(0));
			if (x != null)
				dlg.setXMinMax(x.min(), x.max());
		}
		
		dlg.setVisible(true);
		
		vy.phoebe.regression.ui.graph.PModelGraphConfigDlg2.Config config = dlg.getConfig();
		if (config == null)
			return;
		
		PModelGraph graph = PModelGraph.create(
				dataset, 
				rmodel, 
				config.getXRange(), 
				config.percent);
		
		if (graph == null) {
			JOptionPane.showMessageDialog(
					comp, 
					"Graph invalid", 
					"Graph invalid", 
					JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		new PModelGraphDlg(comp, graph);
	}
	
}




class PModelGraphConfigDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	Dataset dataset = null;
	
	
	JTextField txtPercent = null;
	JComboBox<String> cmbRegressor = null;
	JComboBox<String> cmbResponse = null;
	
	
	Config config = null;
	
	
	public PModelGraphConfigDlg(Component comp, Dataset dataset) {
		super(MiscUtil.getFrameForComponent(comp), "Percentile model graph configuration", true);
		this.dataset = dataset;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 200);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		left.add(new JLabel("Percentile: "));
		left.add(new JLabel("Regressor: "));
		left.add(new JLabel("Response: "));
		
		JPanel center = new JPanel(new GridLayout(0, 1));
		header.add(center, BorderLayout.CENTER);

		
		txtPercent = new JTextField("5, 10, 50, 90, 95");
		center.add(txtPercent);
		
		
		cmbRegressor = new JComboBox<String>(dataset.getMeasureNameVector());
		center.add(cmbRegressor);
		
		
		cmbResponse = new JComboBox<String>(dataset.getMeasureNameVector());
		center.add(cmbResponse);

		
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
					List<String> list = DSUtil.split(txtPercent.getText().trim(), ",", null);
					List<Double> percent = new ArrayList<Double>();
					
					for (String text : list) {
						percent.add(Double.parseDouble(text) / 100.0);
					}
					
					config = new Config(
							DSUtil.toArray(percent), cmbRegressor.getSelectedItem().toString(), cmbResponse.getSelectedItem().toString()
							);
					
					dispose();
				}
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
	
	
	private boolean validateValues() {
		String percentText = txtPercent.getText().trim();
		if (percentText.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"Percentile invalid", 
					"Percentile invalid", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		List<String> list = DSUtil.split(percentText, ",", null);
		if (list.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Percentile invalid", 
					"Percentile invalid", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		for (String text : list) {
			try {
				double percent = Double.parseDouble(text) / 100f;
				if (percent < 0 || percent > 1) {
					JOptionPane.showMessageDialog(
							this, 
							"Percentile invalid", 
							"Percentile invalid", 
							JOptionPane.ERROR_MESSAGE);
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(
						this, 
						"Percentile invalid", 
						"Percentile invalid", 
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		if (cmbRegressor.getSelectedItem() == null || cmbResponse.getSelectedItem() == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Regressor or response empty", 
					"Regressor or response empty", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		
		if ( cmbRegressor.getSelectedItem().equals(cmbResponse.getSelectedItem()) ) {
			JOptionPane.showMessageDialog(
					this, 
					"Invalid regressor or response", 
					"Invalid regressor or response", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		
		return true;
	}
	
	
	public static class Config {
		
		public double[] percent = null;
		public String regressor = null;
		public String response = null;
		
		public Config(
				double[] percent, String regressor, String response) {
			
			this.percent = percent;
			this.regressor = regressor;
			this.response = response;
			
		}
		
		
		
	}
	
	
	
}



class PModelGraphConfigDlg2 extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	JTextField txtPercent = null;

	JFormattedTextField xMin = null;
	JFormattedTextField xMax = null;
	JFormattedTextField xSteps = null;
	
	
	Config config = null;
	
	
	public PModelGraphConfigDlg2(Component comp, RModel model) {
		super(MiscUtil.getFrameForComponent(comp), "Regression model graph configuration", true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 200);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		txtPercent = new JTextField("5, 10, 50, 90, 95");
		header.add(txtPercent, BorderLayout.NORTH);
		
		JPanel rangePanel = new JPanel(new GridLayout(0, 3));
		header.add(rangePanel, BorderLayout.CENTER);
		
		JPanel xMinPanel = new JPanel();
		rangePanel.add(xMinPanel);
		xMinPanel.add(
				new JLabel(model == null ? "X min:" : model.getRegressorNames().get(0) + " min:"));
		xMin = new JFormattedTextField(new NumberFormatter());
		xMin.setValue(1.0);
		xMin.setColumns(4);
		xMinPanel.add(xMin);
		//
		JPanel xMaxPanel = new JPanel();
		rangePanel.add(xMaxPanel);
		xMaxPanel.add(
				new JLabel(model == null ? "X max:" : model.getRegressorNames().get(0) + " max:"));
		xMax = new JFormattedTextField(new NumberFormatter());
		xMax.setColumns(4);
		xMax.setValue(100.0);
		xMaxPanel.add(xMax);
		//
		JPanel xStepPanel = new JPanel();
		rangePanel.add(xStepPanel);
		xStepPanel.add(
				new JLabel(model == null ? "X steps:" : model.getRegressorNames().get(0) + " steps:"));
		xSteps = new JFormattedTextField(new NumberFormatter());
		xSteps.setColumns(4);
		xSteps.setValue(10.0);
		xStepPanel.add(xSteps);

	
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
					List<String> list = DSUtil.split(txtPercent.getText().trim(), ",", null);
					List<Double> percent = new ArrayList<Double>();
					
					for (String text : list) {
						percent.add(Double.parseDouble(text) / 100.0);
					}
					config = new Config(
							DSUtil.toArray(percent),
							((Number)xMin.getValue()).doubleValue(),
							((Number)xMax.getValue()).doubleValue(),
							((Number)xSteps.getValue()).intValue());
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
	
	
	public void setXMinMax(double xMin, double xMax) { 
		this.xMin.setValue(xMin);
		this.xMax.setValue(xMax);
	}

	public Config getConfig() {
		return config;
	}
	
	
	private boolean validateValues() {
		String percentText = txtPercent.getText().trim();
		if (percentText.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"Percentile invalid", 
					"Percentile invalid", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		List<String> list = DSUtil.split(percentText, ",", null);
		if (list.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Percentile invalid", 
					"Percentile invalid", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		for (String text : list) {
			try {
				double percent = Double.parseDouble(text) / 100f;
				if (percent < 0 || percent > 1) {
					JOptionPane.showMessageDialog(
							this, 
							"Percentile invalid", 
							"Percentile invalid", 
							JOptionPane.ERROR_MESSAGE);
				}
			}
			catch (Throwable e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(
						this, 
						"Percentile invalid", 
						"Percentile invalid", 
						JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}

		
		if (xMin.getValue() == null || xMax.getValue() == null || xSteps.getValue() == null) {
			
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
		
		if (xmin < 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Minimum values must be greater than or equal to 0", 
					"Minimum values must be greater than or equal to 0", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		if (xsteps < 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Step values must be greater than 0", 
					"Step values must be greater than 0", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		
		if (xmax < xmin) {
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
		
		public double[] percent = null;
		public double xMin = 0;
		public double xMax = 0;
		public int xSteps = 0;
		
		
		public Config(double[] percent, double xMin, double xMax, int xSteps) {
			
			this.percent = percent;
			this.xMin = xMin;
			this.xMax = xMax;
			this.xSteps = xSteps;
			
		}
		
		
		public double[] getXRange() {
			return MinMax.createRange(xMin, xMax, xSteps);
		}
		
		
	}
	
	
	
}
