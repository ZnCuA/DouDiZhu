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


	// 存放线程
	ArrayList<ServerThread> playerlist = new ArrayList<ServerThread>();
	
	Server() {
		setTitle("服务器");
		setSize(400, 500);
		setLocationRelativeTo(null);//设置窗口相对于该面板的位置（面板不动窗口动），null表示面板显示与窗口中间
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ExitWindow());
		init();
		validate();//对界面布局进行验证
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
	
	public void startServer(int port) {
		try {
			server = new ServerSocket(port);  //创建server
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "服务器已启动！", "提示",
					JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		while (true) {
			try {
				you = server.accept();  //端口监听客户端信息
				youraddress = you.getInetAddress();
			} catch (IOException e) {
			}
			if (you != null) {  //倘若存在客户端向服务器发送请求，那么创建线程
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
						String result = dao.Check(id, psw);  //检查账号密码是否正确
						if (result.equals("2")) {  //与数据库匹配成功
							/*
							 * 成功登录,发送到客户端信息
							 */
							dao.setState("1", id);   //修改当前用户状态
							os.writeUTF("2");   //将信息传递给客户端
							
							/*
							 * 登陆后将自己加入到playerlist链表中，并向其他玩家发送进入房间信息
							 */
							playerlist.add(this);
							for (int i = 0; i < playerlist.size(); i++) {
								ServerThread th = playerlist.get(i);
								th.os.writeUTF("Newcomer");
								th.os.writeUTF(id);
							}

							// 服务器记录日志更新
							nowtime = tempDate.format(new java.util.Date());
							text.append(nowtime + "\n用户：" + id + "\nIP地址："
									+ youraddress.toString() + " 进入了房间\n");
									
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

						} else if (result.equals("1")) {
							os.writeUTF("1"); // 重复登录
							break;
						} else {
							os.writeUTF("0"); // 错误
							break;
						}
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
					
					
					/*
					 * 游戏信息,最主要的信息传递
					 */

					
					
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
					 * 退出seat信息 // playerlist链中移除
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
					
				} catch (IOException e) {
					try {
						socket.close();
					} catch (IOException e1) {
					}
					break;
				}
			}
		}
	}
	
	
}