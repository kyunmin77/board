package article.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import article.dao.ArticleContentDao;
import article.dao.ArticleDao;
import article.model.Article;
import article.model.ArticleContent;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class WriteArticleService {
	private ArticleDao articleDao = new ArticleDao();
	private ArticleContentDao contentDao = new ArticleContentDao();
	
	public Integer write(WriteRequest req) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			//트랜잭션 시작
			conn.setAutoCommit(false);
			
			Article article = toArticle(req);	//WriteRequest형 객체로 article 인스턴스 생성
			//article을 DB에 저장 하고 저장에 성공한 객체를 받아옴
			Article savedArticle = articleDao.insert(conn, article); //article을 DB에 저장
			if(savedArticle == null) {	// 저장에 실패하면 RuntimeException
				throw new RuntimeException("fail to insert article");
			}
			//저장에 성공한 article객체와 WriteRequest 객체로 ArticleContent 인스턴스 생성
			ArticleContent content = new ArticleContent(
					savedArticle.getNumber(),
					req.getContent());
			
			//content을 DB에 저장 하고 저장에 성공한 객체를 받아옴
			ArticleContent savedContent = contentDao.insert(conn, content);
			if(savedContent == null) {	// 저장에 실패하면 RuntimeException
				throw new RuntimeException("fail to insert article_content");
			}
			
			conn.commit();
			//트랜잭션 끝
			return savedArticle.getNumber();	// 저장한 Article의 number를 반환
		}catch (SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		}catch (RuntimeException e) {
			JdbcUtil.rollback(conn);
			throw e;
		}finally {
			JdbcUtil.close(conn);
		}
	}	
	
	//WriteRequest 객체를 Article로 바꾸어 반환해주는 매서드
	private Article toArticle(WriteRequest req) {
		Date now = new Date();	// Date객체에 현재 날짜를 받아와 아래 인스턴스에 대입함
		return new Article(null,req.getWriter(),req.getTitle(),now,now,0);
	}
}
