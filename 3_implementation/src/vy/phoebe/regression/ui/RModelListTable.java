package vy.phoebe.regression.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;

import vy.phoebe.Config;
import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.DatasetRModel;
import vy.phoebe.regression.RModel;
import vy.phoebe.regression.RModelAssoc;
import vy.phoebe.regression.RModelList;
import vy.phoebe.regression.RModelListEx;
import vy.phoebe.regression.ui.graph.PModelGraphDlg;
import vy.phoebe.regression.ui.graph.RMCompareMatrix;
import vy.phoebe.regression.ui.graph.RModelGraphFactory;
import vy.phoebe.util.MiscUtil;
import vy.phoebe.util.SortableTable;

public class RModelListTable extends SortableTable implements MouseListener {

	private static final long serialVersionUID = 1L;

	
	private DefaultTableCellRenderer chosenRenderer = new DefaultTableCellRenderer();
	

    protected Dataset dataset = null;
    
    
	private Color bgrChosenSelected = new Color(225, 225, 0);
	
	
	private Color bgrChosenNormal = new Color(255, 255, 0);;
	
	
    public RModelListTable() {
    	this(null);
    }
    
    
    public RModelListTable(Dataset dataset) {
    	this(null, dataset);
	}
	
	
	public RModelListTable(RModelListEx modelList, Dataset dataset) {
    	super(new RModelListTableModel());
    	this.dataset = dataset;
		update(modelList);
		
    	setDefaultRenderer(Object.class, new ChosenRenderer());
    	addMouseListener(this);
	}
	
	
	private RModelListTable getThis() {
		return this;
	}
	
	
	public RModelList getModelList() {
		return ((RModelListTableModel)getModel()).getModelList();
	}
	
	
	public void update(RModelList modelList) {
		if (modelList == null)
			return;
		
		RModelListTableModel model = (RModelListTableModel)getModel();
		model.update(modelList);
		
		if (model.getColumnCount() > 0) {
			String firstColumn = model.getColumnName(0);
	    	this.getColumn(firstColumn).setPreferredWidth(10);
		}
		
		init();
	}
	
	
	public void setBgrChosenSelected(Color color) {
		this.bgrChosenSelected = color;
	}
	
	
	public void setBgrChosenNormal(Color color) {
		this.bgrChosenNormal = color;
	}

	
	private class ChosenRenderer extends DefaultTableCellRenderer {  

		private static final long serialVersionUID = 1L;

		public Component getTableCellRendererComponent(JTable table,  
	                                                   Object value,  
	                                                   boolean isSelected,  
	                                                   boolean hasFocus,  
	                                                   int row, int column) {  
	        super.getTableCellRendererComponent(table, value, isSelected,  
	                                            hasFocus, row, column);
	        
	        RModelList modelList = ((RModelListTable)table).getModelList();
			RModel model = ((RModelListTable)table).
				getRModel(row); 
	        
			if (modelList.isChosen(model)) {
		        chosenRenderer.getTableCellRendererComponent(table, value, isSelected,  
                        hasFocus, row, column);
		        
				if (isSelected)
					chosenRenderer.setBackground(bgrChosenSelected);
				else
					chosenRenderer.setBackground(bgrChosenNormal);
				
				return chosenRenderer;
			}
	        return this;  
	    }  
	}


