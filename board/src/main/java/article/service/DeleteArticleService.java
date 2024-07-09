package article.service;

import java.sql.Connection;
import java.sql.SQLException;

import article.dao.ArticleContentDao;
import article.dao.ArticleDao;
import article.model.Article;
import jdbc.JdbcUtil;
import jdbc.connection.ConnectionProvider;

public class DeleteArticleService {
	private ArticleDao articleDao = new ArticleDao();
	private ArticleContentDao contentDao = new ArticleContentDao();
	
	public void delete(DeleteRequest delReq) {
		Connection conn = null;
		try {
			conn = ConnectionProvider.getConnection();
			//트랜잭션 시작
			conn.setAutoCommit(false);
			
			//삭제할 articleNumber에 해당하는 게시글을 받아옴
			Article article = articleDao.selectById(conn, delReq.getArticleNumber());
			if(article == null) {
				throw new ArticleNotFoundException();
			}
			
			//받아온 게시글의 작성자 id와 수정하는 사람의 id가 다르면 PermissionDeniedException
			if(!canDelete(delReq.getUserId(), article)) {
				throw new PermissionDeniedException();
			}
			
			//delReq의 번호에맞는 게시글, 게시글내용을 삭제
			articleDao.delete(conn, delReq.getArticleNumber());
			contentDao.delete(conn, delReq.getArticleNumber());
			conn.commit();
			//트랜잭션 끝
		}catch(SQLException e) {
			JdbcUtil.rollback(conn);
			throw new RuntimeException(e);
		}catch(PermissionDeniedException e) {
			JdbcUtil.rollback(conn);
			throw e;
		}finally {
			JdbcUtil.close(conn);
		}
	}
	
	//게시글의 작성자 아이디와 수정을 시도하는유저의 아이디가 일치하면 True를 반환
	private boolean canDelete(String deletingUserId, Article article) {
		return article.getWriter().getId().equals(deletingUserId);
	}
}
