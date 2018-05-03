package vy.phoebe.regression.ui.graph;

import java.awt.Component;

import javax.swing.JOptionPane;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.RModel;

public class RModelGraphFactory {
	
	
	public static void create(Component comp, RModel f, Dataset dataset) {
		if (f == null || dataset == null) {
			JOptionPane.showMessageDialog(
					comp, 
					"Model or dataset invalid", 
					"Model or dataset invalid", 
					JOptionPane.ERROR_MESSAGE);
			
			return;
		}

		int regressorCount = f.getRegressorNames().size();
		
		if (regressorCount == 1)
			RModelGraph2dDlg.showDlg(comp, f, dataset);
		else if (regressorCount == 2)
			RModelGraph3dDlg.showDlg(comp, f, dataset);
		else if (regressorCount == 3)
			RModelGraph4dDlg.showDlg(comp, f, dataset);
		else {
			JOptionPane.showMessageDialog(
					comp, 
					"Not support more than 3 regressors", 
					"Not support more than 3 regressors", 
					JOptionPane.INFORMATION_MESSAGE);
		}
	}
	
	
}
