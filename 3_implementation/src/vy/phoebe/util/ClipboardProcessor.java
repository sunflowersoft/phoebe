package vy.phoebe.util;

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

public class ClipboardProcessor implements ClipboardOwner {

	protected Clipboard clipboard = null;
	
	
	public ClipboardProcessor() {
		try {
			this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		}
		catch (HeadlessException e) {
			e.printStackTrace();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
	public void setText(String data) {
		try {
			StringSelection stringSelection = new StringSelection(data);
		    clipboard.setContents(stringSelection, this);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	public String getText() {
		
		String result = "";
		try {
			Transferable contents = clipboard.getContents(null);
			
			if (contents == null || 
					!contents.isDataFlavorSupported(DataFlavor.stringFlavor))
				return "";
			
			result = (String)contents.getTransferData(DataFlavor.stringFlavor);
			
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return result;
	}
	
	
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		// TODO Auto-generated method stub
		
	}
	
	
}
