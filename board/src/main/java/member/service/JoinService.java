package member.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;
import member.dao.MemberDao;
import member.model.Member;

public class JoinService {
	private MemberDao memberDao = new MemberDao();
	
	public void join(JoinRequest joinReq) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			// 트랜잭션 시작
			conn.setAutoCommit(false);		
			
			// DB내에 joinReq에서 받아온 id와 동일한 id 정보를 가진 레코드를 찾아보고 member 객체 생성
			Member member = memberDao.selectById(conn, joinReq.getId());
			if(member != null) {			// 해당 객체(레코드) 가 이미 존재한다면
				JdbcUtil.rollback(conn);	// 롤백 수행
				throw new DuplicateIdException();
			}
			
			//joinReq에서 받아온 정보로 DB에 새로운 레코드 저장
			memberDao.insert(conn,
					new Member(
							joinReq.getId(),
							joinReq.getName(),
							joinReq.getPassword(),
							new Date())
							);
			conn.commit();
			// 트랜잭션 끝
		}catch(SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		}finally {
			JdbcUtil.close(conn);
		}
	}
}
