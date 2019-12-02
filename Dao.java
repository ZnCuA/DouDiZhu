package game;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import game.Server.ServerThread;

public class Dao {
	
//	public static String driverName = "com.microsoft.sqlserver.jdbc.SQLServerDriver"; // 加载JDBC驱动
//
//	public static String dbURL = "jdbc:sqlserver://GAODONG-PC:1433; DatabaseName=RUSH"; // 连接服务器和数据库
//
//	public static String userName = "sa"; // 默认用户名
//
//	public static String userPwd = "123456"; // 密码
	
	private static String dbURL = "jdbc:mysql://localhost:3306/doudizhu?serverTimezone=UTC";
	private static String driverName = "com.mysql.cj.jdbc.Driver";
	private static String userName = "root";
	private static String userPwd = "";

	public static Connection con = null;

	public Dao() {
		try {
			if (con == null) {
				Class.forName(driverName);
				con = DriverManager.getConnection(dbURL, userName, userPwd);
			}
		} catch (Exception e) {
		}
	}

	/*
	 * 检查账号和密码是否正确 若正确，正状态置1
	 */
	public String Check(String id, String psw) {
		String info = "0"; // 账号或密码错误
		try {
			String sql = "select * from userinfo where userid=? and password=?";
			PreparedStatement ps = con.prepareStatement(sql);
			ps.setString(1, id);
			ps.setString(2, psw);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				if (rs.getString("state").trim().equals("1")) {
					info = "1"; // 已登录
				} else {
					info = "2"; // 成功登录
				}
			}
			ps.close();
		} catch (SQLException e) {
		}
		return info;

	}

	/*
	 * 检查账号是否存在
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

	}

	/*
	 * 指定某人状态均置零
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
	 * 所有人状态均置0或1,一般为关闭服务器时候
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
	
	public ArrayList Get() {
		ArrayList<personSorce> scorequeue = new ArrayList<personSorce>();
		try {
			Statement sql = con.createStatement();
			String str="select userid,score from userinfo order by score desc";
			ResultSet rs = sql.executeQuery(str);
			while(rs.next()) {
				personSorce e =new personSorce(rs.getString("userid"),rs.getInt("score"));
				scorequeue.add(e);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return scorequeue;
	}
//	public static void main(String[] args) {
//		Dao d= new Dao();
//		String score="";
//		ArrayList<personSorce> scorequeue=d.Get();
//		for (Iterator<personSorce> list = scorequeue.iterator(); list.hasNext();) {
//			score += list.next().getScore();
//			score += "#";
//			System.out.println(score);
//	    }
//	}
}
