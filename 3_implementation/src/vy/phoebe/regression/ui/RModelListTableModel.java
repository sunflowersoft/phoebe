package vy.phoebe.regression.ui;

import java.util.Vector;

import vy.phoebe.math.MathUtil;
import vy.phoebe.regression.RModel;
import vy.phoebe.regression.RModelAssoc;
import vy.phoebe.regression.RModelList;
import vy.phoebe.util.SortableTableModel;

public class RModelListTableModel extends SortableTableModel {

	private static final long serialVersionUID = 1L;

	
	protected RModelList modelList = null;
	
	
	public RModelListTableModel() {
		super();
	}

	
	public RModelListTableModel(RModelList modelList) {
		update(modelList);
	}

	
	public RModelList getModelList() {
		return modelList;
	}
	
	
	public void update(RModelList modelList) {
		this.modelList = modelList;
		
		Vector<String> columns = new Vector<String>();
		columns.add("No");
		columns.add("Regression model");
		columns.add("Type");
		columns.add("R");
		columns.add("Error mean (%)");
		columns.add("Error sd (%)");
		columns.add("Count");

		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		for (int i = 0; i < modelList.size(); i++) {
			RModel model = modelList.get(i);
			Vector<Object> row = new Vector<Object>();
			
			row.add(new Integer(i + 1));
			String prettySpec = RModelAssoc.prettySpec(model.getSpec()); 
			row.add(prettySpec);
			row.add(model.getType());
			
			double r = model.getR();
			row.add(MathUtil.round(r));

			double errRatioMean = model.getRatioErrMean();
			row.add(MathUtil.round(errRatioMean * 100.0));

			double errRatioSd = model.getRatioErrSd();
			row.add(MathUtil.round(errRatioSd * 100.0));

			row.add(model.getRegressorNames().size());
			
			data.add(row);
		}
		this.setDataVector(data, columns);
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}

	
    public boolean isSortable(final int column) {
        return true;
    }
	
}
