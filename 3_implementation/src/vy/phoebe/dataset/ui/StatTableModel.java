package vy.phoebe.dataset.ui;

import java.util.ArrayList;
import java.util.Vector;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.math.MathUtil;
import vy.phoebe.util.SortableTableModel;
import flanagan.analysis.Stat;

public class StatTableModel extends SortableTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
    protected Dataset dataset = null;
	
	
	public StatTableModel() {
		super();
	}

	
	public Dataset getDataset() {
		return this.dataset;
	}
	
	
	public void update(Dataset dataset) {
		this.dataset = dataset;
		
		Vector<String> columns = createColumns();
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		ArrayList<String> measureList = dataset.getMeasureNameList();
		for (int i = 0; i < measureList.size(); i++) {
			String measure = measureList.get(i);
			Vector<Object> row = createRow(measure);
			row.insertElementAt(new Integer(i + 1), 0);
			data.add(row);
		}
		
		this.setDataVector(data, columns);
	}
	
	
	private Vector<Object> createRow(String measure) {
		Vector<Object> row = new Vector<Object>();
		
		double[] data = dataset.getMeasureArrayByName(measure);
		Stat stat = new Stat(data);
		
		row.add(measure);
		row.add(MathUtil.round(stat.minimum()));
		row.add(MathUtil.round(stat.maximum()));
		row.add(MathUtil.round(stat.mean()));
		row.add(MathUtil.round(stat.median()));
		row.add(MathUtil.round(stat.standardDeviation()));
		row.add(MathUtil.round(stat.standardError()));
		row.add(MathUtil.round(stat.variance()));
		row.add(MathUtil.round(stat.momentSkewness()));
		row.add(MathUtil.round(stat.kurtosis()));
		
		return row;
	}

	
	private Vector<String> createColumns() {
		Vector<String> columns = new Vector<String>();
		
		columns.add("No");
		columns.add("Name");
		columns.add("Min");
		columns.add("Max");
		columns.add("Mean");
		columns.add("Median");
		columns.add("Sd");
		columns.add("Se");
		columns.add("Variance");
		columns.add("Skewness");
		columns.add("Kurtosis");
		
		return columns;
	}


	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}


    public boolean isSortable(final int column) {
        return true;
    }


}
