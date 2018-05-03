package vy.phoebe.dataset.ui;

import java.util.EventObject;

public class SelMeasureEvent extends EventObject {

	private static final long serialVersionUID = 1L;

	
	
	private int column = 0;
	
	
	private String name = null;

	
	private boolean isSelected = false;
	

	public SelMeasureEvent(
			SelDatasetTable table, 
			int column,
			String name,
			boolean isSelected) {
		super(table);
		this.column = column;
		this.name = name;
		this.isSelected = isSelected;
	}

	
	public SelDatasetTable getCheckDatasetTable() {
		return (SelDatasetTable)getSource();
	}

	
	public boolean isChecked() {
		return isSelected;
	}
	
	
	public int getColumn() {
		return column;
	}
	
	
	public String getName() {
		return name;
	}
}
