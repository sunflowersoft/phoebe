package vy.phoebe.regression.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import vy.phoebe.Config;
import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.RModel;
import vy.phoebe.regression.RModelAssoc;
import vy.phoebe.regression.ui.graph.PModelGraphDlg;
import vy.phoebe.regression.ui.graph.RModelGraphFactory;
import vy.phoebe.util.MiscUtil;

public class RModelTextArea extends JTextArea {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	protected RModel model = null;
	
	
	protected Dataset dataset = null;

	
	public RModelTextArea(RModel model) {
		this (model, null);
		
	}
	
	
	public RModelTextArea(RModel model, Dataset dataset) {
		this.model = model;
		this.dataset = dataset;
		
		String prettySpec = RModelAssoc.prettySpec(model.getSpec()); 
		setText(prettySpec);
		setEditable(false);
		setWrapStyleWord(true);
		setLineWrap(true);
		setRows(1);
		setAutoscrolls(true);
		setCaretPosition(0);
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else {
				}
			}
			
		});
		
		setToolTipText("Nice form: " + RModelAssoc.prettySpec(model.getNiceForm()));
	}
	
	
	private RModelTextArea getThis() {
		return this;
	}
	
	
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miDetail = MiscUtil.makeMenuItem(null, "Detail", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						showDetail();
					}
				});
		contextMenu.add(miDetail);

		JMenuItem miNiceForm = MiscUtil.makeMenuItem(null, "Nice form", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					RModelListTable.niceFormDlg(getThis(), model);
					
				}
			});
		contextMenu.add(miNiceForm);

		JMenuItem miCopySpec = MiscUtil.makeMenuItem(null, "Copy", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					copySpecToClipboard();
				}
			});
		contextMenu.add(miCopySpec);

		contextMenu.addSeparator();
		
		JMenuItem miEstiGraph = MiscUtil.makeMenuItem(null, "Estimate graph", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					estiGraph();
				}
			});
		contextMenu.add(miEstiGraph);

		if (model.getRegressorNames().size() == 1) {
			
			JMenuItem miPercentileGraph = MiscUtil.makeMenuItem(null, "Percentile graph", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						percentileGraph();
					}
				});
			contextMenu.add(miPercentileGraph);
		}

		return contextMenu;
	}
	
	
	private void copySpecToClipboard() {
		if (!Config.doesSupportClipboard()) {
			JOptionPane.showMessageDialog(
					this, 
					"Clipboard not supported", 
					"Clipboard not supported", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		String spec = RModelAssoc.prettySpec(model.getSpec());
		Config.getClipboardProcessor().setText(spec);
	}
	
	
	private void estiGraph() {
		RModelGraphFactory.create(this, model, dataset);
	}


	private void percentileGraph() {
		PModelGraphDlg.showDlg(this, dataset, model);
	}

	
	public void showDetail() {
		new RModelDlg(this, model, dataset);
	}
	
	
	
}
