package article.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import article.model.ArticleContent;
import jdbc.JdbcUtil;

public class ArticleContentDao {
	
	public ArticleContent insert(Connection conn, ArticleContent content) throws SQLException {
		PreparedStatement pstmt = null;
		try {
			//ArticleContent 객체의 변수를 테이블에 저장
			pstmt = conn.prepareStatement("insert into article_content "
					+"(article_no, content) values (?,?)");
			pstmt.setLong(1, content.getNumber());
			pstmt.setString(2, content.getContent());
			int insertedCount = pstmt.executeUpdate();
			// 쿼리를 실행하고 영향을 받은 레코드 수를 반환받음
			
			if(insertedCount > 0) { // 입력이 정상적으로 수행되었다면
				return content;	// content를 반환
			}else {
				return null;
			}
		}finally {
			JdbcUtil.close(pstmt);
		}
	}
	
	// no와 일치하는 ArticleContent을 반환하는 매서드
	public ArticleContent selectById(Connection conn, int no) throws SQLException{
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = conn.prepareStatement("select * from article_content where article_no = ?");
			pstmt.setInt(1, no);
			rs = pstmt.executeQuery();
			// 해당 번호의 article_content 레코드를 rs에 저장
			
			ArticleContent content = null;
			if(rs.next()) {
				content = new ArticleContent(	//rs를 이용하여 ArticleContent 객체를 만들고
						rs.getInt("article_no"),rs.getString("content"));
			}
			return content;	//그 객체를 반환
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
	
	//해당 번호의 게시글 내용을 수정하는 매서드
	public int update(Connection conn, int no, String content) throws SQLException{
		try (PreparedStatement pstmt =conn.prepareStatement("update article_content set "
				+ "content = ? where article_no = ?")) {
			pstmt.setString(1, content);
			pstmt.setInt(2, no);
			return pstmt.executeUpdate();
		}
	}
	
	public int delete(Connection conn, int no) throws SQLException{
		try (PreparedStatement pstmt =conn.prepareStatement("delete from article_content "
				+ "where article_no = ?")) {
			pstmt.setInt(1, no);
			return pstmt.executeUpdate();
		}
	}
	
}




