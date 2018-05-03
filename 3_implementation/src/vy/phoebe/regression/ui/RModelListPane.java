package vy.phoebe.regression.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;

import javax.swing.AbstractButton;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.WindowConstants;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.RModelList;
import vy.phoebe.util.Constants;
import vy.phoebe.util.FileUtil;
import vy.phoebe.util.MiscUtil;

public class RModelListPane extends JPanel {

	private static final long serialVersionUID = 1L;
	
	
	protected Dataset dataset = null;
	
	
	protected RModelListTable tblModelList = null;
	
	
	protected JLabel lblRsponse = null;

	
	protected JToolBar controlList = null;

	
	protected JButton btnCompare = null; 
		
	
	public RModelListPane(Dataset dataset) {
		super(new BorderLayout());
		this.dataset = dataset;
		
		this.lblRsponse = new JLabel();
		add(lblRsponse, BorderLayout.NORTH);
		
		this.tblModelList = new RModelListTable(dataset);
		add(new JScrollPane(this.tblModelList), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		controlList = new JToolBar("Control");
		controlList.setFloatable(false);
		footer.add(controlList);
		//
		JButton btnZoom = MiscUtil.makeIconButton(
			getClass().getResource(Constants.IMAGES_DIR + "zoomin-16x16.png"), 
			"zoom", "Zoom", "Zoom", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					zoom();
				}
			});
		btnZoom.setMargin(new Insets(0, 0, 0, 0));
		controlList.add(btnZoom);
		//
		JButton btnDetail = MiscUtil.makeIconButton(
			getClass().getResource(Constants.IMAGES_DIR + "copy-16x16.png"), 
			"copy", "Copy", "Copy", 
			new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					copyAllSpecsToClipboard();
				}
		});
		btnDetail.setMargin(new Insets(0, 0, 0, 0));
		controlList.add(btnDetail);
		//
		btnCompare = MiscUtil.makeIconButton(
			getClass().getResource(Constants.IMAGES_DIR + "compare-16x16.gif"), 
			"compare", "Compare", "Compare", 
			new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					showCompareDlg();
				}
		});
		btnCompare.setVisible(dataset != null);
		btnCompare.setMargin(new Insets(0, 0, 0, 0));
		controlList.add(btnCompare);
		
		//
		JButton btnSave = MiscUtil.makeIconButton(
			getClass().getResource(Constants.IMAGES_DIR + "save-16x16.png"), 
			"save", "Save", "Save", 
			new ActionListener() {
			
				@Override
				public void actionPerformed(ActionEvent e) {
					save();
				}
		});
		btnSave.setMargin(new Insets(0, 0, 0, 0));
		controlList.add(btnSave);
	}


	public RModelListPane(RModelList modelList, Dataset dataset) {
		this(dataset);
		update(modelList);
	}
	
	
	public RModelListPane(RModelList modelList) {
		this(modelList, (Dataset)null);
	}

	
	public RModelListTable getModelListTable() {
		return tblModelList;
	}
	
	
	public void update(RModelList modelList) {
		tblModelList.update(modelList);
		this.lblRsponse.setText(modelList.getResponse());
	}
	
	
	public RModelList getModelList() {
		return tblModelList.getModelList();
	}
	
	
	public void addControl(Component control) {
		controlList.add(control);
	}
	
	
	public void removeAllControls() {
		controlList.removeAll();
	}
	
	
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
		this.tblModelList.setDataset(dataset);
		
		btnCompare.setVisible(dataset != null);
	}
	
	
	public void removeControl(String cmd) {
		for (int i = 0; i < controlList.getComponentCount(); i++) {
			Component comp = controlList.getComponentAtIndex(i);
			if (!(comp instanceof AbstractButton))
				continue;
			
			AbstractButton btn = (AbstractButton)comp;
			String action = btn.getActionCommand();
			if (action.toLowerCase().equals(cmd.toLowerCase())) {
				controlList.remove(comp);
				return;
			}
		}
	}
	
	
	private void zoom() {
		RModelList modelList = this.getModelList();
		
		final JDialog dlgZoom = new JDialog(
				MiscUtil.getFrameForComponent(this),
				"Model list for response \"" + modelList.getResponse() + "\"", 
				false);
		
		dlgZoom.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		final RModelListPane paneModelList = new RModelListPane(dataset);
		paneModelList.update(modelList); 
		dlgZoom.add(paneModelList);
		
		paneModelList.removeControl("zoom");
		
		JButton btnClose = MiscUtil.makeIconButton(
			getClass().getResource(Constants.IMAGES_DIR + "close-16x16.png"), 
			"close", "Close", "Close", 
			
			new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					dlgZoom.dispose();
				}
		});
		btnClose.setMargin(new Insets(0, 0, 0, 0));
		paneModelList.addControl(btnClose);
		
		dlgZoom.setSize(800, 600);
		dlgZoom.setVisible(true);
	}
	
	
	private void copyAllSpecsToClipboard() {
		RModelList modelList = tblModelList.getModelList();
		modelList.copyToClipboard(true);
	}
	
	
	private void showCompareDlg() {
		if (dataset == null)
			JOptionPane.showMessageDialog(
					this, 
					"Comparing dialog require dataset", 
					"Comparing dialog require dataset", 
					JOptionPane.ERROR);
		else
			new RMCompareDlg(this, tblModelList.getModelList(), dataset);
		
	}
	
	
	private void save() {
		
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
		        writer.write(getModelList().toString());
		        writer.flush();
		        writer.close();
	        }
	        else if(ext.equals("xls")) {
	        	getModelList().toExcel(file);
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


}




