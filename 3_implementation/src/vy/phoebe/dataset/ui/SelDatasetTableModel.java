package vy.phoebe.dataset.ui;

import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import vy.phoebe.dataset.Dataset;

public class SelDatasetTableModel extends DefaultTableModel {

	private static final long serialVersionUID = 1L;

	
	protected Dataset dataset = null;
	
	
	public SelDatasetTableModel() {
		super();
	}

	
	public SelDatasetTableModel(Dataset dataset) {
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
