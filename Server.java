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

	// ����߳�
	ArrayList<ServerThread> playerlist = new ArrayList<ServerThread>();

	// ��ɫ
	String[] color = { "c", "d", "s", "h" };

	// ������
	String[] num = { "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
			"14", "15" };

	// ����ֳɵ�����
	String[][] player = new String[4][13];

	Server() {
		setTitle("������");
		setSize(400, 500);
		setLocationRelativeTo(null);
		setResizable(false);
		setVisible(true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ExitWindow());
		init();  //������ʾ���ȡ��������ַ
		validate();//����ˢ��
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
			myaddress = InetAddress.getLocalHost().getHostAddress().toString(); // ������IP��ַ
		} catch (UnknownHostException e) {
		}
		text.setText("����������...(IP��ַ: " + myaddress + ")\n");
		text.setEditable(false);
		add(text, BorderLayout.CENTER);
		JScrollPane scroll = new JScrollPane(text);
		add(scroll, BorderLayout.CENTER);
		close = new JButton("�رշ�����");
		add(close, BorderLayout.SOUTH);
		close.addActionListener(this);
	}

	/*
	 * ����ֳ������Ʒַ������
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
			JOptionPane.showMessageDialog(null, "��������������", "��ʾ",
					JOptionPane.WARNING_MESSAGE);
			System.exit(0);
		}
		while (true) {
			try {
				//�����������ȡ��һ������
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

		String id = null, psw = null; // �û���Ϣ

		int tablenum; // �û������table��

		int seatnum; // table��seat��

		/*
		 * ���̵߳�λ������,"hall"��ʾ�����̣߳�"seat"��ʾseat�߳�,
		 * һ��player�յ�½ʱ����һ���̣߳�����socket���ܹ�������
		 * ����seat����̺߳͸ոս���������̱߳�־�������location��
		 */
		String location = null;

		boolean readyflag = false; // ��Ϸ�Ƿ�׼����

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
					 * ��¼��Ϣ
					 */
					if (s.startsWith("login")) {
						id = is.readUTF();
						psw = is.readUTF();
						String result = dao.Check(id, psw);
						if (result.equals("2")) {
							/*
							 * �ɹ���¼,���͵��ͻ�����Ϣ
							 */
							dao.setState("1", id);
							os.writeUTF("2");


						} else if (result.equals("1")) {
							os.writeUTF("1"); // �ظ���¼
							break;
						} else {
							os.writeUTF("0"); // ����
							break;
						}
					}
					/*
					 * ע����Ϣ
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
					//����ͨ���߼�����д

					// END
				} catch (IOException e) {
					try {
						socket.close();
					} catch (IOException e1) {
					}
					// dao.setState("0", id);
					// nowtime = tempDate.format(new java.util.Date());
					// text.append(nowtime + "\n�û���" + id + "\nIP��ַ��"
					// + youraddress.toString() + " �뿪�˷���\n");
					break;
				}
			}
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		int result = JOptionPane.showConfirmDialog(this, "ȷ���˳���", "��ʾ",
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
