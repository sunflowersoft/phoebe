package vy.phoebe;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

import vy.phoebe.estimator.Estimator;
import vy.phoebe.math.MathUtil;
import vy.phoebe.util.MiscUtil;

public class OptionSettingDlg extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	private GUI gui = null;
	
	
	@SuppressWarnings({ "rawtypes" })
	private JComboBox cmbEstimators = null;
	
	
	private JTextField txtDecPrecision = null;

	
	private JTextField txtMaxResults = null;
	
	
	private JTextField txtFitness = null;
	
	
	private JCheckBox chkLinear = null;
	
	
	private JCheckBox chkSquare = null;
	
	
	private JCheckBox chkCube = null;
	
	
	private JCheckBox chkLog = null;
	
	
	private JCheckBox chkExp = null;
	
	
	private JCheckBox chkProduct = null;
	
	
	private JCheckBox chkGroupFlexible = null;
	
	
	private JTextField txtGroupMaxResults = null;

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public OptionSettingDlg(GUI gui) {
		super(gui, "Option setting", true);

		this.gui = gui;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		this.add(body, BorderLayout.NORTH);
		
		GridLayout attrGrid = new GridLayout(0, 1);
		attrGrid.setHgap(5);
		attrGrid.setVgap(5);
		JPanel attrs = new JPanel(attrGrid);
		body.add(attrs, BorderLayout.WEST);
		
		attrs.add(new JLabel("Estimators: "));
		attrs.add(new JLabel("Decimal precision: "));
		attrs.add(new JLabel("Max results: "));
		attrs.add(new JLabel("Fitness: "));
		attrs.add(new JLabel("Regression model"));
		attrs.add(new JLabel("Linear model: "));
		attrs.add(new JLabel("Square model: "));
		attrs.add(new JLabel("Cube model: "));
		attrs.add(new JLabel("Log model: "));
		attrs.add(new JLabel("Exponent model: "));
		attrs.add(new JLabel("Product model: "));
		attrs.add(new JLabel("Group"));
		attrs.add(new JLabel("Group flexible: "));
		attrs.add(new JLabel("Group max results: "));
		
		GridLayout optionGrid = new GridLayout(0, 1);
		optionGrid.setHgap(5);
		optionGrid.setVgap(5);
		JPanel options = new JPanel(optionGrid);
		body.add(options, BorderLayout.CENTER);
		
		cmbEstimators = new JComboBox(Config.getEstimatorList().
				toArray(new Estimator[] {}));
		options.add(cmbEstimators);
		
		txtDecPrecision = new JTextField(10);
		options.add(txtDecPrecision);

		txtMaxResults = new JTextField(10);
		options.add(txtMaxResults);
		
		txtFitness = new JTextField(10);
		options.add(txtFitness);
		
		options.add(new JLabel(""));
		
		chkLinear = new JCheckBox("");
		options.add(chkLinear);
		
		chkSquare = new JCheckBox("");
		options.add(chkSquare);
		
		chkCube = new JCheckBox("");
		options.add(chkCube);
		
		chkLog = new JCheckBox("");
		options.add(chkLog);
		
		chkExp = new JCheckBox("");
		options.add(chkExp);

		chkProduct = new JCheckBox("");
		options.add(chkProduct);

		options.add(new JLabel(""));

		chkGroupFlexible = new JCheckBox("");
		options.add(chkGroupFlexible);
		
		txtGroupMaxResults = new JTextField(10);
		options.add(txtGroupMaxResults);
		
		JPanel footer = new JPanel();
		this.add(footer, BorderLayout.SOUTH);
		
		JButton btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(validateOptions())
					saveConfig();
			}
		});
		footer.add(btnApply);
		
		JButton btnOk = new JButton("Ok");
		btnOk.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(validateOptions()) {
					saveConfig();
					close();
				}
			}
			
		});
		footer.add(btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});
		footer.add(btnCancel);
	
		readConfig();
		
		setSize(300, 500);
		setLocationRelativeTo(MiscUtil.getFrameForComponent(gui));
		setVisible(true);
	}

	
	private void readConfig() {
		
		cmbEstimators.setSelectedItem(Config.getCurrentEstimator());
		
		txtMaxResults.setText(Config.MAX_RESULTS + "");
		txtDecPrecision.setText(Config.DECIMAL_PRECISION + "");
		txtFitness.setText(Config.FITNESS + "");
		
		chkLinear.setSelected(Config.LINEAR_MODEL);
		chkSquare.setSelected(Config.SQUARE_MODEL);
		chkCube.setSelected(Config.CUBE_MODEL);
		chkLog.setSelected(Config.LOG_MODEL);
		chkExp.setSelected(Config.EXP_MODEL);
		chkProduct.setSelected(Config.PRODUCT_MODEL);
		
		chkGroupFlexible.setSelected(Config.GROUP_FLEXIBLE);
		txtGroupMaxResults.setText(Config.GROUP_MAX_RESULTS + "");
	}
	
	
	private void saveConfig() {
		Estimator estimator = (Estimator)cmbEstimators.getSelectedItem();
		Config.ESTI_METHOD = estimator.getName(); 
		
		Config.MAX_RESULTS = Integer.parseInt(
				txtMaxResults.getText().trim());
		txtMaxResults.setText("" + Config.MAX_RESULTS);
		
		Config.DECIMAL_PRECISION = Integer.parseInt(
				txtDecPrecision.getText().trim());
		txtDecPrecision.setText("" + Config.DECIMAL_PRECISION);

		Config.FITNESS = Double.parseDouble(
				txtFitness.getText().trim());
		Config.FITNESS = MathUtil.round(Config.FITNESS);
		txtFitness.setText("" + Config.FITNESS);
		
		Config.LINEAR_MODEL = chkLinear.isSelected();
		Config.SQUARE_MODEL = chkSquare.isSelected();
		Config.CUBE_MODEL = chkCube.isSelected();
		Config.LOG_MODEL = chkLog.isSelected();
		Config.EXP_MODEL = chkExp.isSelected();
		Config.PRODUCT_MODEL = chkProduct.isSelected();

		Config.GROUP_FLEXIBLE = chkGroupFlexible.isSelected();
		Config.GROUP_MAX_RESULTS = Integer.parseInt(
				txtGroupMaxResults.getText().trim());
		txtGroupMaxResults.setText("" + Config.GROUP_MAX_RESULTS);
		
		Config.save();
		
		gui.txtMaxResults.setText("" + Config.MAX_RESULTS);
		gui.txtFitness.setText("" + Config.FITNESS);
	}

	
	private void close() {
		dispose();
	}
	
	
	private boolean validateOptions() {
		int decpre = 0;
		String decpreText = txtDecPrecision.getText().trim();
		if (decpreText.length() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Decimal precision empty", 
					"Invalid decimal precision", 
					JOptionPane.ERROR_MESSAGE);
			txtDecPrecision.setText("" + Config.DECIMAL_PRECISION);
			return false;
		}
		
		try {
			decpre = Integer.parseInt(decpreText);
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(
					this, 
					"Not number format", 
					"Invalid decimal precision", 
					JOptionPane.ERROR_MESSAGE);
			txtDecPrecision.setText("" + Config.DECIMAL_PRECISION);
			return false;
		}

		if (decpre < 2) {
			JOptionPane.showMessageDialog(
					this, 
					"Decimal precision not < 2", 
					"Invalid decimal precision", 
					JOptionPane.ERROR_MESSAGE);
			txtDecPrecision.setText("" + Config.DECIMAL_PRECISION);
			return false;
		}
		
		double fitness = 0;
		String fitnessText = txtFitness.getText().trim();
		if (fitnessText.length() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Fitness empty", 
					"Invalid fitness", 
					JOptionPane.ERROR_MESSAGE);
			txtFitness.setText("" + Config.FITNESS);
			return false;
		}
		
		try {
			fitness = Double.parseDouble(fitnessText);
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(
					this, 
					"Not number format", 
					"Invalid fitness", 
					JOptionPane.ERROR_MESSAGE);
			txtFitness.setText("" + Config.FITNESS);
			return false;
		}
		
		if (fitness > 1.0) {
			JOptionPane.showMessageDialog(
					this, 
					"Fitness not > 1.0", 
					"Invalid fitness", 
					JOptionPane.ERROR_MESSAGE);
			txtFitness.setText("" + Config.FITNESS);
			return false;
		}
		
		if (fitness < 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Fitness not < 0", 
					"Invalid fitness", 
					JOptionPane.ERROR_MESSAGE);
			txtFitness.setText("" + Config.FITNESS);
			return false;
		}
		
		int maxResult = 0;
		String maxResultText = txtMaxResults.getText().trim();
		if (maxResultText.length() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Max results empty", 
					"Invalid max result", 
					JOptionPane.ERROR_MESSAGE);
			txtMaxResults.setText("" + Config.MAX_RESULTS);
			return false;
		}
		
		try {
			maxResult = Integer.parseInt(maxResultText);
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(
					this, 
					"Not number format", 
					"Invalid max results", 
					JOptionPane.ERROR_MESSAGE);
			txtMaxResults.setText("" + Config.MAX_RESULTS);
			return false;
		}

		if (maxResult < 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Max results not < 0", 
					"Invalid max results", 
					JOptionPane.ERROR_MESSAGE);
			txtMaxResults.setText("" + Config.MAX_RESULTS);
			return false;
		}


		int groupMaxResults = 0;
		String groupMaxResultsText = txtGroupMaxResults.getText().trim();
		if (groupMaxResultsText.length() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Group max results empty", 
					"Invalid group max result", 
					JOptionPane.ERROR_MESSAGE);
			txtGroupMaxResults.setText("" + Config.GROUP_MAX_RESULTS);
			return false;
		}
		
		try {
			groupMaxResults = Integer.parseInt(groupMaxResultsText);
		}
		catch (NumberFormatException e) {
			JOptionPane.showMessageDialog(
					this, 
					"Not number format", 
					"Invalid group max results", 
					JOptionPane.ERROR_MESSAGE);
			txtGroupMaxResults.setText("" + Config.GROUP_MAX_RESULTS);
			return false;
		}

		if (groupMaxResults < 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Max group results not < 0", 
					"Invalid max results", 
					JOptionPane.ERROR_MESSAGE);
			txtGroupMaxResults.setText("" + Config.MAX_RESULTS);
			return false;
		}

		return true;
	}
	
	
}
