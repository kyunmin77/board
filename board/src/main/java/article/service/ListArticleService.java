package article.service;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import article.dao.ArticleDao;
import article.model.Article;
import jdbc.connection.ConnectionProvider;

public class ListArticleService {
	private ArticleDao articleDao = new ArticleDao();
	private int size = 10;
	
	// pageNum을 받아와 ArticlePage를 반환하는 매서드
	public ArticlePage getArticlePage(int pageNum) {
		int firstRow = 0;
		int endRow = 0;
		List<Article> content = null;
		try(Connection conn = ConnectionProvider.getConnection()){
			int total = articleDao.selectCount(conn);	//DB에 저장된 게시글수를 불러와 저장
			if(total > 0 ) {
				firstRow = (pageNum -1) *size + 1;		
				endRow =  firstRow + size - 1;			//한 페이지에 표시할 row범위를 지정
				content = articleDao.select(conn, firstRow, endRow);
				// 지정한 범위안에 있는 article을 담은 ArrayList 반환 받아옴
			}
			return new ArticlePage(total, pageNum, size, content);	//ArticlePage객체를 반환
		}catch (SQLException e){
			throw new RuntimeException(e);
		}
	}
}





