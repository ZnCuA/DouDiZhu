package game;

import java.awt.*;
import java.io.*;
import javax.swing.JPanel;

public class PanelArea extends JPanel{
	File imagefile;
	Image image;
	Toolkit tool;
	public PanelArea(){
		tool=getToolkit();
	}
	public void setImageFile(File f){
		imagefile=f;	
		try{
			image=tool.getImage(imagefile.toURI().toURL());
		}catch(Exception exp){}
		repaint();
	}
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		int w=getBounds().width;
		int h=getBounds().height;
		g.drawImage(image,0,0,w,h,this);
	}
}

