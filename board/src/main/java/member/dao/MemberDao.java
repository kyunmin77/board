package member.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import jdbc.JdbcUtil;
import member.model.Member;

public class MemberDao {
	public Member selectById(Connection conn, String id)
			throws SQLException{ //해당아이디를 가진 Member형 객체를 반환하는 매서드
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			
			pstmt = conn.prepareStatement("select * from member where memberid = ?");
			pstmt.setString(1, id);				// 쿼리문 만들고 ?에 값 대입
			rs = pstmt.executeQuery();			// rs에 쿼리문 실행결과를 저장
			Member member = null;
			if(rs.next()) {
				// rs에 저장된 정보를 이용하여 member 객체 초기화
				member = new Member(
						rs.getString("memberid"),
						rs.getString("name"),
						rs.getString("password"),
						toDate(rs.getTimestamp("regDate")));
			}
			return member;
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
	
	private Date toDate(Timestamp date) {
		//Timestamp 객체 date가 null이 아니면 Date 객체로 바꾸어 반환 
		return date == null ? null : new Date(date.getTime());
	}
	
	public void insert(Connection conn, Member mem) throws SQLException{
		//Member 객체 정보와 현재시간을 DB에 저장하는 매서드
		try(PreparedStatement pstmt = conn.prepareStatement("insert into member values(?,?,?,?)")){
			pstmt.setString(1, mem.getId());
			pstmt.setString(2, mem.getName());
			pstmt.setString(3, mem.getPassword());
			pstmt.setTimestamp(4, new Timestamp(mem.getRegDate().getTime()));
			pstmt.executeUpdate();
		}
	}
	
	public void update(Connection conn, Member member) throws SQLException{
		//Member 객체의 이름과 비밀번호를 수정하는 매서드
		try(PreparedStatement pstmt = conn.prepareStatement(
				"update member set name = ?, password = ? where memberid = ?")){
			pstmt.setString(1, member.getName());
			pstmt.setString(2, member.getPassword());
			pstmt.setString(3, member.getId());
			pstmt.executeUpdate();
		}
	}
}
