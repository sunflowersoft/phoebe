package vy.phoebe.regression.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.dataset.ui.DatasetDlg;
import vy.phoebe.regression.RModel;
import vy.phoebe.regression.RModelList;
import vy.phoebe.regression.RModelListEx;
import vy.phoebe.util.Constants;
import vy.phoebe.util.MiscUtil;

public class RMCompareDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected Dataset dataset = null;
	
	
	protected RModelList modelList = null;
	
	
	protected JTextField txtBrowse = new JTextField();

	
	protected RMParserTextArea txtRM = null; 
	
	
	protected RModelListPane paneModelList = null;
	
	
	public RMCompareDlg(Component comp, RModelList modelList, Dataset dataset) {
		super(MiscUtil.getFrameForComponent(comp), "Regression model comparision", false);

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		
		this.dataset = dataset;
		RModelList newModelList = new RModelListEx(modelList);
		for (RModel model : modelList)
			newModelList.choose(model);
		this.modelList = newModelList;
		
		this.setLayout(new GridLayout(0, 1));
		
		JPanel upper = new JPanel(new BorderLayout(5, 5));
		this.add(upper);
		
		JPanel upperheader = new JPanel(new BorderLayout(5, 5));
		upper.add(upperheader, BorderLayout.NORTH);
		JButton btnBrowse = new JButton(new AbstractAction("Browse") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				open();
			}
		});
		upperheader.add(btnBrowse, BorderLayout.WEST);
		txtBrowse = new JTextField();
		txtBrowse.setEditable(false);
		upperheader.add(txtBrowse, BorderLayout.CENTER);
		
		JPanel upperbody = new JPanel(new BorderLayout(5, 5));
		upper.add(upperbody, BorderLayout.CENTER);
		this.txtRM = new RMParserTextArea(dataset, txtBrowse);
		this.txtRM.setRows(10);
		this.txtRM.setColumns(10);
		upperbody.add(new JScrollPane(txtRM), BorderLayout.CENTER);
		
		
		JPanel paneDs = new JPanel(new BorderLayout());
		upperbody.add(paneDs, BorderLayout.SOUTH);
		JButton btnDs = new JButton(new AbstractAction("Show dataset") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				showDataset();
			}
		});
		paneDs.add(btnDs, BorderLayout.EAST);

		
		JPanel upperfooter = new JPanel();
		upper.add(upperfooter, BorderLayout.SOUTH);
		JButton btnCompare = new JButton(new AbstractAction("Compare") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				compare();
			}
		});
		upperfooter.add(btnCompare);
		
		
		JPanel main = new JPanel(new BorderLayout(5, 5));
		this.add(main);
		
		JPanel mainbody = new JPanel(new BorderLayout(5, 5));
		main.add(mainbody, BorderLayout.CENTER);
		paneModelList = new RModelListPane(this.modelList, dataset);
		paneModelList.removeControl("zoom");
		paneModelList.removeControl("compare");
		paneModelList.getModelListTable().setBgrChosenSelected(new Color(0, 225, 225));
		paneModelList.getModelListTable().setBgrChosenNormal(new Color(0, 255, 255));
		
		mainbody.add(paneModelList);

		JButton btnClose = MiscUtil.makeIconButton(
			getClass().getResource(Constants.IMAGES_DIR + "close-16x16.png"), 
			"close", "Close", "Close", 
			
			new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					close();
				}
		});
		paneModelList.addControl(btnClose);

		
		setVisible(true);
	}
	
	
	private void close() {
		this.dispose();
	}
	
	
	private void open() {
		this.txtRM.open();
	}
	
	
	private void showDataset() {
		new DatasetDlg(this, this.dataset, false);
	}
	
	
	private void compare() {
		RModelList parseModelList = txtRM.parse();
		RModelList newModelList = this.modelList.compare(parseModelList);
		
		paneModelList.update(newModelList);
	}
	
	
}
