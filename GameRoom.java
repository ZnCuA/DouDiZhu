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

	JLabel rulearea; // 右边规则区域

	Table[] TABLE = new Table[9];

	JLabel[] table = new JLabel[9];

	Seat[][] seat = new Seat[9][4]; // 座位

	JTextArea receive; // 接收信息区域

	JTextField send; // 发送信息框

	JComboBox onlineplayer; // 在线玩家

	boolean startflag = false;

	String playerID; // 自己的账号

	Socket socket;

	String serverIP = null;

	DataInputStream is = null;

	DataOutputStream os = null;

	public GameRoom(String _playerID, String _serverIP, Socket _socket,
			DataInputStream _is, DataOutputStream _os) {
		playerID = _playerID; // 游戏者账号
		serverIP = _serverIP;
		socket = _socket;
		is = _is;
		os = _os;
		initGUI();
		Playsound.play("audio//comein.wav");
		new Msgrev().start();
	}

	public void initGUI() {
		setTitle("游戏大厅");
		setIconImage(new ImageIcon("pics\\rush.png").getImage());
		setSize(800, 600);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ExitWindow());
		allarea = new PanelArea();
		allarea.setImageFile(new File("pics\\大厅背景.jpg"));
		add(allarea);
		allarea.setLayout(null);
		initrulearea();
		inittalkarea();
		inittablearea();
		initheadarea();
	}

	/*
	 * 关闭窗口触发事件
	 */
	class ExitWindow extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			if (startflag) {
				JOptionPane.showMessageDialog(null, "请先关闭游戏再退出!", "提示",
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
	 * 初始化游戏桌区域
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
				seat[i][j].setIcon(new ImageIcon("pics\\大厅\\seatn.png"));
				seat[i][j].addMouseListener(this);
			}

		for (int i = 0; i < 9; i++) {
			table[i] = new JLabel();
			table[i].setBounds(50, 50, 52, 50);
			table[i].setIcon(new ImageIcon("pics\\大厅\\tablen.png"));
			TABLE[i].setLayout(null);
			TABLE[i].add(table[i]);
			seat[i][0].setBounds(55, 5, 40, 40);// 上
			seat[i][1].setBounds(8, 55, 40, 40);// 左
			seat[i][2].setBounds(55, 105, 40, 40);// 下
			seat[i][3].setBounds(105, 55, 40, 40);// 右
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
		playername.setText("账号：" + playerID);
		playername.setForeground(Color.WHITE);
		playerscore = new JLabel();
		playerscore.setBounds(100, 50, 100, 20);
		playerscore.setForeground(Color.WHITE);
		allarea.add(playername);
		allarea.add(playerscore);
		allarea.add(headpic);
	}

	/*
	 * 初始化规则区域
	 */
	public void initrulearea() {
		rulearea = new JLabel();
		rulearea.setBounds(560, 50, 200, 200);
		rulearea.setIcon(new ImageIcon("pics\\rule.png"));
		allarea.add(rulearea);
	}

	/*
	 * 初始化讨论区域
	 */
	public void inittalkarea() {
		receive = new JTextArea();
		// receive.setPreferredSize(getSize(null));
		receive.setBounds(580, 280, 200, 250);
		receive.setLineWrap(true);// 激活自动换行功能
		receive.setWrapStyleWord(true);// 激活断行不断字功能
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
		onlineplayer.addItem("所有人");
		// onlineplayer.setOpaque(false);
		allarea.add(onlineplayer);

	}

	public void mouseClicked(MouseEvent e) {
		/*
		 * 点击到seat
		 */
		if (e.getSource() instanceof Seat && !startflag) {
			Seat lb = (Seat) e.getSource();
			if (!lb.state) {
				// lb
				// .setIcon(new ImageIcon("pics\\大厅\\seat" + lb.seatnum
				// + ".png"));
				lb.state = true;
				/*
				 * 只能进入一个游戏，startflag表示是否已经开始一个游戏
				 */
				startflag = true;
				/*
				 * 进入seat，并向主机发送信息
				 */
				try {
					os.writeUTF("entertable");
					os.writeUTF(lb.tablenum + "");
					os.writeUTF(lb.seatnum + "");
				} catch (IOException e1) {
				}
				//启动游戏
				new Gamearea(playerID, serverIP, lb.tablenum, lb.seatnum);
			}
		}
	}

	public void mouseEntered(MouseEvent e) {
		if (e.getSource() instanceof Seat) {
			Seat lb = (Seat) e.getSource();
			if (!lb.state)
				lb.setIcon(new ImageIcon("pics\\大厅\\seatmousemove.png"));
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
				lb.setIcon(new ImageIcon("pics\\大厅\\seatn.png"));
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
	 * 新的线程,接收服务器发来的信息
	 */

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == send) {
			if (onlineplayer.getSelectedItem().toString().trim().equals("所有人")) {
				receive.append(playerID + "向"
						+ onlineplayer.getSelectedItem().toString().trim()
						+ "说:" + send.getText() + "\n");
			} else {
				receive.append(playerID + "向"
						+ onlineplayer.getSelectedItem().toString().trim()
						+ "说(悄悄话):" + send.getText() + "\n");
			}
			try {
				os.writeUTF("chating");
				os.writeUTF(onlineplayer.getSelectedItem().toString().trim());
				os.writeUTF(playerID);
				os.writeUTF(send.getText());
			} catch (IOException e1) {
			}
			send.setText("");
		}
	}

	class Msgrev extends Thread {
		public void run() {
			while (true) {
				try {
					String s = is.readUTF();
					/*
					 * 初始化其他用户名添加到combobox中、当前在游戏的玩家
					 */
					if (s.equals("otheronlineplayer")) {
						while ((s = is.readUTF()).equals("END1") == false) {
							onlineplayer.addItem(s);
						}
						/*
						 * 初始化进入seat的玩家
						 */
						while ((s = is.readUTF()).equals("END2") == false) {
							int tn = Integer.parseInt(s);
							int sn = Integer.parseInt(is.readUTF());
							seat[tn][sn].setIcon(new ImageIcon("pics\\大厅\\seat"
									+ sn + ".png"));
							seat[tn][sn].state = true;
							seat[tn][sn].setIcon(new ImageIcon(
									"pics\\icon\\online\\"
											+ is.readUTF().trim() + ".gif"));
						}

						playerscore.setText("积分："
								+ Integer.parseInt(is.readUTF()));
						headpic.setIcon(new ImageIcon("pics\\icon\\online\\"
								+ is.readUTF().trim() + ".gif"));
					}
					/*
					 * 进来一个人
					 */
					if (s.equals("Newcomer")) {
						String comername = is.readUTF();
						receive.append(comername + "进入了房间.\n");
						if (!comername.equals(playerID))
							onlineplayer.addItem(comername);
					}
					/*
					 * 聊天信息
					 */
					if (s.equals("chating")) {
						s = is.readUTF();
						receive.append(s + "\n");
					}
					/*
					 * 离开一个人
					 */
					if (s.equals("someoneexit")) {
						String exitname = is.readUTF();
						receive.append(exitname + "离开了房间.\n");
						int i = -1;
						while (++i < onlineplayer.getItemCount()) {
							if (onlineplayer.getItemAt(i).toString().equals(
									exitname)) {
								onlineplayer.removeItemAt(i);
							}
						}
					}
					/*
					 * 有用户加入seat
					 */
					if (s.equals("comeoneplayer")) {
						int tn = Integer.parseInt((String) is.readUTF());
						int sn = Integer.parseInt((String) is.readUTF());
						seat[tn][sn].setIcon(new ImageIcon(
								"pics\\icon\\online\\" + is.readUTF().trim()
										+ ".gif"));
						seat[tn][sn].state = true;
					}
					/*
					 * 有玩家离开seat，重绘图形
					 */
					if (s.equals("oneplayerexitseat")) {
						int tn = Integer.parseInt(is.readUTF());
						int sn = Integer.parseInt(is.readUTF());
						seat[tn][sn].setIcon(new ImageIcon(
								"pics\\大厅\\seatn.png"));
						seat[tn][sn].state = false;
						table[tn]
								.setIcon(new ImageIcon("pics\\大厅\\tablen.png"));
					}
					/*
					 * 收到可以重新开始的信号，startflag重新置true
					 */
					if (s.equals("youcanstartagain")) {
						startflag = false;
					}
					/*
					 * 收到游戏开始信息，桌子颜色变化
					 */
					if (s.equals("Gamestart")) {
						table[Integer.parseInt(is.readUTF())]
								.setIcon(new ImageIcon("pics\\大厅\\tables.png"));
					}
				} catch (IOException e) {
					JOptionPane.showMessageDialog(null, "与服务器失去连接，请重新登录！",
							"提示", JOptionPane.WARNING_MESSAGE);
					System.exit(0);
				}
			}
		}
	}
}
