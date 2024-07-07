package member.model;

import java.util.Date;

public class Member {
	private String id;
	private String name;
	private String password;
	private Date regDate;
	
	public Member(String id, String name, String password, Date regDate) {
		this.id=id;
		this.name=name;
		this.password=password;
		this.regDate=regDate;
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public String getPassword() {
		return password;
	}
	
	public Date getRegDate() {
		return regDate;
	}
	
	public boolean matchPassword(String pwd) {
		return password.equals(pwd);	//암호가 password와 맞는지 확인
	}
	
	public void changePassword(String newPwd) {
		this.password = newPwd;			//새로운 암호로 password를 변경
	}
	
}
