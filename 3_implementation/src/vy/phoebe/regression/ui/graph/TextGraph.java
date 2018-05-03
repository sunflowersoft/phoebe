package vy.phoebe.regression.ui.graph;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import vy.phoebe.util.DSUtil;

public class TextGraph extends JPanel implements Graph {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	String text = "";
	
	boolean isTextCenter = false;
	
	boolean isTextVertical = false;
	
	Font originFont = null;
	
	GraphViewOption viewOption = new GraphViewOption();
	
	
	public TextGraph() {
		super();
		originFont = getFont();
	}
	
	
	public void setText(String text, boolean isTextCenter, boolean isTextVertical) {
		this.text = text == null ? "" : text;
		
		this.isTextCenter = isTextCenter;
		this.isTextVertical = isTextVertical;
		
		Font font = getFont();
		FontMetrics fm = getFontMetrics(font);
		int width = isTextVertical ?  2 * fm.getHeight() : fm.stringWidth(text);
		int height = isTextVertical? fm.stringWidth(text) : 2 * fm.getHeight();
		setFixedSize(width, height);
	}
	
	
	protected void setFixedSize(int width, int height) {
		setSize(width, height);
		setMaximumSize(new Dimension(width, height));
		setMinimumSize(new Dimension(width, height));
		setPreferredSize(new Dimension(width, height));
	}
	
	
	public void incFontSize(float inc) {
		Font font = originFont.deriveFont( (float)originFont.getSize() + inc);
		setFont(font);
	}
	
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		FontMetrics fm = g.getFontMetrics();
		
		int x = 0, y = 0;
		if (isTextCenter) {
			if (isTextVertical) {
				x = getWidth() / 2;
				y = getHeight() / 2 - fm.stringWidth(text) / 2;
			}
			else {
				x = getWidth() / 2 - fm.stringWidth(text) / 2;
				y = getHeight() / 2;
			}
		}
		else {
			if (isTextVertical) {
				x = fm.getHeight();
				y = 0;
			}
			else {
				x = 0;
				y = fm.getHeight();
			}
		}
		
		Drawer2d.drawText((Graphics2D)g, text, x, y, isTextVertical ? Math.PI / 2 : 0);
	}
	
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, int pageIndex)
			throws PrinterException {
		// TODO Auto-generated method stub
		
		JOptionPane.showMessageDialog(
				this, 
				"Not support this method", 
				"Not support this method", 
				JOptionPane.INFORMATION_MESSAGE);
		
		return 0;
	}

	
	@Override
	public void setupViewOption() {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(
				this, 
				"Not support this method", 
				"Not support this method", 
				JOptionPane.INFORMATION_MESSAGE);
	}

	
	@Override
	public void exportImage() {
		// TODO Auto-generated method stub
		JOptionPane.showMessageDialog(
				this, 
				"Not support this method", 
				"Not support this method", 
				JOptionPane.INFORMATION_MESSAGE);
	}

	
	@Override
	public Rectangle getOuterBox() {
		// TODO Auto-generated method stub
		return new Rectangle(0, 0, getWidth(), getHeight());
	}


	@Override
	public GraphViewOption getViewOption() {
		// TODO Auto-generated method stub
		return viewOption;
	}

	
	
}



class MultiTextGraph extends TextGraph {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	public MultiTextGraph() {
		super();
	}

	
	@Override
	public void setText(String text, boolean isTextCenter, boolean isTextVertical) {
		this.text = text == null ? "" : text;
		this.isTextCenter = isTextCenter;
		this.isTextVertical = isTextVertical;
		
		Font font = getFont();
		FontMetrics fm = getFontMetrics(font);
		int textHeight = fm.getHeight();
		
		List<String> lineList = DSUtil.split(text, "\n", null);
		
		int maxTextWidth = Integer.MIN_VALUE;
		for (int i = 0; i < lineList.size(); i++) {
			String line = lineList.get(i);
			maxTextWidth = Math.max(maxTextWidth, fm.stringWidth(line));
		}
		
		if (isTextVertical)
			setFixedSize((lineList.size() + 1) * textHeight, maxTextWidth);
		else
			setFixedSize(maxTextWidth, (lineList.size() + 1) * textHeight);
	}
	
	
	@Override
	public void paint(Graphics g) {
		// TODO Auto-generated method stub
		super.paint(g);
		
		FontMetrics fm = g.getFontMetrics();
		int textHeight = fm.getHeight();
		List<String> lineList = DSUtil.split(text, "\n", null);
		
		int x = 0, y = 0;
		for (int i = 0; i < lineList.size(); i++) {
			String line = lineList.get(i);
			if (isTextVertical) {
				x += textHeight;
				y = 0;
			}
			else {
				x = 0;
				y += textHeight;
			}
			Drawer2d.drawText((Graphics2D)g, line, x, y, 0);
		}
		
	}
	
	
}


