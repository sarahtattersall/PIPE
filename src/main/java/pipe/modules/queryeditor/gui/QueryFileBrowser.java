/**
 * QueryFileBrowser
 * 
 * This is the file chooser that is used for saving and loading query files
 * 
 * @author Tamas Suto
 * @date 02/08/07
 */


package pipe.modules.queryeditor.gui;

import java.io.File;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import pipe.gui.ExtensionFilter;


public class QueryFileBrowser extends JFileChooser {

	private final String ext;


	private QueryFileBrowser(String filetype, String ext, String path) {
		super();
		if (filetype == null) 
			filetype = "file";
		if (path != null) {
			File f = new File(path);
			if(f.exists())setCurrentDirectory(f);
			if(!f.isDirectory())setSelectedFile(f);
		}
		this.ext = ext;
		ExtensionFilter filter = new ExtensionFilter(ext,filetype);
		setFileFilter(filter);
	}

	public QueryFileBrowser(String path) {
		this("Performance Query","xml",path); // default parameters
	}

	public QueryFileBrowser() {
		this(null);
	}


	public File openFile() {
		if (showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			try {
				return getSelectedFile().getCanonicalFile();
			} catch (IOException e) { }
		}
		return null;    
	}  
	
	public String saveFile() {
		if (showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			try {
				File f = getSelectedFile();
				if(!f.getName().endsWith("."+ext)) f = new File(f.getCanonicalPath()+"."+ext); // force extension
				if (f.exists() && 
						JOptionPane.showConfirmDialog(this,f.getCanonicalPath()+"\nDo you want to overwrite this file?") != 
						JOptionPane.YES_OPTION) { 
					return null;
				}
				return f.getCanonicalPath();
			} catch (IOException e) { }
		}
		return null;    
	}  
} 
