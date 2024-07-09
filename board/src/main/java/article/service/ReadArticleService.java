package article.service;

import java.sql.Connection;
import java.sql.SQLException;

import article.dao.ArticleContentDao;
import article.dao.ArticleDao;
import article.model.Article;
import article.model.ArticleContent;
import jdbc.connection.ConnectionProvider;

public class ReadArticleService {
	private ArticleDao articleDao = new ArticleDao();
	private ArticleContentDao contentDao = new ArticleContentDao();
	
	//
	public ArticleData getArticle(int articleNum, boolean increaseReadCount) {
		try(Connection conn = ConnectionProvider.getConnection()) {
			
			//해당 articleNum의 Article 객체를 받아옴
			Article article = articleDao.selectById(conn, articleNum);
			if(article == null) {
				throw new ArticleNotFoundException();
			}
			//해당 articleNum의 ArticleContent 객체를 받아옴
			ArticleContent content = contentDao.selectById(conn, articleNum);
			
			if(increaseReadCount) {	// 조건이 참이면
				//해당 articleNum의 게시글의 조회수를 하나 증가시킴
				articleDao.increaseReadCount(conn, articleNum);
			}
			return new ArticleData(article, content);	
			// 받아온 내용을 담은 ArticleData 객체를 반환
			
		}catch(SQLException e) {
			throw new RuntimeException(e);
		}
	}
}


