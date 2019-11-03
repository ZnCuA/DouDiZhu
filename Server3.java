package game;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server extends JFrame implements ActionListener {
	JTextArea text;

	JButton close;

	ServerSocket server;

	Socket you;

	InetAddress youraddress;

	Dao dao;

	// 存放线程
	ArrayList<ServerThread> playerlist = new ArrayList<ServerThread>();

	// 花色
	String[] color = { "c", "d", "s", "h" };

	// 牌数字
	String[] num = { "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
			"14", "15" };

	// 随机分成的四组
	String[][] player = new String[4][13];

	//构造函数
	Server() {
		setTitle("服务器");
		setSize(400, 500);
		setLocationRelativeTo(null);//设置窗口相对于该面板的位置（面板不动窗口动），null表示面板显示与窗口中间
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ExitWindow());
		init();
		validate();
		dao = new Dao();
		startServer(6666);
	}
	
	//服务器窗口初始化
	public void init() {  
		text = new JTextArea();
		String myaddress = null;
		try {
			myaddress = InetAddress.getLocalHost().getHostAddress().toString(); // 服务器IP地址
		} catch (UnknownHostException e) {
		}
		text.setText("启动服务器...(IP地址: " + myaddress + ")\n");
		text.setEditable(false);
		add(text, BorderLayout.CENTER);
		JScrollPane scroll = new JScrollPane(text);
		add(scroll, BorderLayout.CENTER);
		close = new JButton("关闭服务器");
		add(close, BorderLayout.SOUTH);
		close.addActionListener(this);
	}
	
	
