	/*
	 * 接收信息
	 */
	public void run() {
		while (true) {
			try {
				String s = is.readUTF();
				/*
				 * 超时
				 */
				if (s.equals("oneplayerchaoshi")) {
					int sn = Integer.parseInt(is.readUTF());
					if ((sn + 1) % 4 == this.seatnum) {
						hideDown();
						chupai.setVisible(true);
						buchu.setVisible(true);
					}
					repaint();
				}
				/*
				 * 胜利
				 */
				if (s.equals("youwin")) {
					startflag = false;
					JOptionPane.showMessageDialog(null, "恭喜,您获得胜利！", "提示",
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
				/*
				 * 失败
				 */
				if (s.equals("youfailed")) {
					String name = is.readUTF();
					JOptionPane.showMessageDialog(null,  name + "胜利！您失败了","提示",
							JOptionPane.INFORMATION_MESSAGE);
					startflag = false;
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
				if(s.equals("gametalking")){
					String name=is.readUTF();
					receive.append(name + "说: " + is.readUTF() + "\n");
				}

			} catch (IOException e) {
				this.dispose();
			}
		}
	}