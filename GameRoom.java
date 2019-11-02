package game;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GameRoom extends JFrame implements ActionListener, MouseListener {
	PanelArea allarea;

	JLabel tablearea;

	JLabel headpic, playername, playerscore;

	JLabel rulearea; // �ұ߹�������

	Table[] TABLE = new Table[9];

	JLabel[] table = new JLabel[9];

	Seat[][] seat = new Seat[9][4]; // ��λ

	JTextArea receive; // ������Ϣ����

	JTextField send; // ������Ϣ��

	JComboBox onlineplayer; // �������

	boolean startflag = false;

	String playerID; // �Լ����˺�

	Socket socket;

	String serverIP = null;

	DataInputStream is = null;

	DataOutputStream os = null;

	public GameRoom(String _playerID, String _serverIP, Socket _socket,
			DataInputStream _is, DataOutputStream _os) {
		playerID = _playerID; // ��Ϸ���˺�
		serverIP = _serverIP;
		socket = _socket;
		is = _is;
		os = _os;
		initGUI();
		Playsound.play("audio//comein.wav");
		new Msgrev().start();
	}

	public void initGUI() {
		setTitle("��Ϸ����");
		setIconImage(new ImageIcon("pics\\rush.png").getImage());
		setSize(800, 600);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ExitWindow());
		allarea = new PanelArea();
		allarea.setImageFile(new File("pics\\��������.jpg"));
		add(allarea);
		allarea.setLayout(null);
		initrulearea();
		inittalkarea();
		inittablearea();
		initheadarea();
	}

	/*
	 * �رմ��ڴ����¼�
	 */
	class ExitWindow extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			if (startflag) {
				JOptionPane.showMessageDialog(null, "���ȹر���Ϸ���˳�!", "��ʾ",
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			try {
				os.writeUTF("exit");
				os.writeUTF(playerID);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			System.exit(0);
		}
	}

	/*
	 * ��ʼ����Ϸ������
	 */
	public void inittablearea() {

		tablearea = new JLabel();
		tablearea.setBounds(0, 100, 450, 450);
		allarea.add(tablearea);

		tablearea.setLayout(new GridLayout(3, 3));
		for (int i = 0; i < 9; i++) {
			TABLE[i] = new Table();
			tablearea.add(TABLE[i]);
		}

		for (int i = 0; i < 9; i++)
			for (int j = 0; j < 4; j++) {
				seat[i][j] = new Seat();
				seat[i][j].tablenum = i;
				seat[i][j].seatnum = j;
				seat[i][j].setIcon(new ImageIcon("pics\\����\\seatn.png"));
				seat[i][j].addMouseListener(this);
			}

		for (int i = 0; i < 9; i++) {
			table[i] = new JLabel();
			table[i].setBounds(50, 50, 52, 50);
			table[i].setIcon(new ImageIcon("pics\\����\\tablen.png"));
			TABLE[i].setLayout(null);
			TABLE[i].add(table[i]);
			seat[i][0].setBounds(55, 5, 40, 40);// ��
			seat[i][1].setBounds(8, 55, 40, 40);// ��
			seat[i][2].setBounds(55, 105, 40, 40);// ��
			seat[i][3].setBounds(105, 55, 40, 40);// ��
			TABLE[i].add(seat[i][0]);
			TABLE[i].add(seat[i][1]);
			TABLE[i].add(seat[i][2]);
			TABLE[i].add(seat[i][3]);
		}
	}

	public void initheadarea() {
		headpic = new JLabel();
		headpic.setBounds(40, 30, 40, 40);
		headpic.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(100, 100, 100)));
		headpic.addMouseListener(this);
		playername = new JLabel();
		playername.setBounds(100, 30, 100, 20);
		playername.setText("�˺ţ�" + playerID);
		playername.setForeground(Color.WHITE);
		playerscore = new JLabel();
		playerscore.setBounds(100, 50, 100, 20);
		playerscore.setForeground(Color.WHITE);
		allarea.add(playername);
		allarea.add(playerscore);
		allarea.add(headpic);
	}

	/*
	 * ��ʼ����������
	 */
	public void initrulearea() {
		rulearea = new JLabel();
		rulearea.setBounds(560, 50, 200, 200);
		rulearea.setIcon(new ImageIcon("pics\\rule.png"));
		allarea.add(rulearea);
	}

	/*
	 * ��ʼ����������
	 */
	public void inittalkarea() {
		receive = new JTextArea();
		// receive.setPreferredSize(getSize(null));
		receive.setBounds(580, 280, 200, 250);
		receive.setLineWrap(true);// �����Զ����й���
		receive.setWrapStyleWord(true);// ������в����ֹ���
		receive.setEditable(false);
		receive.setOpaque(false);
		receive.setForeground(Color.PINK);
		receive.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(255, 255, 51)));
		allarea.add(receive);

		send = new JTextField();
		send.setBounds(670, 535, 110, 25);
		send.setOpaque(false);
		send.setForeground(Color.WHITE);
		send.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 255, 51)));
		send.addActionListener(this);
		allarea.add(send);

		onlineplayer = new JComboBox();
		onlineplayer.setBounds(580, 535, 80, 25);
		onlineplayer.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(255, 255, 51)));
		onlineplayer.addItem("������");
		// onlineplayer.setOpaque(false);
		allarea.add(onlineplayer);

	}

	public void mouseClicked(MouseEvent e) {
		/*
		 * �����seat
		 */
		if (e.getSource() instanceof Seat && !startflag) {
			Seat lb = (Seat) e.getSource();
			if (!lb.state) {
				// lb
				// .setIcon(new ImageIcon("pics\\����\\seat" + lb.seatnum
				// + ".png"));
				lb.state = true;
				/*
				 * ֻ�ܽ���һ����Ϸ��startflag��ʾ�Ƿ��Ѿ���ʼһ����Ϸ
				 */
				startflag = true;
				/*
				 * ����seat����������������Ϣ
				 */
				try {
					os.writeUTF("entertable");
					os.writeUTF(lb.tablenum + "");
					os.writeUTF(lb.seatnum + "");
				} catch (IOException e1) {
				}
				//������Ϸ
				//��վ��λ�ã���ʵ��
				new Gamearea(playerID, serverIP, lb.tablenum, lb.seatnum);
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
		if (e.getSource() instanceof Seat) {
			Seat lb = (Seat) e.getSource();
			if (!lb.state)
				lb.setIcon(new ImageIcon("pics\\����\\seatmousemove.png"));
		}
		if (e.getSource() == headpic) {
			headpic.setBorder(javax.swing.BorderFactory
					.createLineBorder(new java.awt.Color(255, 255, 100)));
		}
	}

	public void mouseExited(MouseEvent e) {
		if (e.getSource() instanceof Seat) {
			Seat lb = (Seat) e.getSource();
			if (!lb.state)
				lb.setIcon(new ImageIcon("pics\\����\\seatn.png"));
		}
		if (e.getSource() == headpic) {
			headpic.setBorder(javax.swing.BorderFactory
					.createLineBorder(new java.awt.Color(100, 100, 100)));
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	/*
	 * �µ��߳�,���շ�������������Ϣ
	 */
	@Override
	public void actionPerformed(ActionEvent e) {

	}

	class Msgrev extends Thread {
		public void run() {
			while (true) {
				try {
					String s = is.readUTF();
					/*
					 * ��ʼ�������û�����ӵ�combobox�С���ǰ����Ϸ�����
					 */
					if (s.equals("otheronlineplayer")) {
						while ((s = is.readUTF()).equals("END1") == false) {
							onlineplayer.addItem(s);
						}
						/*
						 * ��ʼ������seat�����
						 */
						while ((s = is.readUTF()).equals("END2") == false) {
							int tn = Integer.parseInt(s);
							int sn = Integer.parseInt(is.readUTF());
							seat[tn][sn].setIcon(new ImageIcon("pics\\����\\seat"
									+ sn + ".png"));
							seat[tn][sn].state = true;
							seat[tn][sn].setIcon(new ImageIcon(
									"pics\\icon\\online\\"
											+ is.readUTF().trim() + ".gif"));
						}

						playerscore.setText("���֣�"
								+ Integer.parseInt(is.readUTF()));
						headpic.setIcon(new ImageIcon("pics\\icon\\online\\"
								+ is.readUTF().trim() + ".gif"));
					}
					/*
					 * ����һ����
					 */
					if (s.equals("Newcomer")) {
						String comername = is.readUTF();
						receive.append(comername + "�����˷���.\n");
						if (!comername.equals(playerID))
							onlineplayer.addItem(comername);
					}
					/*
					 * ������Ϣ
					 */

					/*
					 * �뿪һ����
					 */

					/*
					 * ���û�����seat
					 */

					/*
					 * ������뿪seat���ػ�ͼ��
					 */

					/*
					 * �յ��������¿�ʼ���źţ�startflag������true
					 */

					/*
					 * �յ���Ϸ��ʼ��Ϣ��������ɫ�仯
					 */
					
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "�������ʧȥ���ӣ������µ�¼��",
							"��ʾ", JOptionPane.WARNING_MESSAGE);
					System.exit(0);
				}
			}
		}
	}
}
