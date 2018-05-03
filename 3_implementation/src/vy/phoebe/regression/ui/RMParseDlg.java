package vy.phoebe.regression.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.dataset.ui.DatasetDlg;
import vy.phoebe.regression.RModelList;
import vy.phoebe.util.Constants;
import vy.phoebe.util.MiscUtil;

public class RMParseDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected Dataset dataset = null;
	
	
	protected RMParserTextArea txtRM = null; 
	
	
	protected RModelListPane paneModelList = null;
	
	
	public RMParseDlg(Component comp) {
		this(comp, null);
	}

	
	public RMParseDlg(Component comp, Dataset dataset) {
		super(MiscUtil.getFrameForComponent(comp), "Parsing model text", false);
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		
		this.dataset = dataset;
		
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
		JTextField txtBrowse = new JTextField();
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
		JButton btnDs = new JButton(new AbstractAction("Dataset") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				onDataset();
			}
		});
		paneDs.add(btnDs, BorderLayout.EAST);
		
		
		JPanel upperfooter = new JPanel();
		upper.add(upperfooter, BorderLayout.SOUTH);
		JButton btnCompare = new JButton(new AbstractAction("Parse") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				parse();
			}
		});
		upperfooter.add(btnCompare);
		
		
		JPanel main = new JPanel(new BorderLayout(5, 5));
		this.add(main);
		
		JPanel mainbody = new JPanel(new BorderLayout(5, 5));
		main.add(mainbody, BorderLayout.CENTER);
		paneModelList = new RModelListPane(dataset);
		paneModelList.removeControl("zoom");
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
	
	
	private void parse() {
		if (dataset == null) {
			int ret = JOptionPane.showConfirmDialog(this, 
				"No dataset. Do you want to load dataset?", 
				"Load dataset", 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.OK_CANCEL_OPTION);
			
			if (ret == JOptionPane.OK_OPTION) {
				onDataset();
			}
		}
		
		RModelList parseModelList = txtRM.parse();
		
		paneModelList.update(parseModelList);
	}
	
	
	private void onDataset() {
		DatasetDlg dlgDs = new DatasetDlg(this, this.dataset, true);
		
		if (!dlgDs.isConfirm() || dlgDs.isDatasetEmpty()) {
			JOptionPane.showMessageDialog(this, 
					"Not load dataset", 
					"Not load dataset", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		this.dataset = dlgDs.getDataset();
		setDataset(dataset);
	}
	
	
	private void setDataset(Dataset dataset) {
		paneModelList.setDataset(dataset);
		txtRM.setDataset(dataset);
	}
	
	
}
