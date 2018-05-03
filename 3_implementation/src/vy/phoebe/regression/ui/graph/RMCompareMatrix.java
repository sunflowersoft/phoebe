package vy.phoebe.regression.ui.graph;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.NumberFormatter;

import vy.phoebe.regression.RModel;
import vy.phoebe.util.Constants;
import vy.phoebe.util.DSUtil;
import vy.phoebe.util.FileUtil;
import vy.phoebe.util.MiscUtil;

public class RMCompareMatrix extends JPanel implements Graph {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static final int GAP = 5;
	
	private final static int FONT_INC = 2;
	
	
	protected RModel[] modelList = new RModel[] { };
	
	protected List<List<Graph>> graphMatrix = new ArrayList<List<Graph>>();
	
	protected MultiTextGraph noteTextGraph = null;
	
	protected RMCompareMatrixViewOption viewOption = new RMCompareMatrixViewOption();
	
	
	public RMCompareMatrix(RModel[] modelList) {
		super();
		
		this.modelList = modelList;
		setLayout(new BorderLayout(GAP, GAP));
		
		JPanel body = new JPanel(new GridLayout(0, 1, GAP, GAP));
		add(body, BorderLayout.CENTER);
		graphMatrix.clear();
		
		ArrayList<String> authors = new ArrayList<String>();
		ArrayList<String> criterons = new ArrayList<String>();
		ArrayList<String> xLegends = new ArrayList<String>();
		ArrayList<String> yLegends = new ArrayList<String>();
		
		for (int i = 0; i < modelList.length; i++) {
			RModel model = modelList[i];
			String author = model.getType(); 
			authors.add(author);
			
			List<Graph> graphList = new ArrayList<Graph>();
			JPanel row = new JPanel(new BorderLayout());
			body.add(row);
			
			TextGraph authorGraph = new TextGraph();
			authorGraph.incFontSize(FONT_INC);
			authorGraph.setText(author, true, true);
			authorGraph.setBackground(Color.CYAN);
			row.add(authorGraph, BorderLayout.WEST);
			graphList.add(authorGraph);
			
			JPanel criterionPane = new JPanel(new GridLayout(1, 0, GAP, GAP));
			row.add(criterionPane, BorderLayout.CENTER);
			ArrayList<Graph> criterionGraphs = model.getGraphList();
			boolean criterionAdded = criterons.size() > 0;
			boolean xLegendsAdded = xLegends.size() > 0;
			boolean yLegendsAdded = yLegends.size() > 0;
			
			for (Graph criterionGraph : criterionGraphs) {
				criterionPane.add((Component)criterionGraph);
				GraphViewOption opt = criterionGraph.getViewOption();
				
				if (!criterionAdded)
					criterons.add(criterionGraph.getViewOption().graphTitle.trim());

				if (!xLegendsAdded)
					xLegends.add(opt.legends[0]);

				if (!yLegendsAdded)
					yLegends.add(opt.legends[1]);

				if (criterionGraph instanceof PlotGraphExt)
					((PlotGraphExt)criterionGraph).setGraphTitle(((PlotGraphExt)criterionGraph).getGraphFeature());
				
				graphList.add(criterionGraph);
			}
			
			graphMatrix.add(graphList);
		}
		
		viewOption.criterions = criterons.toArray(new String[] { }); 
		viewOption.authors = authors.toArray(new String[] { });
		viewOption.xLegends = xLegends.toArray(new String[] { });
		viewOption.yLegends = yLegends.toArray(new String[] { });
		
		// Header criterion
		List<Graph> headerGraphList = new ArrayList<Graph>();
		JPanel header = new JPanel(new BorderLayout());
		
		TextGraph emptyGraph = new TextGraph();
		emptyGraph.setText("", false, false);
		if (graphMatrix.size() > 0)
			emptyGraph.setFixedSize(graphMatrix.get(0).get(0).getOuterBox().width, 0);
		headerGraphList.add(emptyGraph);
		header.add(emptyGraph, BorderLayout.WEST);
		add(header, BorderLayout.NORTH);
		graphMatrix.add(0, headerGraphList);
		
		JPanel criterionPane = new JPanel(new GridLayout(1, 0, GAP, GAP));
		header.add(criterionPane, BorderLayout.CENTER);
		for (String criterion : viewOption.criterions) {
			TextGraph criterionGraph = new TextGraph();
			criterionGraph.incFontSize(FONT_INC);
			criterionGraph.setText(criterion, true, false);
			criterionGraph.setBackground(Color.CYAN);
			headerGraphList.add(criterionGraph);
			
			criterionPane.add((Component)criterionGraph);
		}

		
		// Footer model information
		StringBuffer textInfo = new StringBuffer();
		for (int i = 0; i < modelList.length; i++) {
			RModel model = modelList[i];
			if (i > 0)
				textInfo.append("\n");
			
			String text = viewOption.authors[i] + ": " + model.getNiceForm();
			textInfo.append(text);
		}
		noteTextGraph = new MultiTextGraph();
		noteTextGraph.incFontSize(FONT_INC);
		noteTextGraph.setText(textInfo.toString(), false, false);
		add(noteTextGraph, BorderLayout.SOUTH);
		
	}
	
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(
				this, 
				"Not support this method", 
				"Not support this method", 
				JOptionPane.INFORMATION_MESSAGE);

