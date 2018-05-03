package vy.phoebe.regression.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.RModelList;
import vy.phoebe.util.Constants;
import vy.phoebe.util.MiscUtil;

public class RModelListFrame extends JFrame {

	private static final long serialVersionUID = 1L;


	protected RModelList modelList = null;
	
	
	public RModelListFrame(RModelList modelList) {
		this(modelList, null);
	}
	
	
	public RModelListFrame(RModelList modelList, Dataset dataset) {
		super("Regression model list");
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		this.setSize(800, 600);
		setLocationRelativeTo(null);
		
		MiscUtil.TangChiNeThuongChiThatNhieu(this);

		this.modelList = modelList;

		final RModelListPane paneModelList = new RModelListPane(modelList, dataset);
		paneModelList.update(modelList); 
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
