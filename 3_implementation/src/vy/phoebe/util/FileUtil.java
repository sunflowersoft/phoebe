package vy.phoebe.util;

import java.awt.Component;
import java.io.File;
import java.io.FileReader;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

public class FileUtil {

	public static String getExtension(File f) {
	    String ext = null;
	    
	    String s = f.getName();
	    int i = s.lastIndexOf('.');
	    if (i > 0 &&  i < s.length() - 1) {
	        ext = s.substring(i+1).toLowerCase();
	    }
	    
	    return ext;
	}


	public static class ChosenFileResult {
		
		private File chosenFile = null;
		
		
		private String chosenExt = null;
		
		
		public ChosenFileResult(File chosenFile, String chosenExt) {
			this.chosenFile = chosenFile;
			this.chosenExt = chosenExt;
		}
		
		
		public File getChosenFile() {
			return chosenFile;
		}
		
		
		public String getChosenExt() {
			return chosenExt;
		}
		
	}

	
	static class ChosenFileFilter extends FileFilter {
	
		private String ext = null;
		
		private String desc = null;
		
		public ChosenFileFilter(String ext, String desc) {
			this.ext = ext;
			this.desc = desc;
		}
	
		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			
			String ext = getExtension(f);
			if (ext != null && 
					ext.toLowerCase().equals(this.ext.toLowerCase()))
				return true;
			
			return false;
		}
	
		@Override
		public String getDescription() {
			return desc;
		}
	
		public String getExt() {
			return ext;
		}
	}

	
	public static ChosenFileResult chooseFile(
			Component parent, 
			boolean open, 
			final String[] exts, 
			final String[] descs) {
		
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		
		for (int i = 0; i < exts.length; i++) {
			fc.addChoosableFileFilter(new ChosenFileFilter(exts[i], descs[i]));
		}
		
		int ret = open ? 
				fc.showOpenDialog(parent) : fc.showSaveDialog(parent);
	    if (ret != JFileChooser.APPROVE_OPTION)
	    	return null;
	    
	    File file = fc.getSelectedFile();
	    FileFilter filter = fc.getFileFilter();
	    
	    if (file == null)
	    	return null;
	    
	    if ( (filter != null) && (filter instanceof ChosenFileFilter) )
	    	return new ChosenFileResult(file,
	    			((ChosenFileFilter)filter).getExt());
	    else
	    	return new ChosenFileResult(file, null);
	}

	
	public static File chooseDefaultFile(Component parent, boolean open) {
		
		ChosenFileResult result = chooseFile(
				parent, 
				open, 
				new String[] {
						Constants.PHOEBE_EXT,
						"xls"
					}, 
				new String[] {
						"Phoebe file (*." + Constants.PHOEBE_EXT + ")",
						"Excel 97-2003 (*.xls)"
					});
		
	    if (result == null)
			return null;
	    
	    File file = result.getChosenFile();
	    String ext = getExtension(file);
	    if (open == false && ext == null) {
	    	ext = result.getChosenExt();
	    	if (ext == null)
	    		ext = Constants.PHOEBE_EXT;
	    	file = new File(file.getAbsolutePath() + "." + ext);
	    }
	    
	    return file;
	}

	
	public static File chooseFile2(Component parent, boolean open, 
			String[] exts, String[] descs, String defaultExt) {
		
		ChosenFileResult result = chooseFile(
				parent, 
				open, 
				exts, 
				descs);
		
        if (result == null)
			return null;
        
        File file = result.getChosenFile();
        String ext = getExtension(file);
        if (open == false && ext == null) {
        	ext = result.getChosenExt();
        	if (ext == null && defaultExt != null)
        		ext = defaultExt;
        	file = new File(file.getAbsolutePath() + "." + ext);
        }
        
        return file;
		
	}

	
	public static String read(File file) {
		StringBuilder contents = new StringBuilder();
		try {
	        FileReader reader = new FileReader(file);
	        
			char[] buffer = new char[4096];
			int read = 0;
			do {
				contents.append(buffer, 0, read);
				read = reader.read(buffer);
			}
			while (read >= 0);
			
			reader.close();
			
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		return contents.toString();
	}

	
	
}
