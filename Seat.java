package game;

import javax.swing.JLabel;

public class Seat extends JLabel{
	public boolean state;
	public int tablenum;
	public int seatnum;
	public Seat(){
		state=false;
		tablenum=-1;
		seatnum=-1;
	}
}
