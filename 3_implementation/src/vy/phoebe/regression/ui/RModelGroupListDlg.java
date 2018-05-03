package vy.phoebe.regression.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.RModelGroupList;
import vy.phoebe.regression.RModelList;
import vy.phoebe.util.Constants;
import vy.phoebe.util.FileUtil;
import vy.phoebe.util.MiscUtil;

public class RModelGroupListDlg extends JDialog implements ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected RModelGroupListTable tblGroupList = null;
	
	
	protected RModelListPane paneModelList = null;
	
	
	public RModelGroupListDlg(
			RModelGroupList groupList, 
			RModelGroupList refResultList) {
		this(null, groupList, refResultList);
	}

	
	public RModelGroupListDlg(Component comp, 
			RModelGroupList groupList, 
			RModelGroupList refResultList) {
		this(comp, groupList, refResultList, null);
	}
	
	
	public RModelGroupListDlg(Component comp, 
			RModelGroupList groupList, 
			RModelGroupList refResultList,
			Dataset dataset) {
		super(MiscUtil.getFrameForComponent(comp), "Groups", false);
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		
		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		tblGroupList = new RModelGroupListTable();
		tblGroupList.setRefResultList(refResultList);
		tblGroupList.update(groupList);
		tblGroupList.getSelectionModel().addListSelectionListener(this);
		body.add(new JScrollPane(tblGroupList));
		
		
		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		footer.add(new JLabel("Listing each group"), BorderLayout.NORTH);
		
		paneModelList = new RModelListPane(dataset);
		paneModelList.removeAllControls();
		footer.add(paneModelList, BorderLayout.CENTER);
		paneModelList.getModelListTable().
			setPreferredScrollableViewportSize(new Dimension(200, 200));   
		
		JPanel control = new JPanel();
		footer.add(control, BorderLayout.SOUTH);
		JButton btnSave = MiscUtil.makeIconButton(
				getClass().getResource(Constants.IMAGES_DIR + "save-16x16.png"), 
				"save", "Save", "Save", 
				new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		control.add(btnSave);
		JButton btnClose = MiscUtil.makeIconButton(
				getClass().getResource(Constants.IMAGES_DIR + "close-16x16.png"), 
				"close", "Close", "Close", 
				new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		control.add(btnClose);

		setVisible(true);
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		// TODO Auto-generated method stub
		
		int idx = tblGroupList.getSelectedRow();
		if (idx != -1) {
			Integer i = (Integer)tblGroupList.getModel().getValueAt(idx, 0);
			
			RModelList modelList = tblGroupList.
					getGroupList().get(i.intValue() - 1);
			
			paneModelList.update(modelList);
			
		}
		
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
		        writer.write(tblGroupList.getGroupList().toString());
		        writer.flush();
		        writer.close();
	        }
	        else if(ext.equals("xls")) {
	        	tblGroupList.getGroupList().toExcel(file);
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
