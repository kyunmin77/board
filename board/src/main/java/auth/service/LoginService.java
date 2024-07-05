package auth.service;

import java.sql.Connection;
import java.sql.SQLException;

import jdbc.connection.ConnectionProvider;
import member.dao.MemberDao;
import member.model.Member;

public class LoginService {
	private MemberDao memberDao = new MemberDao();
	
	public User login(String id, String password) {
		//아이디와 패스워드를 입력받아 회원이 맞으면 User객체를 반환하는 매서드
		try (Connection conn = ConnectionProvider.getConnection()){
			Member member = memberDao.selectById(conn, id);
			if(member == null) {
				throw new LoginFailException();
			}//해당 아이디의 멤버가 없다면 LoginFailException
			
			if(!member.matchPassword(password)) {
				throw new LoginFailException();
			}// 패스워드가 일치 하지 않으면 LoginFailException
			
			return new User(member.getId(),member.getName());
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}
