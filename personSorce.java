package game;

public class personSorce {
	private String usid;
	private int score;
	
	
	personSorce(String usid,int score){
		this.usid=usid;
		this.score=score;
	}
	
	public String getUsid() {
		return usid;
	}

	public void setUsid(String usid) {
		this.usid = usid;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}
	
}
