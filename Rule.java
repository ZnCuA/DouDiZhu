package game;

import java.util.Random;

public class Rule {
	/*
	 * 判断出牌和自己上一次储存的牌信息是否相同， 若相同则说明自己可以任意出牌(符合规则)， 否则，必须出比上家大的牌
	 */
	public static boolean Issame(String[] x, String[] y) {
		boolean result = true;
		if (x.length == y.length)
			for (int i = 0; i < x.length; i++) {
				if (!x[i].equals(y[i]))
					result = false;
			}
		else
			result = false;
		return result;
	}

	/*
	 * 判断出牌是否合法
	 */
	public static boolean Isregular(String[] x) {
		boolean result = false;
		int length = x.length;
		int[] y = new int[length];
		for (int i = 0; i < length; i++) {
			y[i] = Integer.parseInt(x[i].substring(1, x[i].length()));
		}
		switch (length) {
		case 1:
			if (duizi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//" + duizi(y) + ".wav");
			}
			break;
		case 2:
			if (duizi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//dui" + duizi(y) + ".wav");
			}
			break;
		case 3:
			if (duizi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//sange.wav");
			}
			break;
		case 4:
			if (duizi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//zhadan.wav");
			}
			break;
		case 5:
			if (shunzi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//shunzi.wav");
			}
			if (sandaier(y) != 0) {
				result = true;
				Playsound.play("audio//Man//sandaier.wav");
			}
			break;
		case 6:
			if (shunzi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//shunzi.wav");
			}
			if (sansan(y) != 0) {
				result = true;
				Playsound.play("audio//Man//sange.wav");
			}
			if (liandui(y) != 0) {
				result = true;
				Playsound.play("audio//Man//liandui.wav");
			}
			break;
		case 7:
			if (shunzi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//shunzi.wav");
			}
			break;
		case 8:
			if (shunzi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//shunzi.wav");
			}
			if (liandui(y) != 0) {
				result = true;
				Playsound.play("audio//Man//liandui.wav");
			}
			break;
		case 9:
			if (shunzi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//shunzi.wav");
			}
			if (sansan(y) != 0)
				result = true;
			break;
		case 10:
			if (shunzi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//shunzi.wav");
			}
			if (sansanerer(y) != 0) {
				result = true;

			}
			if (liandui(y) != 0) {
				result = true;
				Playsound.play("audio//Man//liandui.wav");
			}
			break;
		case 11:
			if (shunzi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//shunzi.wav");
			}
			break;
		case 12:
			if (shunzi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//shunzi.wav");
			}
			if (sansan(y) != 0) {
				result = true;
				Playsound.play("audio//Man//sange.wav");
			}
			if (liandui(y) != 0) {
				result = true;
				Playsound.play("audio//Man//liandui.wav");
			}
			break;
		case 13:
			if (shunzi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//shunzi.wav");
			}
			break;
		}
		return result;
	}