	public RModel getRModel(int row) {
		RModelListTableModel tlbm = (RModelListTableModel)getModel();
		Integer i = (Integer)tlbm.getValueAt(row, 0);
		RModel model = getModelList().get(i.intValue() - 1);
		
		return model;
	}
	
	
	public RModel getSelectedModel() {
		int row = getSelectedRow();
		if (row == -1)
			return null;
			
		return getRModel(row);
	}
	
	
	public RModel[] getSelectedModels() {
		int[] rows = getSelectedRows();
		if (rows.length == 0)
			return new RModel[] { };
		
		RModel[] models = new RModel[rows.length];
		for (int i = 0; i < rows.length; i++) {
			int row = rows[i];
			models[i] = getRModel(row);
		}
		
		return models;
	}

	
	public void showDetail() {
		RModel model = getSelectedModel();
		
		if (model != null) {
			new RModelDlg(this, model, dataset);
		}
		else {
			JOptionPane.showMessageDialog(
					this, 
					"Please choose one model", 
					"Model not chosen", 
					JOptionPane.WARNING_MESSAGE);
			
		}
	}
	
	
	public void setDataset(Dataset dataset) {
		if (dataset == null || this.dataset == dataset)
			return;
		
		this.dataset = dataset;
		RModelList modelList = new RModelListEx(getModelList());
		
		for (RModel model : modelList) {
			if (model instanceof DatasetRModel) {
				((DatasetRModel)model).setDataset(dataset);
			}
		}
		
		update(modelList);
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
		
		RModel model = getSelectedModel();
		if (model == null)
			return;
		
		String spec = RModelAssoc.prettySpec(model.getSpec());
		Config.getClipboardProcessor().setText(spec);
	}
	
	
	private void estiGraph() {
		RModel model = getSelectedModel();
		if (model == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Regression model not selected", 
					"Regression model not selected", 
					JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		RModelGraphFactory.create(this, model, dataset);

	}
	
	
	private void percentileGraph() {
		RModel model = getSelectedModel();
		if (model == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Regression model not selected", 
					"Regression model not selected", 
					JOptionPane.ERROR_MESSAGE);
			
			return;
		}
		
		PModelGraphDlg.showDlg(this, dataset, model);

	}

	
	private JPopupMenu createContextMenu() {
		final int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return null;
		
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
					RModel model = getSelectedModel();
					if (model == null)
						return;
					
					niceFormDlg(getThis(), model);
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
		
		RModel model = getSelectedModel();
		if (model.getRegressorNames().size() == 1) {
			
			JMenuItem miPercentileGraph = MiscUtil.makeMenuItem(null, "Percentile graph", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						percentileGraph();
					}
				});
			contextMenu.add(miPercentileGraph);
		}
		
		final RModel[] models = getSelectedModels();
		if (models.length > 1) {
			JMenuItem miCompareMatrix = MiscUtil.makeMenuItem(null, "Compare matrix graph", 
				new ActionListener() {
					
					public void actionPerformed(ActionEvent e) {
						RMCompareMatrix.showDlg(getThis(), models);
					}
				});
			contextMenu.add(miCompareMatrix);
		}
		
		if (getRowCount() > 1) {
			contextMenu.addSeparator();
			
			if (selectedRow == 0) {
				JMenuItem miMoveDown = MiscUtil.makeMenuItem(null, "Move down", 
					new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							moveRowDown(selectedRow);
						}
					});
				contextMenu.add(miMoveDown);
				
				JMenuItem miMoveLast = MiscUtil.makeMenuItem(null, "Move last", 
					new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							moveRowLast(selectedRow);
						}
					});
				contextMenu.add(miMoveLast);
			}
			else if (selectedRow == getRowCount() - 1) {
				JMenuItem miMoveFirst = MiscUtil.makeMenuItem(null, "Move first", 
					new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							moveRowFirst(selectedRow);
						}
					});
				contextMenu.add(miMoveFirst);
				
				JMenuItem miMoveUp = MiscUtil.makeMenuItem(null, "Move up", 
					new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							moveRowUp(selectedRow);
						}
					});
				contextMenu.add(miMoveUp);
			}
			else {
				JMenuItem miMoveFirst = MiscUtil.makeMenuItem(null, "Move first", 
					new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							moveRowFirst(selectedRow);
						}
					});
				contextMenu.add(miMoveFirst);
					
				JMenuItem miMoveUp = MiscUtil.makeMenuItem(null, "Move up", 
					new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							moveRowUp(selectedRow);
						}
					});
				contextMenu.add(miMoveUp);
				
				JMenuItem miMoveDown = MiscUtil.makeMenuItem(null, "Move down", 
					new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							moveRowDown(selectedRow);
						}
					});
				contextMenu.add(miMoveDown);
				
				JMenuItem miMoveLast = MiscUtil.makeMenuItem(null, "Move last", 
					new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							moveRowLast(selectedRow);
						}
					});
				contextMenu.add(miMoveLast);
			}
			
		}
		
		
		return contextMenu;
	}
	
	
	private void moveRowFirst(int rowIndex) {
		if (rowIndex == 0 || getRowCount() == 0)
			return;
		
		RModelListTableModel model = (RModelListTableModel) getModel();
		model.moveRow(rowIndex, rowIndex, 0);
	}

	
	private void moveRowUp(int rowIndex) {
		if (rowIndex == 0 || getRowCount() == 0)
			return;
		
		RModelListTableModel model = (RModelListTableModel) getModel();
		model.moveRow(rowIndex, rowIndex, rowIndex - 1);
	}

	
	private void moveRowDown(int rowIndex) {
		if (rowIndex == getRowCount() - 1 || getRowCount() == 0)
			return;
		
		RModelListTableModel model = (RModelListTableModel) getModel();
		model.moveRow(rowIndex, rowIndex, rowIndex + 1);
	}
	
	
	private void moveRowLast(int rowIndex) {
		if (rowIndex == getRowCount() - 1 || getRowCount() == 0)
			return;
		
		RModelListTableModel model = (RModelListTableModel) getModel();
		model.moveRow(rowIndex, rowIndex, getRowCount() - 1);
	}

	
	@Override
	public String getToolTipText(MouseEvent event) {
		// TODO Auto-generated method stub
		int row = rowAtPoint(event.getPoint());
		if (row == -1)
			return super.getToolTipText(event);
		
		RModel model = getRModel(row);
		if (model == null)
			return super.getToolTipText(event);
		else 
			return "Nice form: " + RModelAssoc.prettySpec(model.getNiceForm());
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		if(SwingUtilities.isRightMouseButton(e) ) {
			JPopupMenu contextMenu = createContextMenu();
			if(contextMenu != null) 
				contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
		}
		else if (e.getClickCount() >= 2) {
			showDetail();
		}
		
	}


	public static void niceFormDlg(Component comp, RModel model) {
		
		String spec = RModelAssoc.prettySpec(model.getNiceForm());
		final JDialog dlg = new JDialog(
				MiscUtil.getFrameForComponent(MiscUtil.getFrameForComponent(comp)), "Nice form", true);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setSize(400, 200);
		dlg.setLocationRelativeTo(MiscUtil.getFrameForComponent(comp));
		dlg.setLayout(new BorderLayout());

		JTextArea txtSpec = new JTextArea();
		txtSpec.setText(spec);
		txtSpec.setEditable(false);
		txtSpec.setWrapStyleWord(true);
		txtSpec.setLineWrap(true);
		txtSpec.setAutoscrolls(true);
		txtSpec.setCaretPosition(0);
		dlg.add(txtSpec, BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		dlg.add(footer, BorderLayout.SOUTH);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dlg.dispose();
			}
		});
		footer.add(btnClose);
		dlg.setVisible(true);
		
	}
	
	
	@Override
	public void mouseEntered(MouseEvent e) { }


	@Override
	public void mouseExited(MouseEvent e) { }


	@Override
	public void mousePressed(MouseEvent e) { }


	@Override
	public void mouseReleased(MouseEvent e) { }  


}
