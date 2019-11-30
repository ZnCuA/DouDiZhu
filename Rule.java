package game;

import java.util.Random;

public class Rule {
	/*
	 * �жϳ��ƺ��Լ���һ�δ��������Ϣ�Ƿ���ͬ�� ����ͬ��˵���Լ������������(���Ϲ���)�� ���򣬱�������ϼҴ����
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
	 * �жϳ����Ƿ�Ϸ�
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
			if (duizi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//zhadan.wav");
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
			if (duizi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//zhadan.wav");
			}
			break;
		case 7:
			if (shunzi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//shunzi.wav");
			}
			if (duizi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//zhadan.wav");
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
			if (duizi(y) != 0) {
				result = true;
				Playsound.play("audio//Man//zhadan.wav");
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
			if (feiji(y) != 0) {
				Playsound.play("audio//Man//feiji.wav");
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
		case 15:
			if (feijichibang(y) != 0) {
				Playsound.play("audio//Man//feiji.wav");///û�зɻ���������Ƶ
				result = true;
			}
		}
		return result;
	}

	/*
	 * �ж��Լ������Ƿ�����ϼҵ���
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
		if ((lengthX == 4 || lengthX == 5 || lengthX == 6 || lengthX == 7 || lengthX == 8) && lengthY != 4 && duizi(dataX) != 0) {
			Playsound.play("audio/Man/zhadan.wav");
			return true;
		}
		if(duizi(dataX) != 0 && duizi(dataY) != 0)
		{
			if(lengthX == lengthY && duizi(dataX) > duizi(dataY))
			{
				Playsound.play("audio/Man/zhadan.wav");
				return true;
			}	
			if(lengthX > lengthY)
			{
				Playsound.play("audio/Man/zhadan.wav");
				return true;
			}
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
			if (feiji(dataX) > feiji(dataY))
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
		case 15:
			if (feijichibang(dataX) > feijichibang(dataY))
				result = true;
			break;
		}
		if (result)
			Playsound.play("audio//Man//dani" + new Random().nextInt(100) % 3
					+ ".wav");
		return result;
	}

	/*
	 * AAABBBXXYY�ɻ�,�����Ƶ����ȼ�
	 */
	private static int feiji(int[] y) {
		//XXYYAAABBB
		if (y[0] == y[1] && y[2] == y[3] && y[4] == y[5] && y[5] == y[6]
				&& y[6] == y[7] - 1 && y[7] == y[8] && y[8] == y[9])
			return y[4];
		//XXAAABBBYY
		else if (y[0] == y[1] && y[2] == y[3] && y[3] == y[4]
				&& y[4] == y[5] - 1 && y[5] == y[6] && y[6] == y[7]
				&& y[8] == y[9])
			return y[2];
		//AAABBBXXYY
		else if (y[0] == y[1] && y[1] == y[2] && y[2] == y[3] - 1
				&& y[3] == y[4] && y[4] == y[5] && y[6] == y[7] && y[8] == y[9])
			return y[0];
		return 0;
	}

	/*
	 *AAABBBCCCXXYYZZ�ɻ������(15��)
	 */
	private static int feijichibang(int[] y) {
		//XXYYZZAAABBBCCC
		if(y[0] == y[1] && y[2] == y[3] && y[4] == y[5] && y[6] == y[7] 
				&& y[7] == y[8] && y[8] == y[9] - 1 && y[9] == y[10]
						&& y[10] == y[11] && y[11] == y[12]-1
						&& y[12] == y[13] && y[13] == y[14])
			return y[6];
		//XXYYAAABBBCCCZZ
		else if (y[0] == y[1] && y[2] == y[3] && y[4] == y[5] && y[5] == y[6] 
				&& y[6] == y[7]-1 && y[7] == y[8] && y[8] == y[9]
						&& y[9] == y[10]-1 && y[10] == y[11]
						&& y[11] == y[12] && y[13] == y[14])
			return y[4];
		//XXAAABBBCCCYYZZ
		else if (y[0] == y[1] && y[2] == y[3] && y[3] == y[4] && y[4] == y[5]-1 
				&& y[5] == y[6] && y[6] == y[7] && y[7] == y[8]-1
						&& y[8] == y[9] && y[9] == y[10]
						&& y[11] == y[12] && y[13] == y[14])
			return y[2];
		//AAABBBCCCXXYYZZ
		else if (y[0] == y[1] && y[1] == y[2] && y[2] == y[3]-1 && y[3] == y[4]
				&& y[4] == y[5] && y[5] == y[6]-1 && y[6] == y[7]
						&& y[7] == y[8] && y[9] == y[10]
						&& y[11] == y[12] && y[13] == y[14])
			return y[0];
		return 0;
	}
	/*
	 * n��������
	 */
	private static int sansan(int[] x) {
		for (int i = 0; i < x.length - 2; i = i + 3) {
			if (x[i] != x[i + 1] || x[i + 1] != x[i + 2])
				return 0;
		}
		return x[0];
	}

	/*
	 * ˳��
	 */
	public static int shunzi(int[] x) {
		for (int i = 1; i < x.length; i++) {
			if (x[i - 1] != x[i] + 1)
				return 0;
		}
		return x[0];
	}

	/*
	 * ��ͬ����
	 */
	public static int duizi(int[] x) {
		for (int i = 1; i < x.length; i++) {
			if (x[i - 1] != x[i])
				return 0;
		}
		return x[0];
	}

	/*
	 * ������
	 */
	public static int sandaier(int[] x) {
		if (x[0] == x[1] && x[2] == x[3] && x[3] == x[4])
			return x[2];
		else if (x[0] == x[1] && x[1] == x[2] && x[3] == x[4])
			return x[0];
		return 0;
	}

	/*
	 * n����������
	 */
	public static int liandui(int[] x) {
		for (int i = 0; i < x.length - 1; i = i + 2) {
			if (x[i] != x[i + 1])
				return 0;
		}
		return x[0];
	}
	/*
	 * 
	 */

}