	/*
	 * 判断自己的盘是否大于上家的牌
	 */
	public static boolean Isregular(String[] x, String[] y) {
		boolean result = false;
		int lengthX = x.length;
		int lengthY = y.length;

		if (lengthX != lengthY && lengthX != 4)
			return false;

		int[] dataX = new int[lengthX];
		int[] dataY = new int[lengthY];
		for (int i = 0; i < lengthX; i++) {
			dataX[i] = Integer.parseInt(x[i].substring(1, x[i].length()));
			dataY[i] = Integer.parseInt(y[i].substring(1, y[i].length()));
		}
		if (lengthX == 4 && lengthY != 4 && duizi(dataX) != 0) {
			Playsound.play("audio/Man/zhadan.wav");
			return true;
		}
		switch (lengthX) {
		case 1:
			if (duizi(dataX) > duizi(dataY)) {
				result = true;
			}
			break;
		case 2:
			if (duizi(dataX) > duizi(dataY))
				result = true;
			break;
		case 3:
			if (duizi(dataX) > duizi(dataY))
				result = true;
			break;
		case 4:
			if (duizi(dataX) > duizi(dataY))
				result = true;
			break;
		case 5:
			if (shunzi(dataX) > shunzi(dataY))
				result = true;
			if (sandaier(dataX) > sandaier(dataY))
				result = true;
			break;
		case 6:
			if (shunzi(dataX) > shunzi(dataY))
				result = true;
			if (sansan(dataX) > sansan(dataY))
				result = true;
			if (liandui(dataX) > liandui(dataY))
				result = true;
			break;
		case 7:
			if (shunzi(dataX) > shunzi(dataY))
				result = true;
			break;
		case 8:
			if (shunzi(dataX) > shunzi(dataY))
				result = true;
			if (liandui(dataX) > liandui(dataY))
				result = true;
			break;
		case 9:
			if (shunzi(dataX) > shunzi(dataY))
				result = true;
			if (sansan(dataX) > sansan(dataY))
				result = true;
			break;
		case 10:
			if (shunzi(dataX) > shunzi(dataY))
				result = true;
			if (sansanerer(dataX) > sansanerer(dataY))
				result = true;
			break;
		case 11:
			if (shunzi(dataX) > shunzi(dataY))
				result = true;
			break;
		case 12:
			if (shunzi(dataX) > shunzi(dataY))
				result = true;
			if (sansan(dataX) > sansan(dataY))
				result = true;
			if (liandui(dataX) > liandui(dataY))
				result = true;
			break;
		case 13:
			if (shunzi(dataX) > shunzi(dataY))
				result = true;
			break;
		}
		if (result)
			Playsound.play("audio//Man//dani" + new Random().nextInt(100) % 3
					+ ".wav");
		return result;
	}

	/*
	 * AAABBBXXYY,返回牌得优先级
	 */
	private static int sansanerer(int[] y) {
		if (y[0] == y[1] && y[2] == y[3] && y[4] == y[5] && y[5] == y[6]
				&& y[6] == y[7] - 1 && y[7] == y[8] && y[8] == y[9])
			return y[4];
		else if (y[0] == y[1] && y[2] == y[3] && y[3] == y[4]
				&& y[4] == y[5] - 1 && y[5] == y[6] && y[6] == y[7]
				&& y[8] == y[9])
			return y[2];
		else if (y[0] == y[1] && y[1] == y[2] && y[2] == y[3] - 1
				&& y[3] == y[4] && y[4] == y[5] && y[6] == y[7] && y[8] == y[9])
			return y[0];
		return 0;
	}

	/*
	 * n个三相连
	 */
	private static int sansan(int[] x) {
		for (int i = 0; i < x.length - 2; i = i + 3) {
			if (x[i] != x[i + 1] || x[i + 1] != x[i + 2])
				return 0;
		}
		return x[0];
	}

	/*
	 * 顺子
	 */
	public static int shunzi(int[] x) {
		for (int i = 1; i < x.length; i++) {
			if (x[i - 1] != x[i] + 1)
				return 0;
		}
		return x[0];
	}

	/*
	 * 相同的牌
	 */
	public static int duizi(int[] x) {
		for (int i = 1; i < x.length; i++) {
			if (x[i - 1] != x[i])
				return 0;
		}
		return x[0];
	}

	/*
	 * 三带二
	 */
	public static int sandaier(int[] x) {
		if (x[0] == x[1] && x[2] == x[3] && x[3] == x[4])
			return x[2];
		else if (x[0] == x[1] && x[1] == x[2] && x[3] == x[4])
			return x[0];
		return 0;
	}

	/*
	 * n个对子相连
	 */
	public static int liandui(int[] x) {
		for (int i = 0; i < x.length - 1; i = i + 2) {
			if (x[i] != x[i + 1])
				return 0;
		}
		return x[0];
	}

}
