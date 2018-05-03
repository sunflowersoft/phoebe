package vy.phoebe.dataset.ui;

import java.util.ArrayList;

import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import vy.phoebe.dataset.Dataset;

public class SelDatasetTable2 extends SelDatasetTable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
    public SelDatasetTable2() {
    	setModel(new SelDatasetTableModel2());
	}
    
	
    @Override
	public void update(Dataset dataset) {
		SelDatasetTableModel2 model = (SelDatasetTableModel2)getModel();
		model.update(dataset);
		
		uiConfig();
		
		
	}
	
	
	@Override
	public ArrayList<String> getMeasureList(boolean selected) {
		ArrayList<String> measureList = new ArrayList<String>();
		
		SelDatasetTableModel model = getDatasetTableModel();
		if (model.getRowCount() < 2)
			return measureList;
		 
		int n = model.getColumnCount();
		for (int i = 1; i < n; i++) {
			boolean isSlected = ((Boolean)model.getValueAt(0, i)).
				booleanValue();
			
			if (isSlected == selected)
				measureList.add(this.getColumnName(i));
		}
		
		return measureList;
	}


	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		
		int row = e.getFirstRow();
		int column = e.getColumn();
		
		if (row != 0 || column ==0)
			return;
		if (e.getType() != TableModelEvent.UPDATE)
			return;
		
		Boolean selected = (Boolean)getDatasetTableModel().
							getValueAt(row, column);
		
		fireMeasureSelectEvent(
				new SelMeasureEvent(
					this, 
					column,
					getColumnName(column),
					selected.booleanValue()));
	}

	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		SelDatasetTableModel2 model = (SelDatasetTableModel2)getDatasetTableModel();
		TableCellRenderer renderer = getDefaultRenderer(
				model.getColumnClass(row, column));
		if(renderer == null) return super.getCellRenderer(row, column);
		
		return renderer;
	}
	
	
	@Override
    public TableCellEditor getCellEditor(int row, int column) {
		SelDatasetTableModel2 model = (SelDatasetTableModel2)getDatasetTableModel();
    	TableCellEditor editor = getDefaultEditor(model.getColumnClass(row, column));

    	if(editor == null) return super.getCellEditor(row, column);
        return editor;
    }
	
	
}
