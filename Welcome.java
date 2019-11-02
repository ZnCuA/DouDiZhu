package game;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.InetAddress;
import java.net.Socket;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

public class Welcome extends JFrame implements MouseListener {
	PanelArea allarea;

	JLabel loginarea, login, exit, close, register, LOGIN, SET, ipsetarea;

	JTextField loginid, serverip;

	JPasswordField password;

	String serverIP = null;

	Socket s = null;

	DataInputStream is = null;

	DataOutputStream os = null;

	Welcome() {
		super("Rush");
		setIconImage(new ImageIcon("pics\\rush.png").getImage());
		setSize(600, 480);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		allarea = new PanelArea();
		allarea.setImageFile(new File("pics\\welcomebg.jpg"));
		add(allarea);
		allarea.setLayout(null);
		initloginarea();
		initipsetarea();

		LOGIN = new JLabel();
		LOGIN.setBounds(400, 250, 156, 36);
		LOGIN.setIcon(new ImageIcon("pics//��¼.png"));
		LOGIN.addMouseListener(this);
		SET = new JLabel();
		SET.setBounds(400, 320, 156, 36);
		SET.setIcon(new ImageIcon("pics//��Ϸ����.png"));
		SET.addMouseListener(this);

		allarea.add(LOGIN);
		allarea.add(SET);

		repaint();

		// ��ʼ��Ϊ
		serverIP = "127.0.0.1";
	}

	/*
	 * ��ʼ����¼����
	 */
	public void initloginarea() {
		loginarea = new JLabel();
		loginarea.setBounds(100, 80, 204, 86);
		loginarea.setIcon(new ImageIcon("pics\\loginarea.png"));
		allarea.add(loginarea);
		loginid = new JTextField();
		loginid.setBounds(60, 5, 120, 20);
		loginarea.setLayout(null);
		loginarea.add(loginid);
		password = new JPasswordField();
		password.setEchoChar('*');
		password.setBounds(60, 30, 120, 20);
		loginarea.add(password);
		login = new JLabel();
		exit = new JLabel();
		register = new JLabel();
		login.setBounds(40, 60, 50, 20);
		login.setIcon(new ImageIcon("pics//button//loginbutton.png"));
		login.addMouseListener(this);
		register.setBounds(80, 61, 50, 20);
		register.setIcon(new ImageIcon("pics//button//register.png"));
		register.addMouseListener(this);
		exit.setBounds(120, 60, 50, 20);
		exit.setIcon(new ImageIcon("pics//button//exit.png"));
		exit.addMouseListener(this);
		loginarea.add(login);
		loginarea.add(exit);
		loginarea.add(register);
		loginarea.setVisible(false);
	}

	/*
	 * ��ʼ����Ϸ��������
	 */
	public void initipsetarea() {
		ipsetarea = new JLabel();
		ipsetarea.setIcon(new ImageIcon("pics//������IP.png"));
		ipsetarea.setBounds(100, 200, 204, 86);
		allarea.add(ipsetarea);
		ipsetarea.setLayout(null);
		serverip = new JTextField();
		serverip.setBounds(87, 18, 100, 25);
		serverip.setText("127.0.0.1");
		ipsetarea.add(serverip);
		close = new JLabel();
		close.setIcon(new ImageIcon("pics//button//close.png"));
		close.setBounds(80, 57, 50, 20);
		close.addMouseListener(this);
		ipsetarea.add(close);
		ipsetarea.setVisible(false);
	}

	/*
	 * �û���¼
	 */
	public void Login() {
		if (loginid.getText().equals("") || password.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "�������˺ź����룡", "��ʾ",
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		try {
			s = new Socket(serverIP, 6666);
			is = new DataInputStream(s.getInputStream());
			os = new DataOutputStream(s.getOutputStream());
			os.writeUTF("login");
			os.writeUTF(loginid.getText().trim());
			os.writeUTF(password.getText().trim());
			String state = is.readUTF();
			if (state.equals("2")) {
				new GameRoom(loginid.getText().trim(), serverIP, s, is, os);// ������Ϸ����
				this.dispose();
			} else if (state.equals("1")) {
				JOptionPane.showMessageDialog(this, "���˺��ѵ�¼��", "��ʾ",
						JOptionPane.WARNING_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(this, "�˺Ż��������", "��ʾ",
						JOptionPane.WARNING_MESSAGE);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "��������δ������", "��ʾ",
					JOptionPane.WARNING_MESSAGE);
		}
	}

	/*
	 * ���½�Ϊ������
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() == LOGIN) {
			Playsound.play("audio//click.wav");
			loginarea.setVisible(true);
		}
		if (e.getSource() == exit) {
			loginarea.setVisible(false);
		}
		if (e.getSource() == SET) {
			Playsound.play("audio//click.wav");
			ipsetarea.setVisible(true);
		}
		if (e.getSource() == close) {
			// �˴���÷�����IP��ַ
			Playsound.play("audio//click.wav");
			serverIP = serverip.getText().trim();
			ipsetarea.setVisible(false);
		}
		if (e.getSource() == login) {
			Login();
		}
		if(e.getSource()==register){
			new Register(serverIP);
		}
	}

	public void mouseEntered(MouseEvent e) {
		setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		if (e.getSource() == login) {
			login.setIcon(new ImageIcon("pics//button//loginbuttonon.png"));
		}
		if (e.getSource() == exit) {
			exit.setIcon(new ImageIcon("pics//button//exiton.png"));
		}
		if (e.getSource() == LOGIN) {
			LOGIN.setBounds(395, 245, 156, 36);
			LOGIN.setIcon(new ImageIcon("pics//��¼on.png"));
		}
		if (e.getSource() == SET) {
			SET.setBounds(395, 315, 156, 36);
			SET.setIcon(new ImageIcon("pics//��Ϸ����on.png"));
		}
		if (e.getSource() == close) {
			close.setIcon(new ImageIcon("pics//button//closeon.png"));
		}
		if (e.getSource() == register) {
			register.setIcon(new ImageIcon("pics//button//registeron.png"));
		}
	}

	public void mouseExited(MouseEvent e) {
		setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
		if (e.getSource() == login) {
			login.setIcon(new ImageIcon("pics//button//loginbutton.png"));
		}
		if (e.getSource() == exit) {
			exit.setIcon(new ImageIcon("pics//button//exit.png"));
		}
		if (e.getSource() == LOGIN) {
			LOGIN.setBounds(400, 250, 156, 36);
			LOGIN.setIcon(new ImageIcon("pics//��¼.png"));
		}
		if (e.getSource() == SET) {
			SET.setBounds(400, 320, 156, 36);
			SET.setIcon(new ImageIcon("pics//��Ϸ����.png"));
		}
		if (e.getSource() == close) {
			close.setIcon(new ImageIcon("pics//button//close.png"));
		}
		if (e.getSource() == register) {
			register.setIcon(new ImageIcon("pics//button//register.png"));
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public static void main(String[] args) {
		new Welcome();
	}
}
