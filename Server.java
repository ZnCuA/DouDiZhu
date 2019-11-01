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

	Server() {
		setTitle("服务器");
		setSize(400, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ExitWindow());
		init();  //界面显示与获取服务器地址
		validate();//用于刷新
		dao = new Dao();
		startServer(6666);
	}

	class ExitWindow extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			dao.setAllState("0");
			System.exit(0);
		}
	}

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
				//从请求队列中取出一个连接
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
						if (result.equals("2")) {
							/*
							 * 成功登录,发送到客户端信息
							 */
							dao.setState("1", id);
							os.writeUTF("2");


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
					//其余通信逻辑待编写

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
