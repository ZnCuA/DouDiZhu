
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
public class testCard {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//定义一个数组存储牌类
		ArrayList<String> poker = new ArrayList();
		// 花色
		String[] color = { "c", "d", "s", "h" };

		// 牌数字
		String[] num = { "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13",
				"14", "15" };
		
		//将大王和小王存储到poker里面
		poker.add("r"+"17");
		poker.add("b"+"16");
		poker.add("r"+"17");
		poker.add("b"+"16");
		//使用强循环装载牌
		for(String colors:color) {
			for(String number:num) {
				poker.add(colors+number);
				poker.add(colors+number);
			}
		}
		/*
		 * 2.洗牌
		 * 使用集合的工具类collections中的方法
		 * static void shuffle(list<> list)使用默认随机源对指定列表进行置换
		 */
		Collections.shuffle(poker);
		System.out.println(poker);


	// 随机分成的四组
			String[][] player = new String[4][25];
			//定义dipai
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


