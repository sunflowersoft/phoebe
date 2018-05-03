package vy.phoebe.dataset.ui;

import java.util.Vector;

import vy.phoebe.dataset.Dataset;

public class SelDatasetTableModel2 extends SelDatasetTableModel {

	private static final long serialVersionUID = 1L;

	
	public SelDatasetTableModel2() {
		super();
	}

	
	public SelDatasetTableModel2(Dataset dataset) {
		update(dataset);
	}

	
	@Override
	public void update(Dataset dataset) {
		this.dataset = dataset;
		
		Vector<String> columns = dataset.getMeasureNameVector();
		columns.insertElementAt("No", 0);
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		Vector<Object> selectRow = new Vector<Object>();
		selectRow.add("");
		for (int i = 0; i < columns.size(); i++) {
			selectRow.add(new Boolean(false));
		}
		data.add(selectRow);
		
		for (int i = 0; i < dataset.getRows(); i++) {
			Vector<Object> row = dataset.getObjectRowVector(i);
			row.insertElementAt(new Integer(i+1), 0);
			data.add(row);
		}
		
		this.setDataVector(data, columns);
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return ((row == 0) && (column > 0));
	}

	
	public Class<?> getColumnClass(int row, int column) {
		if((row == 0) && (column > 0)) return Boolean.class;
		
		return super.getColumnClass(column);
	}

}