		return 0;
	}


	@Override
	public void setupViewOption() {
		// TODO Auto-generated method stub
		
		RMCompareMatrixViewOption opt = (RMCompareMatrixViewOption)getViewOption();
		RMCompareMatrixViewOptionDlg dlg = new RMCompareMatrixViewOptionDlg(this, opt);
		RMCompareMatrixViewOption returnOpt = dlg.getViewOption();
		if (returnOpt == null)
			return;
		
		int n = Math.min(returnOpt.criterions.length, opt.criterions.length);
		for (int i = 0; i < n; i++) {
			opt.criterions[i] = returnOpt.criterions[i];
		}
		
		n = Math.min(returnOpt.authors.length, opt.authors.length);
		for (int i = 0; i < n; i++) {
			opt.authors[i] = returnOpt.authors[i];
		}
		
		n = Math.min(returnOpt.xLegends.length, opt.xLegends.length);
		for (int i = 0; i < n; i++) {
			opt.xLegends[i] = returnOpt.xLegends[i];
		}

		n = Math.min(returnOpt.yLegends.length, opt.yLegends.length);
		for (int i = 0; i < n; i++) {
			opt.yLegends[i] = returnOpt.yLegends[i];
		}

		List<Graph> header = graphMatrix.get(0);
		for (int j = 1; j < header.size(); j++) {
			Graph authorGraph = header.get(j);
			((TextGraph)authorGraph).setText(opt.criterions[j - 1], true, false);
		}
		
		for (int i = 1; i < graphMatrix.size(); i++) {
			List<Graph> row = graphMatrix.get(i);
			TextGraph criterionGraph = (TextGraph)row.get(0);
			criterionGraph.setText(opt.authors[i-1], true, true);
			
			for (int j = 1; j < row.size(); j++) {
				PlotGraphExt graph = (PlotGraphExt)row.get(j);
				graph.setXaxisLegend(opt.xLegends[j -1]);
				graph.setYaxisLegend(opt.yLegends[j -1]);
			}
		}
		
		StringBuffer textInfo = new StringBuffer();
		for (int i = 0; i < modelList.length; i++) {
			RModel model = modelList[i];
			if (i > 0)
				textInfo.append("\n");
			
			String text = viewOption.authors[i] + ": " + model.getNiceForm();
			textInfo.append(text);
		}
		noteTextGraph.setText(textInfo.toString(), false, false);
		
		revalidate();
		repaint();
		
		for (List<Graph> graphList : graphMatrix) {
			for (Graph graph : graphList) {
				((Component)graph).repaint();
			}
			
		}
		noteTextGraph.repaint();
	}


	@Override
	public GraphViewOption getViewOption() {
		// TODO Auto-generated method stub
		return viewOption;
	}


	@Override
	public void exportImage() {
		// TODO Auto-generated method stub
		File chooseFile = FileUtil.chooseFile2(
				MiscUtil.getFrameForComponent(this), false, new String[] { "png" }, new String[] { "PNG file"}, "png");
		if (chooseFile == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Image not exported", 
					"Image not exported", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		BufferedImage bigImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
		Graphics2D bigGraphics = bigImage.createGraphics();
		
		int y = 0;
		for (List<Graph> graphList : graphMatrix) {
			
			int x = 0;
			int maxHeight = Integer.MIN_VALUE;
			for (Graph graph : graphList) {
				Rectangle outerBox = graph.getOuterBox();
				BufferedImage image = new BufferedImage(outerBox.width, outerBox.height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D graphics = image.createGraphics();
				graphics.setColor(new Color(0, 0, 0));
				
				if (graph instanceof PlotGraphExt)
					((PlotGraphExt)graph).paint(graphics, outerBox.width, outerBox.height);
				else
					graph.paint(graphics);
				
				bigGraphics.drawImage(image, x, y, null);
				x += outerBox.width + GAP;
				maxHeight = Math.max(maxHeight, outerBox.height); 
			}
			
			y += maxHeight + GAP;
		}
		
		
		Rectangle outerBox = noteTextGraph.getOuterBox();
		BufferedImage image = new BufferedImage(outerBox.width, outerBox.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D graphics = image.createGraphics();
		graphics.setColor(new Color(0, 0, 0));
		noteTextGraph.paint(graphics);
		bigGraphics.drawImage(image, 0, y, null);
		
		try {
			ImageIO.write(bigImage, "png", chooseFile);
			
			JOptionPane.showMessageDialog(
					MiscUtil.getFrameForComponent(this), 
					"Big image exported successfully", 
					"Big image exported successfully", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	@Override
	public Rectangle getOuterBox() {
		// TODO Auto-generated method stub
		return new Rectangle(0, 0, getWidth(), getHeight());
	}


	public static void showDlg(Component comp, RModel[] modelList) {
		new RMCompareMatrixDlg(comp, modelList);
	}


}


class RMCompareMatrixDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	RMCompareMatrix matrix = null;
	
	JScrollPane scrollPane = null;
	
	JLabel lblWidth = null;
	
	JLabel lblHeight = null;
	
	public RMCompareMatrixDlg(Component comp, RModel[] modelList) {
		super(MiscUtil.getFrameForComponent(comp), "Comparision matrix", false);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		
		setLayout(new BorderLayout());
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		matrix = new RMCompareMatrix(modelList);
		scrollPane = new JScrollPane(matrix);
		body.add(scrollPane, BorderLayout.CENTER);
		
		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		body.add(toolbar, BorderLayout.SOUTH);
		
		lblWidth = new JLabel("Width: (" + matrix.getWidth() + ")");
		toolbar.add(lblWidth);
		
		JButton btnWidthZoomin = MiscUtil.makeIconButton(
			matrix.getClass().getResource(Constants.IMAGES_DIR + "zoomin-16x16.png"), 
			"width_zoom_in", "Width zoom in", "Width zoom in", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int width = (int) (matrix.getWidth() * 1.1);
					setMatrixSize(new Dimension(width, matrix.getHeight()));
				}
			});
		btnWidthZoomin.setMargin(new Insets(0, 0, 0, 0));
		toolbar.add(btnWidthZoomin);
		
		JButton btnWidthZoomout = MiscUtil.makeIconButton(
		matrix.getClass().getResource(Constants.IMAGES_DIR + "zoomout-16x16.png"), 
		"width_zoom_out", "Width zoom out", "Width zoom out", 
		new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int width = (int) (matrix.getWidth() * 0.9);
				setMatrixSize(new Dimension(width, matrix.getHeight()));
			}
		});
		btnWidthZoomout.setMargin(new Insets(0, 0, 0, 0));
		toolbar.add(btnWidthZoomout);

		JButton btnWidthZoomnone = MiscUtil.makeIconButton(
		matrix.getClass().getResource(Constants.IMAGES_DIR + "zoom-none-16x16.png"), 
		"width_zoom_none", "Width normal", "Width normal", 
		new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setMatrixSize(new Dimension(0, matrix.getHeight()));
			}
		});
		btnWidthZoomnone.setMargin(new Insets(0, 0, 0, 0));
		toolbar.add(btnWidthZoomnone);

		toolbar.add(new JLabel("    "));
		lblHeight = new JLabel("Height");
		toolbar.add(lblHeight);
		
		JButton btnHeightZoomin = MiscUtil.makeIconButton(
			matrix.getClass().getResource(Constants.IMAGES_DIR + "zoomin-16x16.png"), 
			"height_zoom_in", "Height zoom in", "Height zoom in", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int height = (int) (matrix.getHeight() * 1.1);
					setMatrixSize(new Dimension(matrix.getWidth(), height));
				}
			});
		btnHeightZoomin.setMargin(new Insets(0, 0, 0, 0));
		toolbar.add(btnHeightZoomin);
			
		JButton btnHeightZoomout = MiscUtil.makeIconButton(
			matrix.getClass().getResource(Constants.IMAGES_DIR + "zoomout-16x16.png"), 
			"height_zoom_out", "Height zoom out", "Height zoom out", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int height = (int) (matrix.getHeight() * 0.9);
					setMatrixSize(new Dimension(matrix.getWidth(), height));
				}
			});
		btnHeightZoomout.setMargin(new Insets(0, 0, 0, 0));
		toolbar.add(btnHeightZoomout);

		JButton btnHeightZoomnone = MiscUtil.makeIconButton(
				matrix.getClass().getResource(Constants.IMAGES_DIR + "zoom-none-16x16.png"), 
				"height_zoom_none", "Height normal", "Height normal", 
		new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setMatrixSize(new Dimension(matrix.getWidth(), 0));
			}
		});
		btnHeightZoomnone.setMargin(new Insets(0, 0, 0, 0));
		toolbar.add(btnHeightZoomnone);
		
		toolbar.add(new JLabel("    "));
		JButton btnSetSize = MiscUtil.makeIconButton(
				matrix.getClass().getResource(Constants.IMAGES_DIR + "setsize-16x16.gif"), 
				"set_size", "Set size", "set size", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					setupMatrixSize();
				}
			});
		btnSetSize.setMargin(new Insets(0, 0, 0, 0));
		toolbar.add(btnSetSize);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnExport = new JButton("Export image");
		btnExport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				matrix.exportImage();
			}
		});
		footer.add(btnExport);
		
		JButton btnViewOption = new JButton("View option");
		btnViewOption.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				matrix.setupViewOption();
			}
		});
		footer.add(btnViewOption);

		addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub
				updateLabels();
			}
			
		});
		addComponentListener(new ComponentListener() {
			
			@Override
			public void componentShown(ComponentEvent e) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void componentResized(ComponentEvent e) {
				// TODO Auto-generated method stub
				updateLabels();
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void componentHidden(ComponentEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		setVisible(true);
	}
	
	
	private RMCompareMatrixDlg getThis() {
		return this;
	}
	
	
	private void setMatrixSize(Dimension d) {
		matrix.setSize(d);
		matrix.setPreferredSize(d);
		
		revalidate();
		repaint();
		
		scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getMaximum());
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
		scrollPane.getHorizontalScrollBar().setValue(scrollPane.getHorizontalScrollBar().getMinimum());
		scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMinimum());

		for (List<Graph> graphList : matrix.graphMatrix) {
			for (Graph graph : graphList) {
				((Component)graph).repaint();
			}
			
		}
		updateLabels();
	}
	

	private void setupMatrixSize() {
		final JDialog dlg = new JDialog(MiscUtil.getFrameForComponent(this), "Setup matrix size", true);
		dlg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		dlg.setSize(200, 200);
		dlg.setLocationRelativeTo(MiscUtil.getFrameForComponent(this));
		dlg.setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		dlg.add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		left.add(new JLabel("Width:"));
		left.add(new JLabel("Height:"));
		
		JPanel center = new JPanel(new GridLayout(0, 1));
		header.add(center, BorderLayout.CENTER);
		final JFormattedTextField txtWidth = new JFormattedTextField(new NumberFormatter());
		txtWidth.setValue(matrix.getWidth());
		center.add(txtWidth);
		
		final JFormattedTextField txtHeight = new JFormattedTextField(new NumberFormatter());
		txtHeight.setValue(matrix.getHeight());
		center.add(txtHeight);
		
		JPanel footer = new JPanel();
		dlg.add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				if (txtWidth.getValue() == null || txtHeight.getValue() == null) {
						
					JOptionPane.showMessageDialog(
							getThis(), 
							"Empty values", 
							"Empty values", 
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				int width = ((Number) txtWidth.getValue()).intValue();
				int height = ((Number) txtHeight.getValue()).intValue();
				if (width <= 0 || height <= 0) {
					JOptionPane.showMessageDialog(
							getThis(), 
							"Invalid dimension", 
							"Invalid dimension", 
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				setMatrixSize(new Dimension(width, height));
				dlg.dispose();
			}
		});
		footer.add(ok);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dlg.dispose();
			}
		});
		footer.add(cancel);
		
		dlg.setVisible(true);
	}
	
	
	private void updateLabels() {
		lblWidth.setText("Width: (" + matrix.getWidth() + ")");
		lblHeight.setText("Height: (" + matrix.getHeight() + ")");
		
	}
	
	
}


