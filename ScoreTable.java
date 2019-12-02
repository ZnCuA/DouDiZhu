package game;

import java.awt.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;

public class ScoreTable extends JFrame{

	private Vector<Vector<String>> data = new Vector<Vector<String>>();
	private Vector<String> dataTitle = new Vector<String>();
	String userid[];
	String Score[];
	
	public ScoreTable(String usid,String scorce) {
		super();
		setTitle("���а�");
		setBounds(300,300,300,300);
		setVisible(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
 
		dataTitle.add("����");
		dataTitle.add("�û���");
		dataTitle.add("�÷�");
		
		userid=usid.split("#");
		Score=scorce.split("#");
		
		for (int i = 0; i < userid.length; i++) {
			Vector<String> W = new Vector<String>();
			W.add(i+1+"");
			W.add(userid[i]);
			W.add(Score[i]);
			data.add(W);
		}
		
		JTable table = new JTable(data, dataTitle);
		add(table, BorderLayout.CENTER);
		JTableHeader tableHeader = table.getTableHeader();// ��ñ��ͷ����
		// �����ͷ��ӵ��߽粼�ֵ��Ϸ�
		add(tableHeader, BorderLayout.NORTH);
	}
 
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		ScoreTable T = new ScoreTable("1#2#3","10#20#30");
// 
//	}
 
}


