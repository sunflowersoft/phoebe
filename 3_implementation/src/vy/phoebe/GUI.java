package vy.phoebe;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import vy.phoebe.dataset.Dataset;
import vy.phoebe.dataset.ui.SelDatasetTable2;
import vy.phoebe.dataset.ui.SelMeasureEvent;
import vy.phoebe.dataset.ui.SelMeasureListener;
import vy.phoebe.dataset.ui.StatDlg;
import vy.phoebe.estimator.Estimator;
import vy.phoebe.math.MathUtil;
import vy.phoebe.regression.RModel;
import vy.phoebe.regression.RModelAssoc;
import vy.phoebe.regression.RModelGroupList;
import vy.phoebe.regression.RModelList;
import vy.phoebe.regression.RModelListEx;
import vy.phoebe.regression.ui.RMCompareDlg;
import vy.phoebe.regression.ui.RMParseDlg;
import vy.phoebe.regression.ui.RModelGroupListDlg;
import vy.phoebe.regression.ui.RModelListPane;
import vy.phoebe.regression.ui.graph.PModelGraphDlg;
import vy.phoebe.util.Constants;
import vy.phoebe.util.FileUtil;
import vy.phoebe.util.MiscUtil;

public class GUI extends JFrame implements SelMeasureListener {

	private static final long serialVersionUID = 1L;

	
	private JTextField txtOpen = null;

	
	private SelDatasetTable2 tblDataset = null;
	
	
	protected JTextField txtFitness = null;

	
	protected JTextField txtMaxResults = null;

	
	@SuppressWarnings({ "rawtypes" })
	private JList lstResponses = null;
	
	
	private JButton btnSaveResults = null;
	
	
	private JButton btnViewGroups = null;
	
	
	private JPanel paneResultBody = null;
	
	
	private RModelGroupList resultList = new RModelGroupList();
	
	
	private RModelGroupList groupList = new RModelGroupList();

	
	public GUI() {
		super("Phoebe - The software of fetal weight and age estimation");

		
		MiscUtil.TangChiNeThuongChiThatNhieu(this);

		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				exit();
				super.windowClosing(e);
			}
			
		});
		
		init();
		
		setSize(800, 600);
		setLocationRelativeTo(null);
		setVisible(true);
		
	}
	
	
	public Dataset getDataset() {
		return tblDataset.getDataset();
	}
	
	
	@SuppressWarnings({ "rawtypes"})
	private void init() {
		setJMenuBar(createMenuBar());
		
		Container contentPane = this.getContentPane();
		
		JSplitPane main = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		contentPane.add(main);
		main.setDividerLocation(300);
		
		JPanel input = new JPanel(new BorderLayout(4, 4));
		main.add(input);
		
		JPanel inputHeader = new JPanel(new BorderLayout(8, 8));
		input.add(inputHeader, BorderLayout.NORTH);
		JPanel title = new JPanel();
		inputHeader.add(title, BorderLayout.NORTH);
		JLabel lbTitle = new JLabel("  PHOEBE - THE ESTIMATOR OF FETAL WEIGHT AND AGE");
		
		lbTitle.setIcon(new ImageIcon(
				getClass().getResource(Constants.IMAGES_DIR + "TangChiNe-32x32.png")));
		lbTitle.setForeground(Color.RED);
		
		title.add(lbTitle);
		JButton btnOpen = new JButton(new AbstractAction("Open") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				openDataset();
			}
		});
		inputHeader.add(btnOpen, BorderLayout.WEST);
		this.txtOpen = new JTextField();
		this.txtOpen.setEditable(false);
		inputHeader.add(this.txtOpen, BorderLayout.CENTER);
		//
		JButton btnRefresh = MiscUtil.makeIconButton(
			getClass().getResource(Constants.IMAGES_DIR + "refresh-16x16.png"), 
			"refresh", "Refresh", "Refresh", 
				
			new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					refresh();
				}
		});
		btnRefresh.setMargin(new Insets(0, 0 , 0, 0));
		inputHeader.add(btnRefresh, BorderLayout.EAST);

		
		JPanel inputBody = new JPanel(new BorderLayout());
		input.add(inputBody, BorderLayout.CENTER);
		this.tblDataset = new SelDatasetTable2();
		this.tblDataset.addMeasureSelectListener(this);
		inputBody.add(new JScrollPane(this.tblDataset), BorderLayout.CENTER);

		JPanel inputFooter = new JPanel(new BorderLayout());
		input.add(inputFooter, BorderLayout.SOUTH);
		//
		JPanel option = new JPanel(new FlowLayout());
		inputFooter.add(option, BorderLayout.WEST);
		this.txtFitness = new JTextField(5);
		this.txtFitness.setText("" + Config.FITNESS);
		option.add(new JLabel("Fitness (R): "));
		option.add(this.txtFitness);
		//
		this.txtMaxResults = new JTextField(5);
		this.txtMaxResults.setText("" + Config.MAX_RESULTS);
		
		
		option.add(new JLabel("Max results: "));
		option.add(this.txtMaxResults);
		//
		JButton btnMoreOption = MiscUtil.makeIconButton(
			getClass().getResource(Constants.IMAGES_DIR + "option-16x16.png"), 
			"moreoptions", "More options", "More options", 
			
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					moreOptions();
				}
		});
		btnMoreOption.setMargin(new Insets(0, 0, 0, 0));
		option.add(btnMoreOption);
		
		//
		JPanel stat = new JPanel(new FlowLayout());
		inputFooter.add(stat, BorderLayout.EAST);
		JButton btnStat = new JButton(new AbstractAction("Stat.") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				stat();
			}
		});
		stat.add(btnStat);

		
		JPanel result = new JPanel(new BorderLayout(4, 4));
		main.add(result);
		
		
		JPanel resultHeader = new JPanel(new FlowLayout());
		result.add(resultHeader, BorderLayout.NORTH);
		//
		this.lstResponses = new JList();
		this.lstResponses.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		this.lstResponses.setVisibleRowCount(2);
		resultHeader.add(new JScrollPane(this.lstResponses));
		//
		JButton btnEstimate = new JButton(new AbstractAction("Estimate") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				estimate();
			}
		});
		resultHeader.add(btnEstimate);

		
		JPanel paneResult = new JPanel(new BorderLayout(4, 4));
		result.add(paneResult, BorderLayout.CENTER);
		paneResultBody = new JPanel(new GridLayout(1, 0, 4, 4));
		paneResult.add(paneResultBody, BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		result.add(footer, BorderLayout.SOUTH);
		btnViewGroups = new JButton(new AbstractAction("View Groups") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				viewGroups();
			}
		});
		footer.add(btnViewGroups);
		//
		btnSaveResults = new JButton(new AbstractAction("Save Results") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				saveResults();
			}
		});
		footer.add(btnSaveResults);
		
		setResultCtrlVisible();
	}
	
	
	@SuppressWarnings("serial")
	private JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnFile = new JMenu("File");
		mnFile.setMnemonic('F');
		mnBar.add(mnFile);
		
		JMenuItem mniOpen = new JMenuItem(new AbstractAction("Open") {

			@Override
			public void actionPerformed(ActionEvent e) {
				openDataset();
			}
			
		});
		mniOpen.setMnemonic('O');
		mnFile.add(mniOpen);
		
		JMenuItem mniRefresh = new JMenuItem(new AbstractAction("Refresh") {

			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
			
		});
		mniRefresh.setMnemonic('R');
		mnFile.add(mniRefresh);

		mnFile.addSeparator();
		
		JMenuItem mniSaveResults = new JMenuItem(new AbstractAction("Save Results") {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveResults();
			}
			
		});
		mniSaveResults.setMnemonic('S');
		mnFile.add(mniSaveResults);

		mnFile.addSeparator();

		JMenuItem mniExit = new JMenuItem(new AbstractAction("Exit") {

			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
			
		});
		mniExit.setMnemonic('X');
		mnFile.add(mniExit);

		JMenu mnView = new JMenu("View");
		mnView.setMnemonic('V');
		mnBar.add(mnView);
		
		JMenuItem mniStats = new JMenuItem(new AbstractAction("Stats.") {

			@Override
			public void actionPerformed(ActionEvent e) {
				stat();
			}
			
		});
		mniStats.setMnemonic('S');
		mnView.add(mniStats);

		JMenuItem mniGroups = new JMenuItem(new AbstractAction("Groups") {

			@Override
			public void actionPerformed(ActionEvent e) {
				viewGroups();
			}
			
		});
		mniGroups.setMnemonic('G');
		mnView.add(mniGroups);

		JMenu mnAnalyze = new JMenu("Analyze");
		mnAnalyze.setMnemonic('A');
		mnBar.add(mnAnalyze);
		
		JMenuItem mniEstimate = new JMenuItem(new AbstractAction("Estimate") {

			@Override
			public void actionPerformed(ActionEvent e) {
				estimate();
			}
			
		});
		mniEstimate.setMnemonic('E');
		mnAnalyze.add(mniEstimate);

		JMenuItem mniCompare = new JMenuItem(new AbstractAction("Compare") {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				Dataset dataset = tblDataset.getDatasetTableModel().getDataset();
				if (dataset == null) {
					JOptionPane.showMessageDialog(
							getThis(), 
							"Dataset not opened", 
							"Dataset not opened",
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				new RMCompareDlg(getThis(), new RModelListEx(), getDataset());
			}
			
		});
		mniCompare.setMnemonic('C');
		mnAnalyze.add(mniCompare);

		mnAnalyze.addSeparator();
		
		JMenuItem mniParsing = new JMenuItem(new AbstractAction("Parsing") {

			@Override
			public void actionPerformed(ActionEvent e) {
				Dataset dataset = getDataset();
				
				if (dataset == null) {
					JOptionPane.showMessageDialog(
							getThis(), 
							"Warning! Parse without dataset", 
							"Parse without dataset",
							JOptionPane.WARNING_MESSAGE);
					new RMParseDlg(getThis());
				}
				else {
					int ret = JOptionPane.showConfirmDialog(
							getThis(), 
							"Do you want to parse with current dataset ?\n", 
							"Parse with current dataset", 
							JOptionPane.YES_NO_OPTION, 
							JOptionPane.QUESTION_MESSAGE);
						
					if (ret == JOptionPane.OK_OPTION)
						new RMParseDlg(getThis(), dataset);
					else
						new RMParseDlg(getThis());
				}
					
			}
		});
		mniParsing.setMnemonic('P');
		mnAnalyze.add(mniParsing);

		
		JMenu mnTools = new JMenu("Tools");
		mnTools.setMnemonic('T');
		mnBar.add(mnTools);

		JMenuItem mniPercentile = new JMenuItem(new AbstractAction("Percentile") {

			@Override
			public void actionPerformed(ActionEvent e) {
				Dataset dataset = getDataset();
				
				if (dataset == null) {
					JOptionPane.showMessageDialog(
							getThis(), 
							"Dataset not load", 
							"Dataset not load",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				PModelGraphDlg.showDlg(getThis(), dataset, false);
			}
			
		});
		mniPercentile.setMnemonic('P');
		mnTools.add(mniPercentile);

		mnTools.addSeparator();
		
		JMenuItem mniOption = new JMenuItem(new AbstractAction("Option") {

			@Override
			public void actionPerformed(ActionEvent e) {
				moreOptions();
			}
			
		});
		mniOption.setMnemonic('T');
		mnTools.add(mniOption);

		JMenu mnHelp = new JMenu("Help");
		mnHelp.setMnemonic('H');
		mnBar.add(mnHelp);
		
		return mnBar;
	}

	
	private GUI getThis() {
		return this;
	}
	
	
	private void setResultCtrlVisible() {
		btnViewGroups.setVisible(!resultList.isEmpty());
		btnSaveResults.setVisible(!resultList.isEmpty());
	}
	
	
	@SuppressWarnings("deprecation")
	private void estimate() {
		if (!validateOptionsMore())
			return;
			
		clearResult();
		
		String fitnessText = this.txtFitness.getText().trim();
		double fitness = Double.parseDouble(fitnessText);
		if (fitness == -1)
			fitness = Constants.UNUSED;

		String maxResultText = this.txtMaxResults.getText().trim();
		int maxResult = Integer.parseInt(maxResultText);
		
		ArrayList<String> regressors = this.tblDataset.getMeasureList(true);
		Object[] resArray = this.lstResponses.getSelectedValues();
		ArrayList<String> responses = new ArrayList<String>();
		for (Object res : resArray) responses.add((String)res);
		
		Estimator estimator = Config.getCurrentEstimator();
		estimator.setDataset(tblDataset.getDataset());
		estimator.setRegressor(regressors);
		estimator.setParameters(
				Constants.UNUSED, 
				fitness, 
				maxResult);
		try {
			long beginTime = System.currentTimeMillis();
			this.resultList = estimator.estimate(responses, maxResult);
			long endTime = System.currentTimeMillis();
			
			System.out.println(new Date() + 
					": Time elapsed for estimation = " + (endTime - beginTime) + " (milliseconds)");
			
			for (RModelList modelList : this.resultList) {
				RModelListPane modelPane = new RModelListPane(modelList, tblDataset.getDataset());
				 
				this.paneResultBody.add(modelPane);
			}
			
			int ret = JOptionPane.showConfirmDialog(
					this, 
					"Estimate successful. Do you want to group results?", 
					"Grouping", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				
			if (ret == JOptionPane.OK_OPTION)
				grouping();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}

		setResultCtrlVisible();
		
		this.paneResultBody.validate();
		this.paneResultBody.updateUI();
	}
	
	
	private boolean validateOptions() {
		double fitness = -1;
		
		String fitnessText = txtFitness.getText().trim();
		if (fitnessText.length() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Fitness empty", 
					"Invalid fitness", 
					JOptionPane.ERROR_MESSAGE);
			txtFitness.setText("" + Config.FITNESS);
			return false;
		}
		
		try {
			fitness = Double.parseDouble(fitnessText);
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(
					this, 
					"Not number format", 
					"Invalid fitness", 
					JOptionPane.ERROR_MESSAGE);
			txtFitness.setText("" + Config.FITNESS);
			return false;
		}
		
		if (fitness > 1.0) {
			JOptionPane.showMessageDialog(
					this, 
					"Fitness not > 1.0", 
					"Invalid fitness", 
					JOptionPane.ERROR_MESSAGE);
			txtFitness.setText("" + Config.FITNESS);
			return false;
		}
		
		if (fitness < 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Fitness not < 0", 
					"Invalid fitness", 
					JOptionPane.ERROR_MESSAGE);
			txtFitness.setText("" + Config.FITNESS);
			return false;
		}
		
		int maxResults = -1;
		String maxResultsText = txtMaxResults.getText().trim();
		if (maxResultsText.length() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Max results empty", 
					"Invalid max result", 
					JOptionPane.ERROR_MESSAGE);
			txtMaxResults.setText("" + Config.MAX_RESULTS);
			return false;
		}
		
		try {
			maxResults = Integer.parseInt(maxResultsText);
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(
					this, 
					"Not number format", 
					"Invalid max results", 
					JOptionPane.ERROR_MESSAGE);
			txtMaxResults.setText("" + Config.MAX_RESULTS);
			return false;
		}

		if (maxResults < 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Max results not < 0", 
					"Invalid max results", 
					JOptionPane.ERROR_MESSAGE);
			txtMaxResults.setText("" + Config.MAX_RESULTS);
			return false;
		}

		return true;
	}
	
	
	private void openDataset() {
        File file = FileUtil.chooseDefaultFile(this, true);
        if (file == null) {
			JOptionPane.showMessageDialog(
					this, 
					"File not open", 
					"File not open", 
					JOptionPane.WARNING_MESSAGE);
			return;
        }

		Dataset dataset = Dataset.parse(file);
		if (dataset == null) {
			JOptionPane.showMessageDialog(this, "Dataset invalid", "Dataset invalid", JOptionPane.ERROR_MESSAGE);
			return;
		}

        setDataset(dataset);
        this.txtOpen.setText(file.getAbsolutePath());

		setResultCtrlVisible();
		
	}

	
	private void refresh() {
		String path = this.txtOpen.getText().trim();
		if (path.length() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"File path empty", 
					"File path empty", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		File file = new File(path);
        Dataset dataset = Dataset.parse(file);
        setDataset(dataset);
        
		setResultCtrlVisible();
	}
	
	
	private void grouping() {
		if (resultList.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Not estimate yet", 
					"Not estimate yet", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		groupList.clear();
		groupList.addAll(resultList);
		groupList.regroup(Config.GROUP_MAX_RESULTS, !Config.GROUP_FLEXIBLE);
		
		for (RModelList list1 : groupList) {
			for (RModelList list2 : resultList) {
				list1.addChosenList(list2.getChosenList());
			}
		}
		
		
	}
	
	
	private void viewGroups() {
		if (groupList.size() == 0) {
			
			int ret = JOptionPane.showConfirmDialog(
					this, 
					"Group list empty. Do you want to group ?", 
					"Group list empty", 
					JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				
			if (ret == JOptionPane.OK_OPTION)
				grouping();
		}
		
		if (this.groupList.size() > 0)
			new RModelGroupListDlg(this, 
					this.groupList, 
					this.resultList, 
					this.tblDataset.getDataset());
	}
	
	
	private void saveResults() {
		if (resultList.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"No result to save", 
					"No result to save", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
        File file = FileUtil.chooseDefaultFile(this, false);
        if (file == null) {
			JOptionPane.showMessageDialog(
					this, 
					"File not save", 
					"File not save", 
					JOptionPane.WARNING_MESSAGE);
			return;
        }
        String ext = FileUtil.getExtension(file);
		
		try {
	        if (ext.equals(Constants.PHOEBE_EXT)) {
		        FileWriter writer = new FileWriter(file);
		        for (int i = 0; i < resultList.size(); i++) {
		        	RModelList modelList = resultList.get(i);
		        	String text = modelList.toString();
		        	if (i > 0)
			        	writer.write("\n\n");
		        	
		        	writer.write(text);
		        }
		        
		        if (groupList.size() > 0) {
		        	writer.write("\n\n\n");
		        	writer.write(groupList.toString());
		        }
		        
		        writer.close();
	        }
	        else if(ext.equals("xls")) {
	    		WritableWorkbook workbook = Workbook.createWorkbook(file);
	    		
	    		WritableSheet sheet = workbook.createSheet("Phoebe", 0);
	    		
	    		WritableFont boldFont12 = new WritableFont(WritableFont.TIMES, 12,
	    				WritableFont.BOLD, false);
	    		WritableCellFormat boldCellFormat12 = new WritableCellFormat(boldFont12);

	    		WritableFont font12 = new WritableFont(WritableFont.TIMES, 12,
	    				WritableFont.NO_BOLD, false);
	    		WritableCellFormat cellFormat12 = new WritableCellFormat(font12);

	    		int rowStep = 0;
	    		int n = 0;
	    		for (int i = 0; i < resultList.size(); i++) {
	    			RModelList modelList = resultList.get(i);
	    			n = Math.max(modelList.size(), n);
	    			
	    			int colStep = i * (12 + 2);
	    			
		    		Label lblSpec = new Label(colStep + 0, rowStep, "Specification", boldCellFormat12);
		    		sheet.addCell(lblSpec);
		    		
		    		Label lblType = new Label(colStep + 1, rowStep, "Type", boldCellFormat12);
		    		sheet.addCell(lblType);

		    		Label lblFitness = new Label(colStep + 2, rowStep, "Fitness", boldCellFormat12);
		    		sheet.addCell(lblFitness);
		    		
		    		Label lblR = new Label(colStep + 3, rowStep, "R", boldCellFormat12);
		    		sheet.addCell(lblR);

		    		Label lblSS = new Label(colStep + 4, rowStep, "SS", boldCellFormat12);
		    		sheet.addCell(lblSS);
		    		
		    		Label lblMean = new Label(colStep + 5, rowStep, "Mean", boldCellFormat12);
		    		sheet.addCell(lblMean);

		    		Label lblSd = new Label(colStep + 6, rowStep, "Sd", boldCellFormat12);
		    		sheet.addCell(lblSd);

		    		Label lblErrMean = new Label(colStep + 7, rowStep, "Err mean", boldCellFormat12);
		    		sheet.addCell(lblErrMean);

		    		Label lblErrSd = new Label(colStep + 8, rowStep, "Err sd", boldCellFormat12);
		    		sheet.addCell(lblErrSd);

		    		Label lblRatioErrMean = new Label(colStep + 9, rowStep, "Err mean (%)", boldCellFormat12);
		    		sheet.addCell(lblRatioErrMean);

		    		Label lblRatioErrSd = new Label(colStep + 10, rowStep, "Err sd (%)", boldCellFormat12);
		    		sheet.addCell(lblRatioErrSd);

		    		Label lblPvalue = new Label(colStep + 11, rowStep, "Pvalue", boldCellFormat12);
		    		sheet.addCell(lblPvalue);

		    		for (int j = 0; j < modelList.size(); j++) {
		    			RModel model = modelList.get(j);
		    			
		    			Label spec = null;
		    			boolean chosen = modelList.isChosen(model);
						String specInfo = RModelAssoc.prettySpec(model.getSpec());
		    			
		    			if (chosen)
		    				spec = new Label(
		    						colStep + 0, 
		    						rowStep + 1 + j, 
		    						specInfo, 
		    						boldCellFormat12);
		    			else
		    				spec = new Label(
		    						colStep + 0, 
		    						rowStep + 1 + j, 
		    						specInfo,
		    						cellFormat12);
		    			sheet.addCell(spec);
		    			
	    				Label type = new Label(
	    						colStep + 1, 
	    						rowStep + 1 + j, 
	    						model.getType(),
	    						cellFormat12);
		    			sheet.addCell(type);

	    				Number fitness = new Number(
		    						colStep + 2, 
		    						rowStep + 1 + j, 
		    						MathUtil.round(model.getFitness()), 
		    						cellFormat12);
		    			sheet.addCell(fitness);
		    			
	    				Number r = new Number(
	    						colStep + 3, 
	    						rowStep + 1 + j, 
	    						MathUtil.round(model.getR()), 
	    						cellFormat12);
	    				sheet.addCell(r);

	    				Number ss = new Number(
		    						colStep + 4, 
		    						rowStep + 1 + j, 
		    						MathUtil.round(model.getSumOfSquares()), 
		    						cellFormat12);
		    			sheet.addCell(ss);
		    			
		    			Number mean = new Number(
	    						colStep + 5, 
	    						rowStep + 1 + j, 
	    						MathUtil.round(model.getMean()), 
	    						cellFormat12);
		    			sheet.addCell(mean);

		    			Number sd = new Number(
	    						colStep + 6, 
	    						rowStep + 1 + j, 
	    						MathUtil.round(model.getSd()), 
	    						cellFormat12);
		    			sheet.addCell(sd);

		    			Number errMean = new Number(
	    						colStep + 7, 
	    						rowStep + 1 + j, 
	    						MathUtil.round(model.getErrMean()), 
	    						cellFormat12);
		    			sheet.addCell(errMean);

		    			Number errSd = new Number(
	    						colStep + 8, 
	    						rowStep + 1 + j, 
	    						MathUtil.round(model.getErrSd()), 
	    						cellFormat12);
		    			sheet.addCell(errSd);

		    			Label ratioErrMean = new Label(
	    						colStep + 9, 
	    						rowStep + 1 + j, 
	    						MathUtil.round(model.getRatioErrMean() * 100.0) + "%", 
	    						cellFormat12);
		    			sheet.addCell(ratioErrMean);

		    			Label ratioErrSd = new Label(
	    						colStep + 10, 
	    						rowStep + 1 + j, 
	    						MathUtil.round(model.getRatioErrSd() * 100.0) + "%", 
	    						cellFormat12);
		    			sheet.addCell(ratioErrSd);

		    			Label pvalue = new Label(
	    						colStep + 11, 
	    						rowStep + 1 + j, 
	    						MathUtil.format(model.getPvalue()), 
	    						cellFormat12);
		    			sheet.addCell(pvalue);
		    		}
		    		
	    		}

	    		
	    		rowStep = n + 3;
				Label lblSpec = new Label(1, rowStep, "Specification", boldCellFormat12);
				sheet.addCell(lblSpec);
				Label lblType = new Label(2, rowStep, "Type", boldCellFormat12);
				sheet.addCell(lblType);
				
				Label lblFitness = new Label(3, rowStep, "Fitness", boldCellFormat12);
				sheet.addCell(lblFitness);
				Label lblR = new Label(4, rowStep, "R", boldCellFormat12);
				sheet.addCell(lblR);
				Label lblSS = new Label(5, rowStep, "SS", boldCellFormat12);
				sheet.addCell(lblSS);

				Label lblMean = new Label(6, rowStep, "Mean", boldCellFormat12);
				sheet.addCell(lblMean);
				Label lblSd = new Label(7, rowStep, "Sd", boldCellFormat12);
				sheet.addCell(lblSd);
				
				Label lblErrMean = new Label(8, rowStep, "Err mean", boldCellFormat12);
				sheet.addCell(lblErrMean);
				Label lblErrSd = new Label(9, rowStep, "Err sd", boldCellFormat12);
				sheet.addCell(lblErrSd);
				Label lblPecentErrMean = new Label(10, rowStep, "Err mean (%)", boldCellFormat12);
				sheet.addCell(lblPecentErrMean);
				Label lblPercentErrSd = new Label(11, rowStep, "Err sd (%)", boldCellFormat12);
				sheet.addCell(lblPercentErrSd);
	    		
				Label lblPvalue = new Label(12, rowStep, "Pvalue", boldCellFormat12);
				sheet.addCell(lblPvalue);

				rowStep ++;
	    		for (int i = 0; i < groupList.size(); i++) {
	    			RModelList modelList = groupList.get(i);
	    			
	    			Label no = new Label(0, rowStep, "Group " + (i+1), boldCellFormat12);
		    		sheet.addCell(no);
		    		
		    		for (int j = 0; j < modelList.size(); j++) {
		    			RModel model = modelList.get(j);
						
		    			Label spec = new Label(
		    					1, 
		    					rowStep, 
		    					model.getSpec(), 
		    					cellFormat12);
			    		sheet.addCell(spec);
			    		
		    			Label type = new Label(
		    					2, 
		    					rowStep, 
		    					model.getType(), 
		    					cellFormat12);
			    		sheet.addCell(type);

			    		Number fitness = new Number(
		    					3, 
		    					rowStep, 
		    					MathUtil.round(model.getFitness()), 
		    					cellFormat12);
		    			sheet.addCell(fitness);
		    			
		    			Number r = new Number(
		    					4, 
		    					rowStep, 
		    					MathUtil.round(model.getR()), 
		    					cellFormat12);
		    			sheet.addCell(r);

		    			Number ss = new Number(
		    					5, 
		    					rowStep, 
		    					MathUtil.round(model.getSumOfSquares()), 
		    					cellFormat12);
		    			sheet.addCell(ss);
		
		    			Number mean = new Number(
		    					6, 
		    					rowStep, 
		    					MathUtil.round(model.getMean()), 
		    					cellFormat12);
		    			sheet.addCell(mean);

		    			Number sd = new Number(
		    					7, 
		    					rowStep, 
		    					MathUtil.round(model.getSd()), 
		    					cellFormat12);
		    			sheet.addCell(sd);

		    			Number errMean = new Number(
		    					8, 
		    					rowStep, 
		    					MathUtil.round(model.getErrMean()), 
		    					cellFormat12);
		    			sheet.addCell(errMean);

		    			Number errSd = new Number(
		    					9, 
		    					rowStep, 
		    					MathUtil.round(model.getErrSd()), 
		    					cellFormat12);
		    			sheet.addCell(errSd);

		    			Label ratioErrMean = new Label(
		    					10, 
		    					rowStep, 
		    					MathUtil.round(model.getRatioErrMean() * 100.0) + "%", 
		    					cellFormat12);
		    			sheet.addCell(ratioErrMean);

		    			Label ratioErrSd = new Label(
		    					11, 
		    					rowStep, 
		    					MathUtil.round(model.getRatioErrSd() * 100.0) + "%", 
		    					cellFormat12);
		    			sheet.addCell(ratioErrSd);
		    			
		    			Label pvalue = new Label(
		    					12, 
		    					rowStep, 
		    					MathUtil.format(model.getPvalue()), 
		    					cellFormat12);
		    			sheet.addCell(pvalue);

		    			rowStep ++;
		    		}
		    		
	    			rowStep ++;
	    		}
	    		
	    		workbook.write();
	    		workbook.close();
	        }
	        
			JOptionPane.showMessageDialog(
					this, 
					"File saved successfully", 
					"File saved successfully", 
					JOptionPane.INFORMATION_MESSAGE);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void setDataset(Dataset dataset) {
		clearResult();
		
        tblDataset.update(dataset);
        updateResponseList();
	}

	
	private void clearResult() {
		this.resultList.clear();
		this.groupList.clear();
		
		this.paneResultBody.removeAll();
		this.paneResultBody.validate();
		this.paneResultBody.updateUI();
	}
	
	
	private boolean validateOptionsMore() {
		if(!validateOptions())
			return false;
		
		ArrayList<String> selectMeasureList = tblDataset.getMeasureList(true);
		if (selectMeasureList.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Measure item not chosen", 
					"Measure item not chosen", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		if (lstResponses.getSelectedIndices().length == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Response item not chosen", 
					"Response item not chosen", 
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		
		return true;
	}

	
	private boolean isDatasetEmpty() {
		Dataset dataset = getDataset();
		return (dataset == null || dataset.isEmpty());
	}
	
	
	private void stat() {
		if (isDatasetEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"Dataset empty", 
					"Dataset empty", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		new StatDlg(this, getDataset(), false);
	}
	
	
	private void exit() {
		Dataset dataset = tblDataset.getDatasetTableModel().getDataset();
		if (dataset != null)
			dataset.clear();
		
		if (validateOptions()) {
		
			Config.MAX_RESULTS = Integer.parseInt(txtMaxResults.getText().trim());
			Config.FITNESS = Double.parseDouble(txtFitness.getText().trim());
		
			Config.save();
		}
	}
	
	
	private void moreOptions() {
		new OptionSettingDlg(this);
	}
	
	
	@Override
	public void selectMeasure(SelMeasureEvent evt) {
		updateResponseList();
		
		this.paneResultBody.validate();
		this.paneResultBody.updateUI();
	}

	
	@SuppressWarnings("unchecked")
	private void updateResponseList() {
		Vector<String> vResponse = new Vector<String>();
		
		ArrayList<String> selectedMeasureList = tblDataset.getMeasureList(true);
		if (!selectedMeasureList.isEmpty()) {
			ArrayList<String> unselectedMeasureList = tblDataset.getMeasureList(false);
			vResponse.addAll(unselectedMeasureList);
		}
		
		this.lstResponses.setListData(vResponse);
	}
	
	
	
	
}