class RMCompareMatrixViewOption extends GraphViewOption {
	
	
	public String[] criterions = new String[] { };
	
	
	public String[] authors = new String[] { };
	
	
	public String[] xLegends = new String[] { };
	
	
	public String[] yLegends = new String[] { };

	
	public RMCompareMatrixViewOption() {
		
	}
	
	
	public RMCompareMatrixViewOption(String[] criterons, String[] authors, String[] xLegends, String[] yLegends) {
		this.criterions = criterons;
		this.authors = authors;
		this.xLegends = xLegends;
		this.yLegends = yLegends;
	}

}



class RMCompareMatrixViewOptionDlg extends JDialog {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	JTextField criterions = null;

	JTextField authors = null;
	
	JTextField xLegends = null;

	JTextField yLegends = null;

	RMCompareMatrixViewOption returnViewOption = null;
	
	
	public RMCompareMatrixViewOptionDlg(Component comp, RMCompareMatrixViewOption option) {
		super (MiscUtil.getFrameForComponent(comp), "Plot graph option", true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 200);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Criterions: "));
		left.add(new JLabel("Authors: "));
		left.add(new JLabel("X legends: "));
		left.add(new JLabel("Y legends: "));
		
		JPanel center = new JPanel(new GridLayout(0, 1));
		header.add(center, BorderLayout.CENTER);

		criterions = new JTextField();
		if (option != null && option.criterions != null && option.criterions.length > 0)
			criterions.setText(DSUtil.toText(option.criterions, ","));
		else
			criterions.setText("");
		center.add(criterions);

		authors = new JTextField();
		if (option != null && option.authors != null && option.authors.length > 0)
			authors.setText(DSUtil.toText(option.authors, ","));
		else
			authors.setText("");
		center.add(authors);

		xLegends = new JTextField();
		if (option != null && option.xLegends != null && option.xLegends.length > 0)
			xLegends.setText(DSUtil.toText(option.xLegends, ","));
		else
			xLegends.setText("");
		center.add(xLegends);

		yLegends = new JTextField();
		if (option != null && option.yLegends != null && option.yLegends.length > 0)
			yLegends.setText(DSUtil.toText(option.yLegends, ","));
		else
			yLegends.setText("");
		center.add(yLegends);

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
					returnViewOption = new RMCompareMatrixViewOption();
					
					String criterionText = criterions.getText().trim();
					List<String> list = DSUtil.split(criterionText, ",", null);
					if (list.size() > 1)
						returnViewOption.criterions = list.toArray(new String[] { });
					else
						returnViewOption.criterions = new String[] { };
					
					String authorText = authors.getText().trim();
					list = DSUtil.split(authorText, ",", null);
					if (list.size() > 1)
						returnViewOption.authors = list.toArray(new String[] { });
					else
						returnViewOption.authors = new String[] { };
					
					String xLegendText = xLegends.getText().trim();
					list = DSUtil.split(xLegendText, ",", null);
					if (list.size() > 1)
						returnViewOption.xLegends = list.toArray(new String[] { });
					else
						returnViewOption.xLegends = new String[] { };

					String yLegendText = yLegends.getText().trim();
					list = DSUtil.split(yLegendText, ",", null);
					if (list.size() > 1)
						returnViewOption.yLegends = list.toArray(new String[] { });
					else
						returnViewOption.yLegends = new String[] { };
				
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
	
	
	public RMCompareMatrixViewOption getViewOption() {
		return returnViewOption;
	}
	
	
	private boolean validateValues() {
		return true;
	}
	
	
}
