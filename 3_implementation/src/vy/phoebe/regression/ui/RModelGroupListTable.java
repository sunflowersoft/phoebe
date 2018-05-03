package vy.phoebe.regression.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import vy.phoebe.regression.RModelGroupList;
import vy.phoebe.util.SortableTable;

public class RModelGroupListTable extends SortableTable implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


    public RModelGroupListTable() {
    	super(new RModelGroupListTableModel());
    	addMouseListener(this);
	}
	
	
	public RModelGroupList getGroupList() {
		return ((RModelGroupListTableModel)getModel()).getGroupList();
	}
	
	
	public void setRefResultList(RModelGroupList refResultList) {
		((RModelGroupListTableModel)getModel()).setRefResultList(refResultList);
	}
	
	
	public void update(RModelGroupList groupList) {
		RModelGroupListTableModel model = (RModelGroupListTableModel)getModel();
		model.update(groupList);
		
		if (model.getColumnCount() > 0) {
			String firstColumn = model.getColumnName(0);
	    	this.getColumn(firstColumn).setPreferredWidth(10);
		}
		
		if (model.getColumnCount() > 3) {
			for (int i = 3; i < model.getColumnCount(); i++) {
				String column = model.getColumnName(i);
		    	this.getColumn(column).setPreferredWidth(10);
			}
		}
		
		init();
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
}
