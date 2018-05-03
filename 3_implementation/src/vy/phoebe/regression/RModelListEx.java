package vy.phoebe.regression;

import java.util.ArrayList;
import java.util.Collection;

public class RModelListEx extends RModelList {

	protected ArrayList<RModel> chosenList = new ArrayList<RModel>();
	

	public RModelListEx() {
		super();
    	
    }

	public RModelListEx(Collection<? extends RModel> c) {
    	super(c);
    }

	
	public RModelListEx(RModelList modelList) {
		super(modelList);
    	this.setChosenList(modelList.getChosenList());
    }

	
	public void clearChosenList() {
		chosenList.clear();
	}
	
	
	public boolean isChosen(RModel model) {
		return chosenList.contains(model);
	}

	
	public void removeChosen(RModel model) {
		if (chosenList.contains(model))
			chosenList.remove(model);
	}
	
	
	public void choose(RModel model) {
		if (!isChosen(model))
			chosenList.add(model);
	}
	
	public ArrayList<RModel> getChosenList() {
		return new ArrayList<RModel>(chosenList);
	}
	
	
	public void setChosenList(ArrayList<RModel> chosenList) {
		this.chosenList.clear();
		for (RModel chosen : chosenList) {
			if (this.contains(chosen))
				this.chosenList.add(chosen);
		}
	}
	
	
	public void addChosenList(ArrayList<RModel> chosenList) {
		for (RModel chosen : chosenList) {
			if (this.contains(chosen) && !this.isChosen(chosen))
				this.chosenList.add(chosen);
		}
	}

}
