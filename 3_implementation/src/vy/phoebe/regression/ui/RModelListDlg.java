package vy.phoebe.regression.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.WindowConstants;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.RModelList;
import vy.phoebe.util.Constants;
import vy.phoebe.util.MiscUtil;

public class RModelListDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected RModelList modelList = null;
	
	
	public RModelListDlg(RModelList modelList) {
		this(null, modelList, null);
	}

	
	public RModelListDlg(Component comp, RModelList modelList) {
		this(comp, modelList, null);
	}
	
	
	public RModelListDlg(Component comp, RModelList modelList, Dataset dataset) {
		super(MiscUtil.getFrameForComponent(comp), "Regression model list", false);
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(800, 600);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		
		this.modelList = modelList;

		final RModelListPane paneModelList = new RModelListPane(modelList, dataset);
		this.getContentPane().add(paneModelList);
		
		paneModelList.removeControl("zoom");
		
		JButton btnClose = MiscUtil.makeIconButton(
				getClass().getResource(Constants.IMAGES_DIR + "close-16x16.png"), 
				"close", "Close", "Close", 
				new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				close();
			}
		});
		paneModelList.addControl(btnClose);

		
		this.setVisible(true);
	}
	
	
	private void close() {
		this.dispose();
	}

}
