package vy.phoebe.dataset.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.util.Constants;
import vy.phoebe.util.FileUtil;
import vy.phoebe.util.MiscUtil;


public class DatasetDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private JTextField txtOpen = null;

	
	private DatasetTable tblDataset = null;

	
	private boolean confirm = false;
	

	private Dataset dataset = null;
	
	
	public DatasetDlg(Component comp) {
		this(comp, null, true);
	}
	
	
	public DatasetDlg(Component comp, Dataset dataset) {
		this(comp, dataset, true);
	}
	
	
	public DatasetDlg(Component comp, Dataset dataset, boolean enableLoad) {
		super(MiscUtil.getFrameForComponent(comp), "Dataset dialog", true);
		
		
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				onCancel();
				super.windowClosing(e);
			}
			
		});
		
		init(enableLoad);
		
		if (dataset != null) {
			tblDataset.update(dataset);
		}
		
		setSize(800, 600);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		setVisible(true);
		
	}
	
	
	private void init(boolean enableLoad) {
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		this.add(header, BorderLayout.NORTH);
		
		JButton btnOpen = MiscUtil.makeIconButton(
			getClass().getResource(Constants.IMAGES_DIR + "open-16x16.png"), 
			"open", "Open", "Open", 
				
			new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					open();
				}
		});
		header.add(btnOpen, BorderLayout.WEST);
		
		this.txtOpen = new JTextField();
		this.txtOpen.setEditable(false);
		header.add(this.txtOpen, BorderLayout.CENTER);
		
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
		header.add(btnRefresh, BorderLayout.EAST);
		
		
		JPanel body = new JPanel(new BorderLayout());
		this.add(body, BorderLayout.CENTER);
		this.tblDataset = new DatasetTable();
		body.add(new JScrollPane(this.tblDataset), BorderLayout.CENTER);
		
		JPanel info = new JPanel(new BorderLayout());
		body.add(info, BorderLayout.SOUTH);
		
		JButton btnStat = new JButton( new AbstractAction("Stat.") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				stat();
			}
		});
		info.add(btnStat, BorderLayout.EAST);
		

		JPanel footer = new JPanel();
		this.add(footer, BorderLayout.SOUTH);
		
		JButton btnOK = new JButton( new AbstractAction("OK") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		footer.add(btnOK);
		
		JButton btnCancel = new JButton( new AbstractAction("Cancel") {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		footer.add(btnCancel);
	
		this.dataset = null;
		
		if (!enableLoad) {
			btnOpen.setVisible(false);
			txtOpen.setVisible(false);
			btnRefresh.setVisible(false);
			btnCancel.setVisible(false);
		}
		
	}
	
	
	public boolean isConfirm() {
		return confirm;
	}
	
	
	private void openDataset(File file) {
        Dataset dataset = Dataset.parse(file);
        tblDataset.update(dataset);
        this.txtOpen.setText(file.getAbsolutePath());
        this.dataset = null;
	}
	
	
	private void open() {
        File file = FileUtil.chooseDefaultFile(this, true);
        if (file == null) {
			JOptionPane.showMessageDialog(
					this, 
					"File not open", 
					"File not open", 
					JOptionPane.WARNING_MESSAGE);
			return;
        }
        
        openDataset(file);
	}

	
	private void stat() {
		if (tblDataset.getDataset().getRows() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Dataset empty", 
					"Dataset empty", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		new StatDlg(this, tblDataset.getDataset(), this.isModal());
	}
	
	
	public Dataset getDataset() {
		return dataset;
	}
	
	
	public boolean isDatasetEmpty() {
		return (dataset == null || dataset.isEmpty());
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
		openDataset(file);
	}
	
	
	private void onOK() {
		confirm = true;
		dataset = tblDataset.getDataset();
		dispose();
	}

	
	private void onCancel() {
		confirm = false;
		dataset = null;
		dispose();
	}
	
	
}



class DatasetTable extends JTable {

	private static final long serialVersionUID = 1L;

	
    public DatasetTable() {
    	super(new DatasetTableModel());
	}
	
	
	public Dataset getDataset() {
		return ((DatasetTableModel)getModel()).getDataset();
	}
	
	
	public DatasetTableModel getDatasetTableModel() {
		return (DatasetTableModel)getModel();
	}
	
	
	public void update(Dataset dataset) {
		DatasetTableModel model = (DatasetTableModel)getModel();
		model.update(dataset);
	}
	
}


class DatasetTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	
	protected Dataset dataset = null;
	
	
	public DatasetTableModel() {
		super();
	}

	
	public DatasetTableModel(Dataset dataset) {
		update(dataset);
	}

	
	public void update(Dataset dataset) {
		this.dataset = dataset;
		Vector<String> columns = dataset.getMeasureNameVector();
		columns.insertElementAt("No", 0);
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		for (int i = 0; i < dataset.getRows(); i++) {
			Vector<Object> row = dataset.getObjectRowVector(i);
			row.insertElementAt(new Integer(i+1), 0);
			data.add(row);
		}
		
		this.setDataVector(data, columns);
	}
	
	
	public Dataset getDataset() {
		return dataset;
	}


	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}
	
	
	
}
