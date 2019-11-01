package game;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JPanel;

public class Card extends JPanel {
	public int priority;
	
	public String cardtype;  //≈∆µƒ÷÷¿‡

	File imagefile;

	Image image;

	Toolkit tool;

	Card() {
		priority = 0;
		tool = getToolkit();
	}

	public void setImage(String f) {
		imagefile = new File(f);
		try {
			image = tool.getImage(imagefile.toURI().toURL());
		} catch (Exception exp) {
		}
		repaint();
	}
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		int w = getBounds().width;
		int h = getBounds().height;
		g.drawImage(image, 0, 0, w, h, this);
	}
}
