package vy.phoebe.regression.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;

import vy.phoebe.Config;
import vy.phoebe.dataset.Dataset;
import vy.phoebe.regression.RModelList;
import vy.phoebe.regression.RModelParser;
import vy.phoebe.regression.RModelParserImpl;
import vy.phoebe.util.FileUtil;
import vy.phoebe.util.MiscUtil;

public class RMParserTextArea extends JTextArea {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	protected Dataset dataset = null;
	
	
	protected RModelParser parser = null;
	
	
	protected JTextComponent associate = null;

	
	public RMParserTextArea() {
		this(null);
	}
	
	
	public RMParserTextArea(JTextComponent associate) {
		this(null, associate);
	}
	
	
	public RMParserTextArea(Dataset dataset, JTextComponent associate) {
		super();
		this.dataset = dataset;
		this.associate = associate;
		this.parser = new RModelParserImpl(dataset);
		
		init();
	}

	
	public void setDataset(Dataset dataset) {
		this.dataset = dataset;
		this.parser.setDataset(dataset);
	}
	
	
	private void init() {
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), 
								e.getX(), e.getY());
				}
			}
			
		});
	}
	
	
	public RModelList parse() {
		String text = getText().trim();
		
		return parser.parseExprs(text);
	}
	
	
	private void showParse() {
		RModelList modelList = parse();
		if (modelList.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"No model", 
					"No model", 
					JOptionPane.ERROR_MESSAGE);
		}
		else
			new RModelListDlg(modelList);
		
	}
	
	
	public void open(File file) {
		String contents = FileUtil.read(file);
        this.setText(contents.toString());
        this.setCaretPosition(0);
        
        if (associate != null) {
        	associate.setText(file.getAbsolutePath());
        	associate.setCaretPosition(0);
        }
	}
	
	
	public void open() {
		FileUtil.ChosenFileResult result = FileUtil.chooseFile(
				this, 
				true, 
				new String[] {}, 
				new String[] {});
		
	    if (result == null)
			return;
	    
	    File file = result.getChosenFile();
	    open(file);
	}
	
	
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miOpen = MiscUtil.makeMenuItem(null, "Open", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					open();
				}
			});
		contextMenu.add(miOpen);
		
		contextMenu.addSeparator();
		
		JMenuItem miCopy = MiscUtil.makeMenuItem(null, "Copy all", 
			new ActionListener() {
					
				public void actionPerformed(ActionEvent e) {
					if (!Config.testClipboard())
						return;
					
					Config.getClipboardProcessor().setText(getText());
				}
			});
		contextMenu.add(miCopy);

		
		JMenuItem miPaste = MiscUtil.makeMenuItem(null, "Paste all", 
			new ActionListener() {
					
				public void actionPerformed(ActionEvent e) {
					if (!Config.testClipboard())
						return;
					
					String text = Config.getClipboardProcessor().getText(); 
					setText(text);
				}
			});
		contextMenu.add(miPaste);

		contextMenu.addSeparator();

		JMenuItem miParse = MiscUtil.makeMenuItem(null, "Parse", 
			new ActionListener() {
					
				public void actionPerformed(ActionEvent e) {
					showParse();
				}
			});
		contextMenu.add(miParse);
		
		return contextMenu;
	}
	
	
}
