package game;

import java.awt.Color;
import java.awt.Point;
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
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Gamearea extends JFrame implements MouseListener, Runnable,
		ActionListener {
	PanelArea allarea, gamearea; // 总体区域，游戏区域

	JLabel exit;

	JTextArea receive; // 接收信息区域

	JTextField send; // 发送信息框

	JLabel[] playerinfo = new JLabel[4];

	JLabel[] leftcardnum = new JLabel[4]; // 剩余张数

	int myleftcard = 13;

	JLabel timearea; // 时间显示区域

	JLabel[] buchuarea = new JLabel[4]; // 不出

	JLabel[] clock = new JLabel[4]; // 时钟,一共设置四个

	JLabel[] role = new JLabel[4]; // 角色区域

	JButton start, chupai, buchu; // 开始、出牌、提示按钮

	JLabel[] reready = new JLabel[4];

	JLabel[] ready = new JLabel[4];

	JLabel[] rolefropic = new JLabel[4];

	Card[] card = new Card[14];

	Card[][] cardfront = new Card[4][13]; // player出的牌

	Startanimation startanimation;

	Timepass timepass;

	boolean[] cardclick = new boolean[14]; // 是否已点击牌

	boolean[] cardsend = new boolean[14]; // 牌是否出了

	int firstchupai = -1;// 先出牌的seat号

	String[] CARDS = new String[13]; // 用于接收13张牌

	Point[] endpos = new Point[14];

	Point[] startpos = new Point[14];

	boolean startflag = false; // 游戏是否开始

	boolean Isnext = false; // 是否是下一个人出牌了，在自己点击出牌或不出的时候，Isnext置true

	boolean Isfirst = true; // 是否是刚进来的第一盘

	String playerID = null; // 自己的ID

	String[] cardinfo = new String[13]; // 记录上一个出牌player的牌信息

	String[] mycard = new String[13]; // 记录自己出牌信息

	String[] myoldcard = new String[13]; // 记录自己前一论出的牌

	int tablenum, seatnum;// 桌子号，seat号

	Socket socket = null;

	String serverIP = null;

	DataInputStream is = null;

	DataOutputStream os = null;

	public Gamearea(String _playerID, String _serverIP, int _tn, int _sn) {
		playerID = _playerID;
		serverIP = _serverIP;
		tablenum = _tn;
		seatnum = _sn;
		try {
			socket = new Socket(serverIP, 6666);
			is = new DataInputStream(socket.getInputStream());
			os = new DataOutputStream(socket.getOutputStream());
			os.writeUTF("enterseat");
			os.writeUTF(playerID);
			os.writeUTF(tablenum + "");
			os.writeUTF(seatnum + "");
		} catch (IOException e) {
		}
		for (int i = 0; i < 13; i++) {
			cardinfo[i] = "";
			mycard[i] = "";
			myoldcard[i] = "";
		}
		initGUI();
		startanimation = new Startanimation();
		timepass = new Timepass();
		new Thread(this).start();
	}

	/*
	 * 初始化GUI
	 */
	public void initGUI() {
		setTitle("Rush");
		setIconImage(new ImageIcon("pics\\rush.png").getImage());
		setSize(940, 700);
		setLocationRelativeTo(null);
		setVisible(true);
		setResizable(false);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new ExitWindow());

		allarea = new PanelArea();
		allarea.setImageFile(new File("pics\\bgxx.png"));
		add(allarea);
		allarea.setLayout(null);
		gamearea = new PanelArea();
		gamearea.setImageFile(new File("pics\\bgx.png"));
		gamearea.setBounds(0, 0, 730, 680);
		allarea.add(gamearea);

		gamearea.setLayout(null);
		start = new JButton();
		start.setIcon(new ImageIcon("pics\\button\\startbutton.png"));
		start.setBounds(330, 570, 66, 34);
		start.setContentAreaFilled(false);
		start.setBorderPainted(false);
		start.addMouseListener((MouseListener) this);
		/*
		 * three buttons:chupai、buchu、tishi
		 */
		chupai = new JButton();
		chupai.setBounds(330, 450, 55, 22);
		chupai.setIcon(new ImageIcon("pics\\button\\出牌.png"));
		chupai.setContentAreaFilled(false);
		chupai.setBorderPainted(false);
		chupai.addMouseListener((MouseListener) this);
		buchu = new JButton();
		buchu.setBounds(400, 450, 55, 22);
		buchu.setIcon(new ImageIcon("pics\\button\\不出.png"));
		buchu.setContentAreaFilled(false);
		buchu.setBorderPainted(false);
		buchu.addMouseListener((MouseListener) this);
		/*
		tishi = new JButton();
		tishi.setBounds(420, 450, 55, 22);
		tishi.setIcon(new ImageIcon("pics\\button\\提示.png"));
		tishi.setContentAreaFilled(false);
		tishi.setBorderPainted(false);
		tishi.addMouseListener((MouseListener) this);
		*/
		/*
		 * hide the three buttons first
		 */
		chupai.setVisible(false);
		buchu.setVisible(false);
		gamearea.add(start);
		gamearea.add(chupai);
		gamearea.add(buchu);
		for (int i = 0; i < 4; i++) {
			role[i] = new JLabel();
			role[i].setIcon(new ImageIcon("pics\\role\\rolebg_x.png"));
			gamearea.add(role[i]);
			role[i].setLayout(null);
			rolefropic[i] = new JLabel();
			rolefropic[i].setBounds(-5, -20, 130, 210);
			ready[i] = new JLabel();
			ready[i].setBounds(65, 135, 100, 40);
			playerinfo[i] = new JLabel();
			playerinfo[i].setBounds(30, 160, 100, 30);
			playerinfo[i].setForeground(Color.white);
			leftcardnum[i] = new JLabel();
			leftcardnum[i].setBounds(30, 190, 100, 20);
			leftcardnum[i].setForeground(Color.white);
			leftcardnum[i].setVisible(false);
			role[i].add(ready[i]);
			role[i].add(rolefropic[i]);
			role[i].add(playerinfo[i]);
			role[i].add(leftcardnum[i]);
		}
		role[seatnum].setBounds(300, 350, 130, 210);
		role[(seatnum + 1) % 4].setBounds(500, 190, 130, 210);
		role[(seatnum + 2) % 4].setBounds(300, 30, 130, 210);
		role[(seatnum + 3) % 4].setBounds(100, 190, 130, 210);
		rolefropic[seatnum].setIcon(new ImageIcon("pics//role//boyshow2.gif"));
		playerinfo[seatnum].setText("用户ID:" + playerID);

		for (int i = 0; i < 4; i++) {
			reready[i] = new JLabel();
			gamearea.add(reready[i]);
		}
		reready[seatnum].setBounds(330, 420, 66, 34);
		reready[(seatnum + 1) % 4].setBounds(550, 250, 66, 34);
		reready[(seatnum + 2) % 4].setBounds(330, 200, 66, 34);
		reready[(seatnum + 3) % 4].setBounds(180, 250, 66, 34);

		timearea = new JLabel();
		timearea.setBounds(640, 8, 80, 80);
		gamearea.add(timearea);

		for (int i = 0; i < 4; i++) {
			clock[i] = new JLabel();
			clock[i].setVisible(false);
			clock[i].setIcon(new ImageIcon("pics//time//clock.png"));
			gamearea.add(clock[i]);
		}
		clock[seatnum].setBounds(145, 400, 40, 43);
		clock[(seatnum + 1) % 4].setBounds(645, 140, 40, 43);
		clock[(seatnum + 2) % 4].setBounds(150, 20, 40, 43);
		clock[(seatnum + 3) % 4].setBounds(45, 140, 40, 43);

		/*
		 * 三个player出的牌
		 */
		for (int i = 0; i < 13; i++) {
			cardfront[(seatnum + 1) % 4][12 - i] = new Card();
			cardfront[(seatnum + 1) % 4][12 - i].setVisible(false);
			cardfront[(seatnum + 1) % 4][12 - i].setBounds(570 - i * 15, 250,
					65, 100);
			gamearea.add(cardfront[(seatnum + 1) % 4][12 - i]);
			cardfront[(seatnum + 1) % 4][12 - i].addMouseListener(this);
		}
		for (int i = 0; i < 13; i++) {
			cardfront[(seatnum + 2) % 4][12 - i] = new Card();
			cardfront[(seatnum + 2) % 4][12 - i].setVisible(false);
			cardfront[(seatnum + 2) % 4][12 - i].setBounds(440 - i * 15, 140,
					65, 100);
			gamearea.add(cardfront[(seatnum + 2) % 4][12 - i]);
			cardfront[(seatnum + 2) % 4][12 - i].addMouseListener(this);
		}
		for (int i = 0; i < 13; i++) {
			cardfront[(seatnum + 3) % 4][12 - i] = new Card();
			cardfront[(seatnum + 3) % 4][12 - i].setVisible(false);
			cardfront[(seatnum + 3) % 4][12 - i].setBounds(280 - i * 15, 245,
					65, 100);
			gamearea.add(cardfront[(seatnum + 3) % 4][12 - i]);
			cardfront[(seatnum + 2) % 4][12 - i].addMouseListener(this);
		}

		for (int i = 0; i < 4; i++) {
			buchuarea[i] = new JLabel();
			buchuarea[i].setIcon(new ImageIcon("pics//不出.png"));
			buchuarea[i].setVisible(false);
			gamearea.add(buchuarea[i]);
		}
		buchuarea[seatnum].setBounds(330, 420, 90, 40);
		buchuarea[(seatnum + 1) % 4].setBounds(550, 250, 90, 40);
		buchuarea[(seatnum + 2) % 4].setBounds(330, 200, 90, 40);
		buchuarea[(seatnum + 3) % 4].setBounds(180, 250, 90, 40);

		receive = new JTextArea();
		receive.setBounds(735, 300, 190, 300);
		receive.setLineWrap(true);// 激活自动换行功能
		receive.setWrapStyleWord(true);// 激活断行不断字功能
		receive.setEditable(false);
		receive.setOpaque(false);
		receive.setForeground(Color.black);
		receive.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(255, 0, 51)));
		allarea.add(receive);

		send = new JTextField();
		send.setBounds(735, 610, 190, 25);
		send.setOpaque(false);
		send.setForeground(Color.black);
		send.setBorder(javax.swing.BorderFactory
				.createLineBorder(new java.awt.Color(0, 0, 0)));
		send.addActionListener(this);
		allarea.add(send);

		exit = new JLabel();
		exit.setBounds(785, 10, 100, 100);
		exit.setIcon(new ImageIcon("pics//sets.png"));
		exit.addMouseListener(this);
		allarea.add(exit);
	}

	/*
	 * 三家的出牌全清空
	 */
	public void hideAllcard() {
		hidefrontcard((seatnum + 1) % 4);
		hidefrontcard((seatnum + 2) % 4);
		hidefrontcard((seatnum + 3) % 4);
		hideDown();
	}

	/*
	 * 清空某一玩家的牌
	 */
	public void hidefrontcard(int sn) {
		for (int i = 0; i < 13; i++) {
			cardfront[sn][i].setVisible(false);
		}
	}

	/*
	 * 清空自己玩家的牌
	 */
	public void hideDown() {
		for (int i = 1; i <= 13; i++) {
			if (cardsend[i]) {
				card[i].setVisible(false);
				repaint();
			}
			repaint();
		}
	}

	/*
	 * 关闭窗口向服务器发送离开seat信息
	 */
	class ExitWindow extends WindowAdapter {
		public void windowClosing(WindowEvent e) {
			sureExit();
		}
	}

	public void sureExit() {
		if(startflag==false){
			try {
				os.writeUTF("exitseat");
				os.writeUTF(tablenum + "");
				os.writeUTF(seatnum + "");
			} catch (IOException e1) {
			}
			this.dispose();
			return;
		}
		int result = JOptionPane.showConfirmDialog(this, "游戏正在进行,确定退出？",
				"提示", JOptionPane.YES_NO_OPTION);
		if (result == JOptionPane.YES_OPTION) {
			try {
				os.writeUTF("exitseat");
				os.writeUTF(tablenum + "");
				os.writeUTF(seatnum + "");
			} catch (IOException e1) {
			}
			this.dispose();
		}
	}

	public void initcard() {
		for (int i = 13; i >= 1; i--) {
			card[i] = new Card();
			gamearea.add(card[i]);
			card[i].setVisible(false);
			card[i].setBounds(210 + 20 * i, 520, 65, 100);
			card[i].setImage("pics//cards//" + CARDS[13 - i].charAt(0)
					+ CARDS[13 - i].substring(1, CARDS[13 - i].length())
					+ ".gif");
			card[i].priority = Integer.parseInt(CARDS[13 - i].substring(1,
					CARDS[13 - i].length())); // 获得牌的大小
			card[i].cardtype = CARDS[13 - i].charAt(0) + ""; // 获得牌的种类
			card[i].addMouseListener(this);
			cardclick[i] = false;
			cardsend[i] = false;
			startpos[i] = new Point();
			endpos[i] = new Point();
		}
	}

	/*
	 * repaint the left cards
	 */

	public void Repaintleftcard(int cardnum) {
		int temp = cardnum;
		for (int i = 13; i >= 1; i--)
			if (!cardclick[i] && !cardsend[i]) {
				card[i].setBounds(350 + 10 * cardnum - 20 * (cardnum - temp--),
						520, 65, 100);
			}
	}

	public void getPos(int cardnum) {
		int temp = cardnum;
		for (int i = 13; i >= 1; i--)
			if (cardclick[i] && cardsend[i]) {
				startpos[i].x = card[i].getX();
				startpos[i].y = card[i].getY();
				endpos[i].x = 350 + 10 * cardnum - 20 * (cardnum - temp--);
				endpos[i].y = 400;
			}
	}

	/*
	 * 自己的牌
	 */
	public void Cardup(Card card, int num) {
		card.setBounds(card.getX(), card.getY() - num, card.getWidth(), card
				.getHeight());
	}

	/*
	 * 别人的牌
	 */
	public void FrontCardup(JLabel card, int num) {
		card.setBounds(card.getX(), card.getY() - 15, card.getWidth(), card
				.getHeight());
	}

	public void Cardreturn() {
		for (int i = 1; i <= 13; i++)
			if (cardclick[i] && !cardsend[i]) {
				cardclick[i] = false;
				Cardup(card[i], -20);
			}
	}

	/*
	 * mouse click,play sound
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() instanceof JButton) {
			if (e.getSource() == start) {
				Playsound.play("audio//click.wav");
				if (Isfirst == false)
					reready[seatnum]
							.setIcon(new ImageIcon("pics//reready.png"));
				else
					ready[seatnum].setIcon(new ImageIcon("pics//ready.png"));
				start.setVisible(false);
				/*
				 * 发送服务器准备好信息
				 */
				try {
					os.writeUTF("ready");
				} catch (IOException e1) {
				}

			}
			if (e.getSource() == chupai) {
				int k = 0;
				String info = "";
				for (int i = 1; i <= 13; i++) {
					if (cardclick[i] && !cardsend[i]) {
						cardsend[i] = true;
						info += card[i].cardtype + card[i].priority + "#";
						++k;
					}
				}
				mycard = info.split("#");
				if (k == 0)// 没选中任何牌
					return;
				if (Rule.Issame(myoldcard, cardinfo)) {
					if (!Rule.Isregular(mycard)) {
						for (int i = 1; i <= 13; i++) {
							if (cardclick[i] && cardsend[i])
								cardsend[i] = false;
						}
						return;
					}
				} else {
					if (!Rule.Isregular(mycard, cardinfo)) {
						for (int i = 1; i <= 13; i++) {
							if (cardclick[i] && cardsend[i])
								cardsend[i] = false;
						}
						return;
					} else {
						for (int i = 0; i < 4; i++) {
							buchuarea[i].setVisible(false);
						}
					}
				}
				Repaintleftcard(13 - k);
				if (k != 0) {
					getPos(k);
					new Cardanimation(k).start();
				}
				myoldcard = mycard;
				cardinfo = mycard;
				Isnext=true;
				/*
				 * 发送给服务器出的牌
				 */
				try {
					os.writeUTF("gameinfo");
					os.writeUTF("chupai");
					os.writeUTF(info);

				} catch (IOException e1) {
				}
				myleftcard -= mycard.length;

				/*
				 * 如果剩余牌为0,说明自己赢了
				 */
				if (myleftcard == 0)
					try {
						os.writeUTF("oneplayerwin");
					} catch (IOException e1) {
					}
				Isnext = true;
				chupai.setVisible(false);
				buchu.setVisible(false);
			}

			if (e.getSource() == buchu) {
				buchuarea[seatnum].setVisible(true);
				Playsound.play("audio//Man//buyao"
						+ (new Random().nextInt(100) % 4 + 1) + ".wav");
				Cardreturn();

				try {
					os.writeUTF("gameinfo");
					os.writeUTF("buchu");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				Isnext = true;
				hideDown();
				chupai.setVisible(false);
				buchu.setVisible(false);
			}
		}
		if (e.getSource() instanceof Card) {
			for (int i = 1; i <= 13; i++)
				if (e.getSource() == card[i] && !cardsend[i]) {
					if (cardclick[i] == false) {
						cardclick[i] = true;
						Cardup(card[i], 15);
					} else {
						cardclick[i] = false;
						Cardup(card[i], -15);
					}
					break;
				}
		}
		if (e.getSource() == exit)
			sureExit();
	}

	/*
	 * enter the startbutton area
	 */
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() instanceof JButton) {
			if (e.getSource() == start)
				start.setIcon(new ImageIcon("pics\\button\\startentered.png"));
			if (e.getSource() == chupai)
				chupai.setIcon(new ImageIcon("pics\\button\\出牌x.png"));
			if (e.getSource() == buchu)
				buchu.setIcon(new ImageIcon("pics\\button\\不出x.png"));
		}
		if (e.getSource() instanceof Card) {
			for (int i = 1; i <= 13; i++)
				if (e.getSource() == card[i] && cardclick[i] == false) {
					Cardup(card[i], 5);
					break;
				}
			repaint();
		}
		if (e.getSource() == exit) {
			exit.setIcon(new ImageIcon("pics//setn.png"));
		}
	}

	/*
	 * existe the startbutton area
	 */
	public void mouseExited(MouseEvent e) {
		if (e.getSource() instanceof JButton) {
			start.setIcon(new ImageIcon("pics\\button\\startbutton.png"));
			if (e.getSource() == chupai)
				chupai.setIcon(new ImageIcon("pics\\button\\出牌.png"));
			if (e.getSource() == buchu)
				buchu.setIcon(new ImageIcon("pics\\button\\不出.png"));
		}
		if (e.getSource() instanceof Card) {
			for (int i = 1; i <= 13; i++)
				if (e.getSource() == card[i] && cardclick[i] == false) {
					Cardup(card[i], -5);
					break;
				}
			repaint();
		}
		if (e.getSource() == exit) {
			exit.setIcon(new ImageIcon("pics//sets.png"));
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	/*
	 * 右上角时间动画
	 */
	class Timepass extends Thread {
		public void run() {
			while (startflag) {
				clock[firstchupai % 4].setVisible(true);
				Isnext = false;
				for (int i = 30; i > 0; i--) {
					if (startflag == false)
						return;
					timearea
							.setIcon(new ImageIcon("pics//time//" + i + ".png"));
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
					}
					if (Isnext == true) {
						clock[firstchupai % 4].setVisible(false);
						firstchupai++;
						timearea.setIcon(null);
						break;
					}
					// 规定的时间结束,向服务器发出"不出"信号
					if (i == 1) {
						if ((firstchupai % 4) == seatnum) {
							try {
								os.writeUTF("gameinfo");
								os.writeUTF("chaoshi");
								chupai.setVisible(false);
								buchu.setVisible(false);
							} catch (IOException e1) {
								e1.printStackTrace();
							}
						}
						clock[firstchupai % 4].setVisible(false);
						firstchupai++;
					}
				}
			}
			timearea.setIcon(null); // startflag为false说明一局结束，timearea图片置空
		}
	}

	/*
	 * start animation and card layout
	 */
	class Startanimation extends Thread {
		public void run() {
			for (int i = 0; i < 4; i++) {
				role[i].setIcon(null);
				ready[i].setIcon(null);
				reready[i].setIcon(null);
			}
			if (Isfirst == true) {
				for (int i = 0; i < 100; i++) {
					try {
						Thread.sleep(4);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					role[seatnum].setBounds(300 - 2 * i, 350 + i, 130, 210);
					role[(seatnum + 1) % 4].setBounds(500 + i, 190, 130, 210);
					role[(seatnum + 2) % 4].setBounds(270 - i, 30 - i / 3, 130,
							210);
					role[(seatnum + 3) % 4].setBounds(100 - i, 190, 130, 210);
				}
			}
			// 图片一张一张出现
			for (int i = 1; i <= 13; i++) {
				card[i].setVisible(true);
				repaint();
				Playsound.play("audio//give.wav");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			if (firstchupai == seatnum)
				chupai.setVisible(true);
			if (!timepass.isAlive()) {
				timepass = new Timepass();
				timepass.start();
			}
		}
	}

	class Cardanimation extends Thread {
		int cardnum;

		Cardanimation(int _cardnum) {
			cardnum = _cardnum;
		}

		public void run() {
			int f = 0;
			while (++f <= 380) {
				f = f + 4;
				int temp = cardnum;
				for (int i = 13; i >= 1; i--) {
					if (cardclick[i] && cardsend[i]) {
						temp--;
						if (temp < cardnum / 2)
							card[i].setBounds(f, f, 65, 100);
						else
							card[i].setBounds(760 - f, f, 65, 100);
					}
				}
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
				}
			}
			Playsound.play("audio//give.wav");
			for (int i = 13; i >= 1; i--)
				if (cardclick[i]) {
					cardclick[i] = false;
					card[i].setBounds(endpos[i].x, endpos[i].y, 65, 100);
				}
		}
	}

	/*
	 * 接收信息
	 */
	public void run() {
		while (true) {
			try {
				String s = is.readUTF();
				/*
				 * 初始化
				 */
				if (s.equals("initotherplayer")) {
					int sn = Integer.parseInt(is.readUTF());
					String readyflag = is.readUTF();
					String name = is.readUTF();
					rolefropic[sn]
							.setIcon(new ImageIcon("pics//role//boy1.gif"));
					if (readyflag.trim().equals("true"))
						ready[sn].setIcon(new ImageIcon("pics//ready.png"));
					playerinfo[sn].setText("用户ID:" + name);

				}
				/*
				 * 进来一个player
				 */
				if (s.equals("oneplayercomein")) {
					int sn = Integer.parseInt((String) is.readUTF());
					rolefropic[sn]
							.setIcon(new ImageIcon("pics//role//boy1.gif"));
					String name = is.readUTF();
					// if (Isfirst)
					playerinfo[sn].setText("用户ID:" + name);
				}
				/*
				 * 一个player准备好
				 */
				if (s.equals("oneplayerready")) {
					int sn = Integer.parseInt((String) is.readUTF());
					if (Isfirst == true)
						ready[sn].setIcon(new ImageIcon("pics//ready.png"));
					else
						reready[sn].setIcon(new ImageIcon("pics//reready.png"));
				}
				/*
				 * 一个player离开
				 */
				if (s.equals("oneplayerexit")) {
					int sn = Integer.parseInt((String) is.readUTF());
					int exitname = Integer.parseInt((String) is.readUTF());
					rolefropic[sn].setIcon(null);
					playerinfo[sn].setText(null);
					ready[sn].setIcon(null);
					reready[sn].setIcon(null);
					for (int i = 0; i < 4; i++)
						buchuarea[i].setVisible(false);
					hideAllcard();
					for (int i = 0; i < 4; i++){
						clock[i].setVisible(false);
						leftcardnum[i].setVisible(false);
					}
					if (startflag == true) { // 如果本局已经开始
						startflag = false; // 本局结束
						JOptionPane.showMessageDialog(null, "玩家  " + exitname
								+ "  逃跑,本局作废！", "提示",
								JOptionPane.INFORMATION_MESSAGE);
						Isfirst = false; // 此时已经不是第一盘
						timearea.setIcon(null);
						start.setVisible(true);
						chupai.setVisible(false);
						buchu.setVisible(false);
						for (int i = 1; i <= 13; i++)
							card[i].setVisible(false);

						cardinfo = new String[13];
						mycard = new String[13];
						myoldcard = new String[13];
						for (int i = 0; i < 13; i++) {
							cardinfo[i] = "";
							mycard[i] = "";
							myoldcard[i] = "";
						}
						leftcardnum[(seatnum + 1) % 4].setVisible(false);
						leftcardnum[(seatnum + 2) % 4].setVisible(false);
						leftcardnum[(seatnum + 3) % 4].setVisible(false);
					}
					startflag=false;
				}
				/*
				 * 游戏开始
				 */
				if (s.equals("gamestart")) {
					String allcards = is.readUTF();
					firstchupai = Integer.parseInt(is.readUTF());// 最先出牌的seat号
					CARDS = allcards.split("#");
					initcard();
					startflag = true; // 本局开始
					if (!startanimation.isAlive()) {
						startanimation = new Startanimation();
						startanimation.start();
					}
					Playsound.play("audio//start.wav");
					leftcardnum[(seatnum + 1) % 4].setVisible(true);
					leftcardnum[(seatnum + 1) % 4].setText("剩余张数：13");
					leftcardnum[(seatnum + 2) % 4].setVisible(true);
					leftcardnum[(seatnum + 2) % 4].setText("剩余张数：13");
					leftcardnum[(seatnum + 3) % 4].setVisible(true);
					leftcardnum[(seatnum + 3) % 4].setText("剩余张数：13");
				}

				/*
				 * 游戏信息
				 */
				if (s.equals("oneplayerchupai")) {
					int sn = Integer.parseInt(is.readUTF());
					cardinfo = (is.readUTF()).split("#");
					hidefrontcard(sn);
					int length = cardinfo.length;
					int leftnum = Integer.parseInt(leftcardnum[sn].getText()
							.substring(5, leftcardnum[sn].getText().length()));
					leftcardnum[sn].setText("剩余张数：" + (leftnum - length));
					for (int i = 0; i < length; i++) {
						cardfront[sn][(13 - length) / 2 + i]
								.setImage("pics//cards//"
										+ cardinfo[i].charAt(0)
										+ cardinfo[i].substring(1, cardinfo[i]
												.length()) + ".gif");
						cardfront[sn][(13 - length) / 2 + i].setVisible(true);
						repaint();
					}
					buchuarea[sn].setVisible(false);
					Isnext = true;
					if ((sn + 1) % 4 == this.seatnum) {
						hideDown();
						buchuarea[seatnum].setVisible(false);
						chupai.setVisible(true);
						buchu.setVisible(true);
					}
					repaint();
				}
				/*
				 * 不出
				 */
				if (s.equals("oneplayerbuchu")) {
					int sn = Integer.parseInt(is.readUTF());
					buchuarea[sn].setVisible(true);
					hidefrontcard(sn);
					Isnext = true;
					if ((sn + 1) % 4 == this.seatnum) {
						hideDown();
						chupai.setVisible(true);
						buchu.setVisible(true);
						if (Rule.Issame(myoldcard, cardinfo)) {
							buchu.setVisible(false);
						}
					}
					repaint();
				}
				/*
				 * 超时
				 */
				
				/*
				 * 胜利
				 */
				
				/*
				 * 失败
				 */
				

			} catch (IOException e) {
				this.dispose();
			}
		}
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == send) {
			receive.append(playerID + "说: " + send.getText() + "\n");
			try {
				os.writeUTF("gametalking");
				os.writeUTF(send.getText());
			} catch (IOException e1) {
			}
			send.setText("");
		}
	}
	
	
}
