
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
public class testCard {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//����һ������洢����
		ArrayList<String> poker = new ArrayList();
		// ��ɫ
		String[] color = { "c", "d", "s", "h" };

		// ������
		String[] num = { "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
				"14", "15" };
		
		//��������С���洢��poker����
		poker.add("r"+"17");
		poker.add("b"+"16");
		poker.add("r"+"17");
		poker.add("b"+"16");
		//ʹ��ǿѭ��װ����
		for(String colors:color) {
			for(String number:num) {
				poker.add(colors+number);
				poker.add(colors+number);
			}
		}
		/*
		 * 2.ϴ��
		 * ʹ�ü��ϵĹ�����collections�еķ���
		 * static void shuffle(list<> list)ʹ��Ĭ�����Դ��ָ���б�����û�
		 */
		Collections.shuffle(poker);
		System.out.println(poker);


	// ����ֳɵ�����
			String[][] player = new String[4][25];
			//����dipai
			ArrayList<String> dipai = new ArrayList();
	
	
			for (int i = 0; i < 5; i++) {
				if(i<4) {
				for (int j = 0; j < 25; j++) {
					/*
					if(i==0) {
						String p=poker.get(j);
						player[i][j] = p;
					}else if(i==1){
						String p=poker.get(j+25);
						player[i][j] = p;
					}else if(i==2) {
						String p=poker.get(j+50);
						player[i][j] = p;
					}else if(i==3) {
						String p=poker.get(j+75);
						player[i][j] = p;
					}*/
					String p=poker.get(i*25+j);
					player[i][j] = p;
				}	
				}else {
					for(int m = 0; m <8; m++) {
						String p=poker.get(m+i*25);
						dipai.add(p);
					}
				}
			}
			
			
	 System.out.println("b:"+Arrays.deepToString(player));
	 System.out.println(dipai);
	}
}


