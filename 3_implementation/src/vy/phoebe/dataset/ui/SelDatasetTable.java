package vy.phoebe.dataset.ui;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JTable;
import javax.swing.event.EventListenerList;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import vy.phoebe.dataset.Dataset;


public class SelDatasetTable extends JTable {

	private static final long serialVersionUID = 1L;

	
    protected EventListenerList listenerList = new EventListenerList();

    
    public SelDatasetTable() {
    	super(new SelDatasetTableModel());
	}
	
	
	public Dataset getDataset() {
		return ((SelDatasetTableModel)getModel()).getDataset();
	}
	
	
	public SelDatasetTableModel getDatasetTableModel() {
		return (SelDatasetTableModel)getModel();
	}
	
	
	protected void uiConfig() {
		if (getColumnCount() > 0) {
			String columnName = this.getColumnName(0);
			getColumn(columnName).setPreferredWidth(10);
		}
	}
	
	
	public void update(Dataset dataset) {
		SelDatasetTableModel model = (SelDatasetTableModel)getModel();
		model.update(dataset);
		
		TableColumnModel tcm = getColumnModel();
		for (int i = 1; i < tcm.getColumnCount(); i++) {
		    TableColumn tc = getColumnModel().getColumn(i);
		    tc.setHeaderRenderer(new SelMeasureHeaderRenderer(new HeaderItemListener(this)));
		}
		
		uiConfig();
		
		
	}
	
	
	public ArrayList<String> getMeasureList(boolean selected) {
		ArrayList<String> measureList = new ArrayList<String>();
		
		TableColumnModel tcm = getColumnModel();
		for (int i = 0; i < tcm.getColumnCount(); i++) {
		    TableColumn tc = getColumnModel().getColumn(i);
		    if (!(tc.getHeaderRenderer() instanceof SelMeasureHeaderRenderer))
		    	continue;
		    
		    SelMeasureHeaderRenderer header = (SelMeasureHeaderRenderer)tc.getHeaderRenderer();
		    if (header.isSelected() == selected)
		    	measureList.add(header.getText());
		}
		
		return measureList;
	}

	
	private class HeaderItemListener implements ItemListener {
		
		protected SelDatasetTable table = null;
		
		public HeaderItemListener(SelDatasetTable table) {
			this.table = table;
		}
		
		public void itemStateChanged(ItemEvent e) {   
			Object source = e.getSource();   
			if (!(source instanceof SelMeasureHeaderRenderer))
				return;   
	      
			SelMeasureHeaderRenderer header = (SelMeasureHeaderRenderer)source;
			fireMeasureSelectEvent(
					new SelMeasureEvent(
						table, 
						header.getColumn(),
						header.getText(),
						header.isSelected()));
		}   
	}   

	
	public void addMeasureSelectListener(SelMeasureListener listener) {
        listenerList.add(SelMeasureListener.class, listener);
    }

    
    public void removeMeasureSelectListener(SelMeasureListener listener) {
        listenerList.remove(SelMeasureListener.class, listener);
    }
	
    
    public SelMeasureListener[] getMeasureSelectListeners() {
        return listenerList.getListeners(SelMeasureListener.class);
    }


	protected void fireMeasureSelectEvent(SelMeasureEvent evt) {
		SelMeasureListener[] listeners = getMeasureSelectListeners();
		for (SelMeasureListener listener : listeners) {
			listener.selectMeasure(evt);
		}
	}

}
