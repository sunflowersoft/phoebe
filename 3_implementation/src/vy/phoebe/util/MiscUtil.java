package vy.phoebe.util;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;



public class MiscUtil {

	
	public static JButton makeIconButton(URL iconURL, String cmd, String tooltip, String alt, 
			ActionListener listener) {
		JButton button = new JButton();
        button.setActionCommand(cmd);
        button.setToolTipText(tooltip);
        button.addActionListener(listener);

        if (iconURL != null) {
            button.setIcon(new ImageIcon(iconURL, alt));
        }
        else {                                     //no image found
            button.setText(alt);
        }

        return button;
	}


	public static JMenuItem makeMenuItem(URL iconURL, String text, ActionListener listener) {
		JMenuItem item = new JMenuItem(text);
		item.addActionListener(listener);
		
        if (iconURL != null) {
            item.setIcon(new ImageIcon(iconURL, text));
        }

        return item;
	}


	public static CommandLine parseCmdLine(String[] args, Options options) {
		
		CommandLineParser parser = new GnuParser();
		
		try {
			return parser.parse(options, args);
		}
		catch (ParseException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	public static void TangChiNeThuongChiThatNhieu(Window frame) {
		try {
			URL iconURL = frame.getClass().getResource(Constants.IMAGES_DIR + "TangChiNe-32x32.png");
			Image iconImage = ImageIO.read(iconURL);
			frame.setIconImage(iconImage);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public static Frame getFrameForComponent(Component comp) {
	    if (comp == null)
	        return null;
	    if (comp instanceof Frame)
	        return (Frame)comp;
	    
	    return getFrameForComponent(comp.getParent());
	}

	


}
