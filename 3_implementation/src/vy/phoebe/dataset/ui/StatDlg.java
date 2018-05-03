package vy.phoebe.dataset.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import vy.phoebe.Config;
import vy.phoebe.dataset.Dataset;
import vy.phoebe.util.Constants;
import vy.phoebe.util.DSUtil;
import vy.phoebe.util.MiscUtil;
import flanagan.plot.PlotGraph;

public class StatDlg extends JDialog implements ListSelectionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected StatTable tblStat = null;
	
	
	protected JPanel paneHist = null;

	
	public StatDlg(Component comp, Dataset dataset, boolean modal) {
		super(MiscUtil.getFrameForComponent(comp), "Statistics", modal);
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		
		setLayout(new BorderLayout());

		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		tblStat = new StatTable();
		tblStat.update(dataset);
		tblStat.getSelectionModel().addListSelectionListener(this);
		tblStat.setPreferredScrollableViewportSize(new Dimension(200, 80));   
		header.add(new JScrollPane(tblStat), BorderLayout.CENTER);
		
		JPanel paneStat = new JPanel(new BorderLayout());
		header.add(paneStat, BorderLayout.SOUTH);
		
		JButton btnCopy = MiscUtil.makeIconButton(
			getClass().getResource(Constants.IMAGES_DIR + "copy-16x16.png"), 
			"copy", "Copy values to clipboard", "Copy values to clipboard", 
				
			new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (!Config.testClipboard())
						return;

					double[] datarow = tblStat.getSelectedData();
					if (datarow.length == 0) {
						JOptionPane.showMessageDialog(
							getThis(), 
							"Please choose one row", 
							"Choose row", 
							JOptionPane.WARNING_MESSAGE);
						
						return;
					}
					
					StringBuffer buffer = DSUtil.toDataColBuffer(datarow);
					Config.getClipboardProcessor().setText(buffer.toString());
				}
		});
		btnCopy.setMargin(new Insets(0, 0 , 0, 0));
		paneStat.add(btnCopy, BorderLayout.EAST);
		
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		paneHist = new JPanel(new BorderLayout());
		paneHist.setBackground(Color.WHITE);
		body.add(paneHist, BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		//
		JButton btnClose = new JButton(new AbstractAction("Close") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(btnClose);
		
		setVisible(true);
	}


	private StatDlg getThis() {
		return this;
	}
	
	
	@Override
	public void valueChanged(ListSelectionEvent e) {
		selectChanged();
	}
	
	
	private void selectChanged() {
		clearHist();
		
		PlotGraph graph = tblStat.hist2();
		if (graph != null) {
			graph.setBackground(Color.WHITE);
			paneHist.add(graph, BorderLayout.CENTER);
			paneHist.validate();
			paneHist.updateUI();
		}
	}
	
	
	private void clearHist() {
		paneHist.removeAll();
		paneHist.validate();
		paneHist.updateUI();
	}
	
	
}
