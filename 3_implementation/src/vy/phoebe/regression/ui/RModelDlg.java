package vy.phoebe.regression.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import vy.phoebe.Config;
import vy.phoebe.dataset.Dataset;
import vy.phoebe.dataset.ui.StatTable;
import vy.phoebe.math.FlanaganStat;
import vy.phoebe.math.MathUtil;
import vy.phoebe.regression.RModel;
import vy.phoebe.regression.RModelAssoc;
import vy.phoebe.regression.ui.graph.Graph;
import vy.phoebe.regression.ui.graph.PlotGraphExt;
import vy.phoebe.regression.ui.graph.RModelMultiVarGraph;
import vy.phoebe.util.Constants;
import vy.phoebe.util.DSUtil;
import vy.phoebe.util.FileUtil;
import vy.phoebe.util.MiscUtil;
import flanagan.analysis.Stat;
import flanagan.math.Fmath;
import flanagan.plot.PlotGraph;

public class RModelDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private RModel model = null;
	
	
	private Dataset dataset = null;


	private JTable tblRegression = null;
	
	
	private JTextField txtCalc = null; 
	
	
	ArrayList<Graph> graphList = new ArrayList<Graph>();
	
	ArrayList<Graph> graphList2 = new ArrayList<Graph>();
	
	
	public RModelDlg(Component comp, RModel model) {
		this(comp, model, null);
	}


	public RModelDlg(final Component comp, final RModel model, final Dataset dataset) {
		super(MiscUtil.getFrameForComponent(comp), "Regression Information", false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(800, 650);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		
		this.model = model;
		this.dataset = dataset;
		
		setLayout(new BorderLayout());
		
		JPanel top = new JPanel(new BorderLayout());
		this.add(top, BorderLayout.NORTH);
		
		RModelTextArea txtModel = new RModelTextArea(model, dataset);
		top.add(txtModel, BorderLayout.CENTER);
		
		JPanel main = new JPanel(new BorderLayout());
		this.add(main, BorderLayout.CENTER);
		
		JPanel header = new JPanel(new GridLayout(1, 0));
		main.add(header, BorderLayout.NORTH);

		
		JPanel col = null;
		JPanel left = null;
		JPanel right = null; 
		JPanel pane = null;
		
		col = new JPanel(new BorderLayout());
		header.add(col);
		//
		left = new JPanel(new GridLayout(0, 1));
		col.add(left, BorderLayout.WEST);
		//
		left.add(new JLabel("Type: "));
		left.add(new JLabel("Fitness: "));
		left.add(new JLabel("R: "));
		left.add(new JLabel("SS: "));
		//
		right = new JPanel(new GridLayout(0, 1));
		col.add(right, BorderLayout.CENTER);
		//
		JTextField txtType = new JTextField(model.getType());
		txtType.setCaretPosition(0);
		txtType.setEditable(false);
		pane = new JPanel(new BorderLayout());
		pane.add(txtType, BorderLayout.WEST);
		right.add(pane);
		//
		JTextField txtFitness = new JTextField(
				MathUtil.format(model.getFitness()));
		txtFitness.setCaretPosition(0);
		txtFitness.setEditable(false);
		pane = new JPanel(new BorderLayout());
		pane.add(txtFitness, BorderLayout.WEST);
		right.add(pane);
		//
		JTextField txtR = new JTextField(
				MathUtil.format(model.getR()));
		txtR.setCaretPosition(0);
		txtR.setEditable(false);
		pane = new JPanel(new BorderLayout());
		pane.add(txtR, BorderLayout.WEST);
		right.add(pane);
		//
		JTextField txtSS = new JTextField(
				MathUtil.format(model.getSumOfSquares()));
		txtSS.setCaretPosition(0);
		txtSS.setEditable(false);
		pane = new JPanel(new BorderLayout());
		pane.add(txtSS, BorderLayout.WEST);
		right.add(pane);
		
		
		col = new JPanel(new BorderLayout());
		header.add(col);
		//
		left = new JPanel(new GridLayout(0, 1));
		col.add(left, BorderLayout.WEST);
		//
		left.add(new JLabel("Mean: "));
		left.add(new JLabel("Sd: "));
		left.add(new JLabel(""));
		left.add(new JLabel(""));
		//
		right = new JPanel(new GridLayout(0, 1));
		col.add(right, BorderLayout.CENTER);
		//
		JTextField txtMean = new JTextField(
				MathUtil.format(model.getMean()));
		txtMean.setCaretPosition(0);
		txtMean.setEditable(false);
		pane = new JPanel(new BorderLayout());
		pane.add(txtMean, BorderLayout.WEST);
		right.add(pane);
		//
		JTextField txtSd = new JTextField(
				MathUtil.format(model.getSd()));
		txtSd.setCaretPosition(0);
		txtSd.setEditable(false);
		pane = new JPanel(new BorderLayout());
		pane.add(txtSd, BorderLayout.WEST);
		right.add(pane);
		//
		pane = new JPanel(new BorderLayout());
		pane.add(new JLabel(""), BorderLayout.WEST);
		right.add(pane);
		//
		pane = new JPanel(new BorderLayout());
		pane.add(new JLabel(""), BorderLayout.WEST);
		right.add(pane);


		col = new JPanel(new BorderLayout());
		header.add(col);
		//
		left = new JPanel(new GridLayout(0, 1));
		col.add(left, BorderLayout.WEST);
		//
		left.add(new JLabel("Error mean: "));
		left.add(new JLabel("Error sd: "));
		left.add(new JLabel("Error mean (%): "));
		left.add(new JLabel("Error sd (%): "));
		//
		right = new JPanel(new GridLayout(0, 1));
		col.add(right, BorderLayout.CENTER);
		//
		JTextField txtErrMean = new JTextField(
				MathUtil.format(model.getErrMean()) );
		txtErrMean.setCaretPosition(0);
		txtErrMean.setEditable(false);
		pane = new JPanel(new BorderLayout());
		pane.add(txtErrMean, BorderLayout.WEST);
		right.add(pane);
		//
		JTextField txtErrSd = new JTextField(
				MathUtil.format(model.getErrSd()));
		txtErrSd.setCaretPosition(0);
		txtErrSd.setEditable(false);
		pane = new JPanel(new BorderLayout());
		pane.add(txtErrSd, BorderLayout.WEST);
		right.add(pane);
		//
		JTextField txtRatioErrMean = new JTextField(
				MathUtil.format(model.getRatioErrMean() * 100.0));
		txtRatioErrMean.setCaretPosition(0);
		txtRatioErrMean.setEditable(false);
		pane = new JPanel(new BorderLayout());
		pane.add(txtRatioErrMean, BorderLayout.WEST);
		right.add(pane);
		//
		JTextField txtRatioErrSd = new JTextField(
				MathUtil.format(model.getRatioErrSd() * 100.0));
		txtRatioErrSd.setCaretPosition(0);
		txtRatioErrSd.setEditable(false);
		pane = new JPanel(new BorderLayout());
		pane.add(txtRatioErrSd, BorderLayout.WEST);
		right.add(pane);
		
		
		JPanel body = new JPanel(new BorderLayout());
		main.add(body, BorderLayout.CENTER);
		
		JPanel paneValues = new JPanel(new BorderLayout());
		body.add(paneValues, BorderLayout.NORTH);
		paneValues.setVisible(dataset != null);
		
		
		JButton btnMore = new JButton(new AbstractAction("More stat.") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				moreStat();
			}
		});
		paneValues.add(btnMore, BorderLayout.EAST);
		
		JPanel paneGraphList = new JPanel(new GridLayout(1, 0));
		body.add(paneGraphList, BorderLayout.CENTER);
		graphList = model.getGraphList();
		graphList2 = model.getGraphList();
		for (int i = 0; i < graphList.size(); i++) {
			final Graph graph = graphList.get(i);
			
			final Graph graph2 = graphList2.get(i);
			JPanel gPanel = new JPanel(new BorderLayout());
			paneGraphList.add(gPanel);
			
			gPanel.add((Component)graph, BorderLayout.CENTER);
			JPanel toolbar = new JPanel();
			gPanel.add(toolbar, BorderLayout.SOUTH);
			
			JButton btnZoom = MiscUtil.makeIconButton(
				getClass().getResource(Constants.IMAGES_DIR + "zoomin-16x16.png"), 
				"zoom", "Zoom", "Zoom", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						if (graph2 instanceof RModelMultiVarGraph) {
							RModelMultiVarGraph.showDlg(comp, model, dataset, ((RModelMultiVarGraph) graph2).getViewOption());
						}
						else {
							final JDialog dlg = new JDialog(MiscUtil.getFrameForComponent(comp), "Graph", false);
							dlg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
							dlg.setSize(600, 400);
							dlg.setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
							
							dlg.setLayout(new BorderLayout());
							dlg.add( (Component)graph2, BorderLayout.CENTER);
							
							JPanel footer = new JPanel();
							dlg.add(footer, BorderLayout.SOUTH);
							JButton btnExport = new JButton("Export image");
							btnExport.addActionListener(new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									// TODO Auto-generated method stub
									graph2.exportImage();
								}
							});
							footer.add(btnExport);
							
							dlg.setVisible(true);
						}
					}
				});
			btnZoom.setMargin(new Insets(0, 0 , 0, 0));
			toolbar.add(btnZoom);
			
			JButton btnPrint = MiscUtil.makeIconButton(
				getClass().getResource(Constants.IMAGES_DIR + "print-16x16.png"), 
				"print", "Print", "Print", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
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
									((PlotGraphExt) graph2).paint(graphics, (int) pageFormat.getImageableWidth(), (int) pageFormat.getImageableHeight());
									
									return PAGE_EXISTS;
								}
							};
							
			  				PrinterJob pjob = PrinterJob.getPrinterJob();
			  				
			    			//set a HelloPrint as the target to print
			  				pjob.setPrintable(printable);
			  				
			  				//get the print dialog, continue if canel
			  				//is not clicked
		    				if (pjob.printDialog()) {
		    					//print the target (HelloPrint)
		    					pjob.print();
		    				}
		    				
						}
						catch (Throwable ex) {
							ex.printStackTrace();
						}
					}
				});
				btnPrint.setMargin(new Insets(0, 0 , 0, 0));
				toolbar.add(btnPrint);
				
				JButton btnOption = MiscUtil.makeIconButton(
						getClass().getResource(Constants.IMAGES_DIR + "option-16x16.png"), 
						"view_option", "View Option", "View Option", 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								graph.setupViewOption();
								((PlotGraphExt)graph2).setViewOption(((PlotGraphExt)graph).getViewOption());
							}
						}
				);
				btnOption.setMargin(new Insets(0, 0 , 0, 0));
				toolbar.add(btnOption);
		}
		
		
		JPanel footer = new JPanel(new BorderLayout());
		main.add(footer, BorderLayout.SOUTH);
		
		JPanel control = new JPanel();
		footer.add(control, BorderLayout.CENTER);
		
		
		DefaultTableModel tbm = new DefaultTableModel() {

			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				return (column == 1);
			}
		};
		tbm.setColumnIdentifiers(new String[] {"Regressor", "Value"});
		for (String regressor : model.getRegressorNames()) {
			Vector<Object> rowData = new Vector<Object>();
			rowData.add(regressor);
			rowData.add(new Double(0));
			tbm.addRow(rowData);
		}
		this.tblRegression = new JTable(tbm);
		this.tblRegression.setPreferredScrollableViewportSize(new Dimension(200, 60));   
		JScrollPane scroll = new JScrollPane(this.tblRegression);
		control.add(scroll);
		
	    JButton btnCalc = new JButton("Calculate");
	    btnCalc.addActionListener(new ActionListener() {
	    	public void actionPerformed(ActionEvent arg0) {
	    		calc();
	    	}
	    });
		control.add(btnCalc);

		this.txtCalc = new JTextField(12);
		txtCalc.setEditable(false);
		control.add(txtCalc);
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(SwingUtilities.isRightMouseButton(e) ) {

					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
			
		});

		setVisible(true);
	}
	
	
	private RModelDlg getThis() {
		return this;
	}
	
	
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miBigZoom = MiscUtil.makeMenuItem(null, "Big zoom", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					final JDialog dlg = new JDialog(MiscUtil.getFrameForComponent(getThis()), "Big zoom", false);
					dlg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					dlg.setSize(600, 400);
					dlg.setLocationRelativeTo(MiscUtil.getFrameForComponent(getThis()));
					
					dlg.setLayout(new BorderLayout());
					JPanel body = new JPanel(new GridLayout(1, 0));
					dlg.add(body, BorderLayout.CENTER);
					
					for (Graph graph : graphList2) {
						body.add( (Component)graph);
					}
					
					JPanel footer = new JPanel();
					dlg.add(footer, BorderLayout.SOUTH);
					
					JButton btnExport = new JButton("Export image");
					btnExport.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							mergeGraphImages(getThis(), graphList2);
						}
					});
					footer.add(btnExport);
					
					dlg.setVisible(true);
				}
			});
		contextMenu.add(miBigZoom);
		
		JMenuItem miExport = MiscUtil.makeMenuItem(null, "Export graphs to image", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						mergeGraphImages(getThis(), graphList);
					}
				});
		contextMenu.add(miExport);
		
		return contextMenu;
	}
	
	
	private void calc() {
		final HashMap<String, Double> regressorValues = new HashMap<String, Double>();
		
		int n = tblRegression.getModel().getRowCount();
		TableModel tbm = tblRegression.getModel();
		for (int i = 0; i < n; i++) {
			String name = tbm.getValueAt(i, 0).toString();
			String value = tbm.getValueAt(i, 1).toString();
			
			regressorValues.put(name, Double.parseDouble(value));
		}
		
		double result = model.eval(regressorValues);
		this.txtCalc.setText(MathUtil.format(result));
		this.txtCalc.setCaretPosition(0);
	}

	
	private void moreStat() {
		double[] datarow = new RModelAssoc(model).
				getYcalc(dataset);
		
		if (datarow.length == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"No data", 
					"No data", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		new EstiStatDlg(this, datarow, true);
	}
	
	
	private static void mergeGraphImages(Component comp, ArrayList<Graph> graphList) {
		if (graphList.size() == 0)
			return;
		
		File chooseFile = FileUtil.chooseFile2(
				comp, false, new String[] { "png" }, new String[] { "PNG file (*.png)"}, "png");
		if (chooseFile == null) {
			JOptionPane.showMessageDialog(
					comp, 
					"Image not exported", 
					"Image not exported", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		int bigWidth = 0;
		int maxHeight = Integer.MIN_VALUE;
		
		for (Graph graph : graphList) {
			bigWidth += graph.getOuterBox().width;
			maxHeight = Math.max(maxHeight, graph.getOuterBox().height);
		}
		
		BufferedImage bigImage = new BufferedImage(bigWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D bigGraphics = bigImage.createGraphics();
		
		int x = 0;
		for (Graph graph : graphList) {
			Rectangle outerBox = graph.getOuterBox();
			BufferedImage image = new BufferedImage(outerBox.width, outerBox.height, BufferedImage.TYPE_INT_ARGB);
			Graphics2D graphics = image.createGraphics();
			graphics.setColor(new Color(0, 0, 0));
			
			if (graph instanceof PlotGraphExt)
				((PlotGraphExt)graph).paint(graphics, outerBox.width, outerBox.height);
			else
				graph.paint(graphics);
			
			bigGraphics.drawImage(image, x, 0, null);
			x += outerBox.width;
		}
		
		try {
			ImageIO.write(bigImage, "png", chooseFile);
			
			JOptionPane.showMessageDialog(
					comp, 
					"Big image exported successfully", 
					"Big image exported successfully", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}



class EstiStatDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	private double[] data = new double[0];
	
	
	public EstiStatDlg(double[] values) {
		this(null, values);
	}

	
	public EstiStatDlg(Component comp, double[] data) {
		this(comp, data, false);
		this.data = data;
	}
	
	
	public EstiStatDlg(Component comp, double[] data, boolean modal) {
		super(MiscUtil.getFrameForComponent(comp), "Estimated values Statistics", modal);
		this.data = data;
	
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);

		Vector<Vector<Object>> rowData = new Vector<Vector<Object>>();
		rowData.add(createRow());
		JTable tblStat = new JTable(new DefaultTableModel(rowData, createColumns()) {
	    	/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int rowIndex, int columnIndex) {
	        	return false;
	    	}
		});
		
		tblStat.setPreferredScrollableViewportSize(new Dimension(200, 40));   
		header.add(new JScrollPane(tblStat), BorderLayout.CENTER);
		
		JPanel paneStat = new JPanel(new BorderLayout());
		header.add(paneStat, BorderLayout.SOUTH);
		
		JButton btnCopy = MiscUtil.makeIconButton(
				getClass().getResource(Constants.IMAGES_DIR + "copy-16x16.png"), 
				"copy", "Copy estimated values to clipboard", "Copy values to clipboard", 
					
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						if (!Config.testClipboard())
							return;
						
						StringBuffer buffer = DSUtil.toDataColBuffer(getThis().data);
							
						Config.getClipboardProcessor().setText(buffer.toString());
					}
			});
		btnCopy.setMargin(new Insets(0, 0 , 0, 0));
		paneStat.add(btnCopy, BorderLayout.EAST);

		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		double bintWidth = (Fmath.maximum(data) - Fmath.minimum(data)) / 
				StatTable.BIN_NUMBER;
		PlotGraph graph = FlanaganStat.histogramBinsPlot2(data, bintWidth);
		body.add(graph, BorderLayout.CENTER);
		graph.setBackground(Color.WHITE);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		//
		JButton btnClose = new JButton(new AbstractAction("Close") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(btnClose);
		
		setSize(600, 480);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		setVisible(true);
	}
	
	
	private EstiStatDlg getThis() {
		return this;
	}
	
	
	private Vector<Object> createRow() {
		Vector<Object> row = new Vector<Object>();
		
		Stat stat = new Stat(data);
		
		row.add(MathUtil.round(stat.minimum()));
		row.add(MathUtil.round(stat.maximum()));
		row.add(MathUtil.round(stat.mean()));
		row.add(MathUtil.round(stat.median()));
		row.add(MathUtil.round(stat.standardDeviation()));
		row.add(MathUtil.round(stat.standardError()));
		row.add(MathUtil.round(stat.variance()));
		row.add(MathUtil.round(stat.momentSkewness()));
		row.add(MathUtil.round(stat.kurtosis()));
		
		return row;
	}

	
	private Vector<String> createColumns() {
		Vector<String> columns = new Vector<String>();
		
		columns.add("Min");
		columns.add("Max");
		columns.add("Mean");
		columns.add("Median");
		columns.add("Sd");
		columns.add("Se");
		columns.add("Variance");
		columns.add("Skewness");
		columns.add("Kurtosis");
		
		return columns;
	}
}

