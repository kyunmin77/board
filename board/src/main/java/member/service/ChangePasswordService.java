package member.service;

import java.sql.Connection;
import java.sql.SQLException;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import member.dao.MemberDao;
import member.model.Member;

public class ChangePasswordService {
	private MemberDao memberDao = new MemberDao();
	
	// 비밀번호를 바꾸는 매서드
	public void changePassword(String userId, String curPwd, String newPwd) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			// 트랜잭션 시작
			conn.setAutoCommit(false);
			
			Member member = memberDao.selectById(conn, userId);
			if(member == null) {
				throw new MemberNotFoundException();
			}
			// 비밀번호가 현재 비밀번호와 안맞으면 InvalidPasswordExceptio
			if(!member.matchPassword(curPwd)) {
				throw new InvalidPasswordException();
			}
			// member의 비밀번호를 바꾸고 DB를 업데이트
			member.changePassword(newPwd);
			memberDao.update(conn, member);
			conn.commit();
			// 트랜잭션 끝
		}catch(SQLException e) {
			JdbcUtil.close(conn);
			throw new RuntimeException(e);
		}finally {
			JdbcUtil.close(conn);
		}
	}
}
