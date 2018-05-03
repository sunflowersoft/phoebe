package vy.phoebe.regression.ui;

import java.util.Vector;

import vy.phoebe.math.MathUtil;
import vy.phoebe.regression.RModelAssoc;
import vy.phoebe.regression.RModelGroupList;
import vy.phoebe.regression.RModelList;
import vy.phoebe.util.SortableTableModel;

public class RModelGroupListTableModel extends SortableTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected RModelGroupList groupList = null;
	
	
	protected RModelGroupList refResultList = null;
	
	
	public RModelGroupListTableModel() {
		super();
	}

	
	public RModelGroupList getGroupList() {
		return groupList;
	}
	
	
	public void setRefResultList(RModelGroupList refResultList) {
		this.refResultList = refResultList;
	}
	
	
	public void update(RModelGroupList groupList) {
		this.groupList = groupList;
		
		if (groupList.size() == 0) {
			setDataVector(new Object[0][], new Object[0]);
			return;
		}
		
		Vector<String> columns = new Vector<String>();
		columns.add("Group");
		
		if (refResultList == null) {
			RModelList model =  groupList.get(0);
			for (int j = 0; j < model.size(); j++) {
				columns.add("");
			}
		}
		else {
			for (RModelList modelList : refResultList) {
				columns.add(modelList.getResponse());
			}
		}
		
		columns.add("\"Avg. R\"");
		columns.add("\"Avg. Error mean (%)\"");
		columns.add("\"Avg. Error sd (%)\"");
		columns.add("\"Common\"");
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		for (int i = 0; i < groupList.size(); i++) {
			RModelList modelList = groupList.get(i);
			
			Vector<Object> row = new Vector<Object>();
			row.add(new Integer(i + 1));
			
			for (int j = 0; j < modelList.size(); j++) {
				String prettySpec = RModelAssoc.prettySpec(modelList.get(j).getSpec()); 
				row.add(prettySpec);
			}
			
			row.add(MathUtil.round(modelList.getAvgR()));
			row.add(MathUtil.round(modelList.getAvgErrMean()));
			row.add(MathUtil.round(modelList.getAvgErrSd()));
			row.add(MathUtil.round(modelList.getCommonRegressors().size()));
			
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