//	WindowAdapter是一个实现了WindowListener的类
//	Frame则是一个具体的显示窗体，
//	Frame中添加WindowListener可以实现Frame事件的监听
//	也可以直接添加一个WindowAdapter来取带WIndowListener因为它已经实现了WindowListener
//	frame.addWindowListener(new WindowAdapter(){});
	class ExitWindow extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dao.setAllState("0");
			System.exit(0);
		}
	}


	/*
	 * 随机分成四组牌分发给玩家
	 */
	public void Randomization() {
		int[] a = new int[4];
		for (int i = 0; i < 4; i++)
			a[i] = 0;
		for (int i = 0; i < 13; i++)
			for (int j = 0; j < 4; j++) {
				int row = (new Random().nextInt(100)) % 4;
				if (a[row] < 13)
					player[row][a[row]++] = "" + color[j] + num[i];
				else {
					while (a[row] >= 13)
						row = (row + 1) % 4;
					player[row][a[row]++] = "" + color[j] + num[i];
				}

			}
	}

	public void startServer(int port) {
		try {
			server = new ServerSocket(port);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "服务器已启动！", "提示",
					JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		while (true) {
			try {
				you = server.accept();
				youraddress = you.getInetAddress();
			} catch (IOException e) {
			}
			if (you != null) {
				new ServerThread(you, youraddress).start();
			}
		}
	}

	class ServerThread extends Thread {
		Socket socket;

		InetAddress address;

		DataInputStream is = null;

		DataOutputStream os = null;

		String id = null, psw = null; // 用户信息

		int tablenum; // 用户进入的table号

		int seatnum; // table的seat号

		/*
		 * 该线程的位置属性,"hall"表示大厅线程，"seat"表示seat线程,
		 * 一个player刚登陆时启动一个线程，由于socket不能共享，故在
		 * 进入seat后的线程和刚刚进入大厅的线程标志区别就在location上
		 */
		String location = null;

		boolean readyflag = false; // 游戏是否准备好

		ServerThread(Socket s, InetAddress addr) {
			tablenum = -1;
			seatnum = -1;
			socket = s;
			location = "hall";
			address = addr;
			try {
				is = new DataInputStream(socket.getInputStream());
				os = new DataOutputStream(socket.getOutputStream());
			} catch (IOException e) {
			}
		}

		public void run() {
			SimpleDateFormat tempDate = new SimpleDateFormat(
					"yyyy-MM-dd HH:mm:ss");
			String nowtime;
			String s = "";
			while (true) {
				try {
					s = is.readUTF();
					/*
					 * 登录信息
					 */
					if (s.startsWith("login")) {
						id = is.readUTF();
						psw = is.readUTF();
						String result = dao.Check(id, psw);
						if (result.equals("2")) {  //
							/*
							 * 成功登录,发送到客户端信息
							 */
							dao.setState("1", id);
							os.writeUTF("2");
							/*
							 * 向客户端发送当前有哪些玩家在线
							 */
							os.writeUTF("otheronlineplayer");
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th != this && th.location.equals("hall"))
									os.writeUTF(th.id);
							}
							os.writeUTF("END1");
							/*
							 * 向客户端发送seat里面的玩家
							 */
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th.location.equals("seat")) {
									os.writeUTF((th.tablenum + "").toString());
									os.writeUTF((th.seatnum + "").toString());
									os.writeUTF(dao.getIcon(th.id));
								}
							}
							os.writeUTF("END2");
							os.writeUTF(dao.getScore(this.id) + "");
							os.writeUTF(dao.getIcon(this.id));
							/*
							 * 登陆后将自己加入到playerlist链表中，并向其他玩家发送进入房间信息
							 */
							playerlist.add(this);
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								th.os.writeUTF("Newcomer");
								th.os.writeUTF(id);
							}

							// 服务器记录日志
							nowtime = tempDate.format(new java.util.Date());
							text.append(nowtime + "\n用户：" + id + "\nIP地址："
									+ youraddress.toString() + " 进入了房间\n");

						} else if (result.equals("1")) {
							os.writeUTF("1"); // 重复登录
							break;
						} else {
							os.writeUTF("0"); // 错误
							break;
						}
					}
					/*
					 * 注册信息
					 */
					if (s.equals("register")) {
						String name=is.readUTF();
						String psw=is.readUTF();
						if(!dao.Check(name)){
							dao.Adduser(name, psw);
							os.writeUTF("success");
						}else
							os.writeUTF("failed");
					}
					/*
					 * 玩家聊天信息
					 */
					if (s.equals("chating")) {
						String toname = is.readUTF();
						String fromname = is.readUTF();
						String text = is.readUTF();
						if (toname.equals("所有人")) {
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th != this && th.location.equals("hall")) {
									th.os.writeUTF("chating");
									th.os.writeUTF(fromname + "向所有人说:" + text);
								}
							}
						} else {
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th.id.equals(toname.trim())
										&& th.location.equals("hall")) {
									th.os.writeUTF("chating");
									th.os.writeUTF(fromname + "向" + toname
											+ "说(悄悄话):" + text);
								}
							}
						}
					}

					/*
					 * 进入游戏table的信息，向大厅的其他用户通知
					 */
					if (s.equals("entertable")) {
						String tn = is.readUTF();
						String sn = is.readUTF();
						tablenum = Integer.parseInt(tn);// 得到桌子号
						seatnum = Integer.parseInt(sn);// 得到座位号
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th.location.equals("hall")) {
								th.os.writeUTF("comeoneplayer");
								th.os.writeUTF((tablenum + "").toString());
								th.os.writeUTF((seatnum + "").toString());
								th.os.writeUTF(dao.getIcon(id));
							}
						}
					}
					/*
					 * 进入游戏seat，实际上是另一条线程的新加入
					 */
					if (s.equals("enterseat")) {
						location = "seat";// 修改位置，说明这是一条新的线程
						id = is.readUTF();
						String tn = is.readUTF();
						String sn = is.readUTF();
						tablenum = Integer.parseInt(tn);// 得到桌子号
						seatnum = Integer.parseInt(sn);// 得到座位号
						// playerlist加入此线程
						playerlist.add(this);

						/*
						 * 向本桌的其他player发送信息
						 */
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th != this && th.tablenum == this.tablenum
									&& th.location.equals("seat")) {
								// 初始化本桌其他用户信息
								os.writeUTF("initotherplayer");
								os.writeUTF(th.seatnum + "");
								os.writeUTF(th.readyflag + "");
								os.writeUTF(th.id);
								// -------
								th.os.writeUTF("oneplayercomein");
								th.os.writeUTF(this.seatnum + "");
								th.os.writeUTF(this.id + "");
							}
						}
					}

					/*
					 * 收到准备好信息,向本桌其他用户通知此玩家准备好信息
					 */
					if (s.equals("ready")) {
						this.readyflag = true;
						int count = 0;
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th != this && th.tablenum == this.tablenum
									&& th.location.equals("seat")) {
								if (th.readyflag == true)
									count++;
								th.os.writeUTF("oneplayerready");
								th.os.writeUTF(this.seatnum + "");
							}
						}
						// count==3，说明4人都准备好，游戏可以开始了！
						if (count == 3) {
							Randomization();// 牌随机分成4组
							String num = (new Random().nextInt(100) % 4) + "";
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th.tablenum == this.tablenum
										&& th.location.equals("hall")) {
									th.os.writeUTF("Gamestart");
									th.os.writeUTF(tablenum + "");
								}
								if (th.tablenum == this.tablenum
										&& th.location.equals("seat")) {
									th.os.writeUTF("gamestart");
									String cards = "";
									for (int j = 0; j < 13; j++)
										cards += player[th.seatnum][j] + "#";
									th.os.writeUTF(cards);
									// 随机发出最先出牌的玩家
									th.os.writeUTF(num);
								}
							}
						}
					}
					/*
					 * 游戏信息,最主要的信息传递
					 */

					if (s.equals("gameinfo")) {
						String type = is.readUTF();// 不出、出牌或超时
						if (type.equals("buchu")) {
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th != this && th.tablenum == this.tablenum
										&& th.location.equals("seat")) {
									th.os.writeUTF("oneplayerbuchu");
									th.os.writeUTF(this.seatnum + "");
								}
							}
						} else if (type.equals("chaoshi")) {
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th != this && th.tablenum == this.tablenum
										&& th.location.equals("seat")) {
									th.os.writeUTF("oneplayerchaoshi");
									th.os.writeUTF(this.seatnum + "");
								}
							}
						} else if (type.equals("chupai")) {
							String info = is.readUTF();
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								if (th != this && th.tablenum == this.tablenum
										&& th.location.equals("seat")) {
									th.os.writeUTF("oneplayerchupai");
									th.os.writeUTF(this.seatnum + "");
									th.os.writeUTF(info);
								}
							}
						}

					}

					if (s.equals("oneplayerwin")) {
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th != this && th.tablenum == this.tablenum
									&& th.location.equals("seat")) {
								dao.updateScore(th.id, -5);
								th.os.writeUTF("youfailed");
								th.os.writeUTF(this.id + "");
							}
							if (th == this) {
								os.writeUTF("youwin");
								dao.updateScore(id, 15);
							}
						}
					}
					/*
					 * 游戏交谈
					 */
					if (s.equals("gametalking")) {
						String text = is.readUTF();
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th != this && th.tablenum == this.tablenum
									&& th.location.equals("seat")) {
								th.os.writeUTF("gametalking");
								th.os.writeUTF(this.id + "");
								th.os.writeUTF(text);
							}
						}
					}

					/*
					 * 退出seat信息
					 */
					if (s.equals("exitseat")) {
						String tn = is.readUTF();
						String sn = is.readUTF();
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th != this && th.location.equals("hall")) {
								th.os.writeUTF("oneplayerexitseat");
								th.os.writeUTF(tn);
								th.os.writeUTF(sn);
							}
							if ((th.id).equals(id)
									&& th.location.equals("hall")) {
								th.os.writeUTF("youcanstartagain");
							}
							if (th != this && th.tablenum == this.tablenum
									&& th.location.equals("seat")) {
								th.os.writeUTF("oneplayerexit");
								th.os.writeUTF(seatnum + "");
								th.os.writeUTF(id);
								th.readyflag = false;
							}
						}
						// playerlist链中移除
						playerlist.remove(this);
						break;
					}
					/*
					 * 退出大厅信息
					 */
					if (s.equals("exit")) {
						s = is.readUTF();
						for (int i = 0; i < playerlist.size(); i++) {
							ServerThread th = playerlist.get(i);
							if (th != this) {
								th.os.writeUTF("someoneexit");
								th.os.writeUTF(id);
							}
						}
						// 把自己从playerlist去除
						playerlist.remove(this);
						// 服务器记录日志
						dao.setState("0", id);
						nowtime = tempDate.format(new java.util.Date());
						text.append(nowtime + "\n用户：" + id + "\nIP地址："
								+ youraddress.toString() + " 离开了房间\n");
						this.stop();
					}

					// END
				} catch (IOException e) {
					try {
						socket.close();
					} catch (IOException e1) {
					}
					// dao.setState("0", id);
					// nowtime = tempDate.format(new java.util.Date());
					// text.append(nowtime + "\n用户：" + id + "\nIP地址："
					// + youraddress.toString() + " 离开了房间\n");
					break;
				}
			}
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		int result = JOptionPane.showConfirmDialog(this, "确定退出？", "提示",
				JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			dao.setAllState("0");
			System.exit(0);
		}
	}

	public static void main(String[] args) {
		new Server();
	}
}
