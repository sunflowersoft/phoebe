package vy.phoebe.regression.ui.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.text.NumberFormatter;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.RModel;
import vy.phoebe.regression.RModelAssoc;
import vy.phoebe.util.MinMax;
import vy.phoebe.util.MiscUtil;
import flanagan.plot.PlotGraph;

public class RModelMultiVarGraph extends PlotGraphExt {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private RModelMultiVarGraph(double[][] data) {
		super(data);
	}

	
	@Override
	public int print(Graphics g, PageFormat pf, int pageIndex)
			throws PrinterException {
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
			paint(g);
		}
	    return retval;
	}


	@Override
	public String getGraphFeature() {
		// TODO Auto-generated method stub
		return "";
	}


	public static RModelMultiVarGraph create(RModel model, Dataset dataset) {
		ArrayList<String> regressors = model.getRegressorNames();
		if (regressors.size() == 0)
			return null;
		
		MinMax[] rangeList = new MinMax[regressors.size()];
		for (int i = 0; i < regressors.size(); i++) {
			double[] x = dataset.getMeasureArrayByName(regressors.get(i));
			rangeList[i] = MinMax.create(x);
		}
		
		return create(model, rangeList, dataset);
	}

	
	public static RModelMultiVarGraph create(RModel model, MinMax[] regressorRanges, Dataset dataset) {
		ArrayList<String> regressors = model.getRegressorNames();
		if (regressors.size() != regressorRanges.length)
			return null;
		
    	int curves = regressors.size();
    	int points = dataset.getRows();
    	RModelAssoc assoc = new RModelAssoc(model);
    	double[][] data = PlotGraph.data(curves, points);
    	double[] ycalc = assoc.getYcalc(dataset);
    	int[] pointOption = new int[curves];
		for (int i = 0; i < curves; i ++) {
			double[] x = dataset.getMeasureArrayByName(regressors.get(i));
			int curveIdx = 2*i;
			data[curveIdx] = regressorRanges[i].normalize(x);
			data[curveIdx + 1] = Arrays.copyOf(ycalc, ycalc.length);
			pointOption[i] = (i % 8) + 1;
		}
		
		RModelMultiVarGraph graph = new RModelMultiVarGraph(data);
		
		graph.setGraphTitle("Multivariate plot");
		graph.setXaxisLegend("Normalized measures");
		graph.setYaxisLegend("Estimated " + model.getResponse());
		graph.setPoint(pointOption);
		graph.setLine(0);

		graph.setBackground(Color.WHITE);

    	return graph;
	}

	
	public static void showDlg(Component comp, RModel model, Dataset dataset, GraphViewOption viewOption) {
		RModelMultiVarConfigDlg config = new RModelMultiVarConfigDlg(comp, model, dataset);
		MinMax[] result = config.getResult();
		
		if (result == null || result.length == 0) {
			JOptionPane.showMessageDialog(
					comp, 
					"Graph invalid", 
					"Graph invalid", 
					JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		final RModelMultiVarGraph graph = create(model, result, dataset);
		if (viewOption != null)
			graph.setViewOption(viewOption);
		
		JDialog dlg = new JDialog(MiscUtil.getFrameForComponent(comp), "Multivariate plot", false);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setSize(600, 400);
		dlg.setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		
		dlg.setLayout(new BorderLayout());
		dlg.add(graph, BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		dlg.add(footer, BorderLayout.SOUTH);
		JButton btnExport = new JButton("Export image");
		btnExport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				graph.exportImage();
			}
		});
		footer.add(btnExport);
		
		dlg.setVisible(true);
	}

	
	
}



class RModelMultiVarConfigDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	ArrayList<JFormattedTextField> minList = new ArrayList<JFormattedTextField>();

	
	ArrayList<JFormattedTextField> maxList = new ArrayList<JFormattedTextField>();
	
	
	MinMax[] result = new MinMax[0];
	
	
	public RModelMultiVarConfigDlg(final Component comp, RModel model, Dataset dataset) {
		super(MiscUtil.getFrameForComponent(comp), "Multivariate plot", true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 300);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		setLayout(new BorderLayout());

		JPanel header = new JPanel(new GridLayout(0, 3));
		add(header, BorderLayout.NORTH);
		
		ArrayList<String> regressors = model.getRegressorNames();
		MinMax[] rangeList = new MinMax[regressors.size()];
		for (int i = 0; i < regressors.size(); i++) {
			double[] x = dataset.getMeasureArrayByName(regressors.get(i));
			rangeList[i] = MinMax.create(x);
		}
		
		for (int i = 0; i < regressors.size(); i++) {
			String regressor = regressors.get(i);
			MinMax range = rangeList[i];
			JLabel lblRegressor = new JLabel(regressor);
			header.add(lblRegressor);
			
			JPanel minPanel = new JPanel();
			header.add(minPanel);
			minPanel.add(new JLabel("Min:"));
			JFormattedTextField min = new JFormattedTextField(new NumberFormatter());
			min.setValue(range.min());
			min.setColumns(4);
			minPanel.add(min);
			minList.add(min);
			
			JPanel maxPanel = new JPanel();
			header.add(maxPanel);
			maxPanel.add(new JLabel("Max:"));
			JFormattedTextField max = new JFormattedTextField(new NumberFormatter());
			max.setValue(range.max());
			max.setColumns(4);
			maxPanel.add(max);
			maxList.add(max);
		}
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		footer.add(ok);
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (!validateValues()) {
					result = new MinMax[0];
					
					JOptionPane.showMessageDialog(
							MiscUtil.getFrameForComponent(comp), 
							"Invalid paramter", 
							"Invalid paramter", 
							JOptionPane.ERROR_MESSAGE);
				}
				else {
					int n = minList.size();
					result = new MinMax[n];
					for (int i = 0; i < n; i++) {
						JFormattedTextField min = minList.get(i);
						JFormattedTextField max = maxList.get(i);
						
						double minValue = ((Number) min.getValue()).doubleValue();
						double maxValue = ((Number) max.getValue()).doubleValue();
						result[i] = new MinMax(minValue, maxValue);
					}
					
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
				result = new MinMax[0];
				dispose();
			}
		});
		
		setVisible(true);
	}
	
	
	private boolean validateValues() {
		if (minList.size() == 0 || maxList.size() == 0 || 
				minList.size() != maxList.size())
			return false;
		
		int n = minList.size();
		for (int i = 0; i < n; i++) {
			JFormattedTextField min = minList.get(i);
			JFormattedTextField max = maxList.get(i);
			
			if (min.getValue() == null || max.getValue() == null)
				return false;
			
			double minValue = ((Number) min.getValue()).doubleValue();
			double maxValue = ((Number) max.getValue()).doubleValue();
			if (maxValue < minValue)
				return false;
		}
		
		
		return true;
	}
	
	
	
	public MinMax[] getResult() {
		return result;
	}
	
	
}
