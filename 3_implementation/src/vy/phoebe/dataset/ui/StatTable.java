package vy.phoebe.dataset.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.math.FlanaganStat;
import vy.phoebe.util.MiscUtil;
import vy.phoebe.util.SortableTable;
import flanagan.analysis.Stat;
import flanagan.math.Fmath;
import flanagan.plot.PlotGraph;

public class StatTable extends SortableTable implements MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public static final double BIN_NUMBER = 50;

	
    public StatTable() {
    	super(new StatTableModel());
    	addMouseListener(this);
	}
	
	
	public Dataset getDataset() {
		return ((StatTableModel)getModel()).getDataset();
	}


	public void update(Dataset dataset) {
		StatTableModel model = (StatTableModel)getModel();
		model.update(dataset);
		
		if (getColumnCount() > 0) {
			String columnName = this.getColumnName(0);
			getColumn(columnName).setPreferredWidth(10);
		}
		
		init();
	}

	
	public double[] getSelectedData() {
		int selectedIdx = getSelectedRow();
		
		if (selectedIdx == -1)
			return new double[0];
		
		Integer idx = (Integer)getModel().getValueAt(selectedIdx, 0);
		double[] data = getDataset().getMeasureArray(idx.intValue() - 1);
		
		return data;
	}
	
	
	public void hist() {
		int selectedIdx = getSelectedRow();
		
		if (selectedIdx == -1) {
			return;
		}
		
		Integer idx = (Integer)getModel().getValueAt(selectedIdx, 0);
		double[] data = getDataset().getMeasureArray(idx.intValue() - 1);
		double bintWidth = (Fmath.maximum(data) - Fmath.minimum(data)) / BIN_NUMBER;
		Stat.histogramBinsPlot(data, bintWidth);
	}
	
	
	public PlotGraph hist2() {
		int selectedIdx = getSelectedRow();
		
		if (selectedIdx == -1) {
			return null;
		}
		
		Integer idx = (Integer)getModel().getValueAt(selectedIdx, 0);
		double[] data = getDataset().getMeasureArray(idx.intValue() - 1);
		double bintWidth = (Fmath.maximum(data) - Fmath.minimum(data)) / BIN_NUMBER;
		return FlanaganStat.histogramBinsPlot2(data, bintWidth);
	}

	
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miHist = MiscUtil.makeMenuItem(null, "Hist", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					hist();
				}
			});
		contextMenu.add(miHist);
		
		return contextMenu;
	}

	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(SwingUtilities.isRightMouseButton(e) ) {
			JPopupMenu contextMenu = createContextMenu();
			if(contextMenu != null) 
				contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
		}
		else if (e.getClickCount() >= 2){
			hist();
		}
		
	}


	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}


}
