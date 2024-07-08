package article.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import article.model.Article;
import article.model.Writer;
import jdbc.JdbcUtil;

public class ArticleDao {
	// insert 매서드
	public Article insert(Connection conn, Article article) throws SQLException{
		PreparedStatement pstmt = null;
		Statement stmt = null;
		ResultSet rs = null;
		try {
			// article객체의 정보들을 쿼리문에 대입하여 레코드를 삽입하는 선언문
			pstmt = conn.prepareStatement("insert into article "
					+ "(article_no, writer_id, writer_name, title, regdate, moddate, read_cnt) "
					+ "values(article_seq.nextval,?,?,?,?,?,0)");
			pstmt.setString(1, article.getWriter().getId());
			pstmt.setString(2, article.getWriter().getName());
			pstmt.setString(3, article.getTitle());
			pstmt.setTimestamp(4, toTimestamp(article.getRegDate()));
			pstmt.setTimestamp(5, toTimestamp(article.getModifiedDate()));
			int insertedCount = pstmt.executeUpdate();
			// 쿼리를 실행하고 영향을 받은 레코드 수를 반환받음
			
			if(insertedCount > 0) { // 입력이 정상적으로 수행되었다면
				stmt = conn.createStatement();
				rs = stmt.executeQuery("SELECT * "
						+ "FROM( SELECT article_no FROM article"
						+ " ORDER BY ROWNUM DESC )"			// article_no을 내림차순 정렬한 뒤
						+ "WHERE ROWNUM = 1");				// 첫번째 열 정보를 선택
		
				if(rs.next()) {
					// rs쿼리문에 첫번째필드를 Int형으로 받아옴(시퀀스값)
					Integer newNum = rs.getInt(1);
					// DB에 저장된 내용과 같은 Article객체를 만들어 반환
					return new Article(newNum,
							article.getWriter(),
							article.getTitle(),
							article.getRegDate(),
							article.getModifiedDate(),
							0);
				}
			}
			return null;		// 입력이 수행되지 않았다면 null 반환
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
			JdbcUtil.close(pstmt);
		}
	}
	private Timestamp toTimestamp(Date date) {
		return new Timestamp(date.getTime());	//Date형 객체를 Timestamp 객체로 바꾸어 반환
	}
	
	public int selectCount(Connection conn) throws SQLException{// Article 수를 반환하는 매서드
		Statement stmt = null;
		ResultSet rs = null;
		try {
			stmt = conn.createStatement();
			rs = stmt.executeQuery("select count(*) from article");	// 레코드 수를 세어 반환
			if(rs.next()) {
				return rs.getInt(1);	// rs의 1번째 필드를 Int형으로 받고 그수를 반환.
			}
			return 0;
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(stmt);
		}
	}
	
	public List<Article> select(Connection conn, int firstRow, int endRow) throws SQLException{
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		try {
			pstmt = conn.prepareStatement(
					"select * from (select rownum as rnum, a.* from"
					+ " (select * from article order by article_no desc)"
					+ " a where rownum <= ?) where rnum >= ?");
					// rownum + (내림차순 정렬된 article 목록)이 적힌 레코드를 원하는 구간만큼 selcet
			pstmt.setInt(1, endRow);
			pstmt.setInt(2, firstRow);
			rs = pstmt.executeQuery();
			List<Article> result = new ArrayList<>();
			
			while(rs.next()) {
				result.add(convertArticle(rs));	//rs레코드를 Article 객체로 바꾸어 ArrayList에 저장
			}
			return result; // article을 담은 ArrayList 반환
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
	
	//ResultSet을 Article형으로바꾸는 매서드
	private Article convertArticle(ResultSet rs) throws SQLException{
		// rs에 담긴 정보로 Article 객체를 만들어 반환
		return new Article(rs.getInt("article_no"),	//article_no필드를 int형으로 받아옴
				new Writer(
						rs.getString("writer_id"),	//writer_id필드를 String형으로 받아옴
						rs.getString("writer_name")),	// 같은 방식으로 생성자에 변수 입력해감
				rs.getString("title"),
				toDate(rs.getTimestamp("regdate")),	//Timestamp형을 Date형으로 바꾸어 저장
				toDate(rs.getTimestamp("moddate")),
				rs.getInt("read_cnt"));
	}
	
	private Date toDate(Timestamp ts) { //Timestamp형을 Date형으로 바꾸는 매서드
		return new Date(ts.getTime());
	}
	
	// no와 일치하는 Article을 반환하는 매서드
	public Article selectById(Connection conn, int no) throws SQLException{
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		try {
			pstmt = conn.prepareStatement("select * from article where article_no = ?");
			pstmt.setInt(1, no);
			rs = pstmt.executeQuery();
			// 해당 번호의 게시글 레코드를 rs에 저장
			
			Article article = null;
			if(rs.next()) {
				article = convertArticle(rs);	//게시글을 Article형 객체로 바꿈
			}
			return article;	// 해당 게시글 반환
		}finally {
			JdbcUtil.close(rs);
			JdbcUtil.close(pstmt);
		}
	}
	
	//해당 번호의 게시글의 조회수 증가시키는 매서드
	public void increaseReadCount(Connection conn, int no) throws SQLException {
		try (PreparedStatement pstmt =conn.prepareStatement("update article set read_cnt ="
				+ "read_cnt + 1 where article_no = ?")){
			pstmt.setInt(1, no);
			pstmt.executeUpdate();
		}
	}
	
	//해당 번호의 게시글 제목을 수정하는 매서드
	public int update(Connection conn, int no, String title) throws SQLException{
		try (PreparedStatement pstmt =conn.prepareStatement("update article set "
				+ "title = ?, moddate = sysdate where article_no = ?")){
			pstmt.setString(1, title);
			pstmt.setInt(2, no);
			return pstmt.executeUpdate();
		}
	}
	
	public int delete(Connection conn, int no) throws SQLException{
		try (PreparedStatement pstmt =conn.prepareStatement("delete from article "
				+ "where article_no = ?")){
			pstmt.setInt(1, no);
			return pstmt.executeUpdate();
		}
	}

}





