package article.command;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import article.service.ArticleData;
import article.service.ArticleNotFoundException;
import article.service.DeleteArticleService;
import article.service.DeleteRequest;
import article.service.ModifyRequest;
import article.service.PermissionDeniedException;
import article.service.ReadArticleService;
import auth.service.User;
import mvc.command.CommandHandler;

public class DeleteArticleHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/readArticle.jsp";

	private ReadArticleService readService = new ReadArticleService();
	private DeleteArticleService deleteService = new DeleteArticleService();

	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if (req.getMethod().equalsIgnoreCase("get")) { // get 요청이 오면 processForm(req,res)
			return processForm(req, res);
		} else if (req.getMethod().equalsIgnoreCase("post")) {// post 요청이 오면 processSubmit(req,res)
			return processSubmit(req, res);
		} else {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}

	private String processForm(HttpServletRequest req, HttpServletResponse res) throws IOException {
		try {
			String noVal = req.getParameter("no");
			int no = Integer.parseInt(noVal); // 파라미터에서 번호를 받아와서

			// 해당 번호의 ArticleData 객체를 받아옴 (조회수는 증가하지 않음)
			ArticleData articleData = readService.getArticle(no, false);

			// 세션에 등록된(로그인 된) 유저의 객체를 받아옴
			User authUser = (User) req.getSession().getAttribute("authUser");
			// 유저와 게시글 작성자가 일치하지 않으면 에러
			if (!canDelete(authUser, articleData)) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
				return null;
			}

			// 로그인 된 유저와 게시글 정보로 DeleteRequest객체 생성
			DeleteRequest delReq = new DeleteRequest(authUser.getId(), no, articleData.getArticle().getTitle(),
					articleData.getContent());
			// DeleteRequest객체 delReq를 Request에 저장
			req.setAttribute("delReq", delReq);

			Map<String, Boolean> errors = new HashMap<>();
			req.setAttribute("errors", errors);
			// delReq에 에러가 없는지 확인
			delReq.validate(errors);
			if (!errors.isEmpty()) {
				return FORM_VIEW; // 에러가 있다면 readArticle 페이지주소를 반환
			}

			// delReq객체로 게시글 삭제
			deleteService.delete(delReq);
			return "/WEB-INF/view/deleteSuccess.jsp";
			// 성공하면 deleteSuccess 페이지주소를 반환
		} catch (ArticleNotFoundException e) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} catch (PermissionDeniedException e) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}

	}

	// 게시글 작성자와 로그인중인 유저의 아이디가 일치하는지 확인하는 매서드
	private boolean canDelete(User authUser, ArticleData articleData) {
		String writerId = articleData.getArticle().getWriter().getId();
		return authUser.getId().equals(writerId);
	}

	private String processSubmit(HttpServletRequest req, HttpServletResponse res) throws Exception {
		return FORM_VIEW; // readArticle 페이지주소를 반환
	}

}
