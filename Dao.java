package game;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Dao {
	private static String dbURL = "jdbc:mysql://localhost:3306/doudizhu?serverTimezone=UTC";
	private static String driverName = "com.mysql.cj.jdbc.Driver";
	private static String userName = "root";
	private static String userPwd = "123456";
	public static Connection con = null;

	public Dao() {
		try{
			Class.forName(driverName);  //��������
			try {
				con = DriverManager.getConnection(dbURL,userName,userPwd);
			//	System.out.println("success");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}

	/*
	 * ����˺ź������Ƿ���ȷ ����ȷ����״̬��1
	 */
	public String Check(String id, String psw) {
		String info = "0"; // �˺Ż��������
		try {
			String sql = "select * from userinfo where userid=? and password=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, psw);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getString("state").trim().equals("1")) {
					info = "1"; // �ѵ�¼
				} else {
					info = "2"; // �ɹ���¼
				}
			}
			ps.close();
		} catch (SQLException e) {
		}
		return info;
//		return "2";
	}

	/*
	 * ����˺��Ƿ����
	 */
	public boolean Check(String id) {
		try {
			String sql = "select * from userinfo where userid=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id);
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return true;
			ps.close();
		} catch (SQLException e) {
		}
		return false;
//		return true;
	}

	/*
	 * ָ��ĳ��״̬������
	 */
	public void setState(String state, String id) {
		try {
			String sql = "update userinfo set state=? where userid=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, state);
			ps.setString(2, id);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * ������״̬����0��1,һ��Ϊ�رշ�����ʱ��
	 */
	public void setAllState(String state) {
		try {
			String sql = "update userinfo set state=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, state);
			ps.executeUpdate();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateScore(String _id, int _score) {
		try {
			Statement sql = con.createStatement();
			sql.execute("update userinfo set score=score+" + _score
					+ "where userid='" + _id + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public int getScore(String _id) {
		int score = 0;
		try {
			Statement sql = con.createStatement();
			ResultSet rs = sql
					.executeQuery("select score from userinfo where userid='"
							+ _id + "'");
			rs.next();
			score = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return score;
	}

	public String getIcon(String _id) {
		String icon = "";
		try {
			Statement sql = con.createStatement();
			ResultSet rs = sql
					.executeQuery("select icon from userinfo where userid='"
							+ _id + "'");
			rs.next();
			icon = rs.getString(1);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return icon;
	}

	public void Adduser(String id, String psw) {
		try {
			Statement sql = con.createStatement();
			sql.execute("insert into userinfo(userid,password,state,score,icon)values('"
					+ id + "','" + psw + "','0',0,'0')");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
