package article.command;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import article.service.ArticleData;
import article.service.ArticleNotFoundException;
import article.service.ModifyArticleService;
import article.service.ModifyRequest;
import article.service.PermissionDeniedException;
import article.service.ReadArticleService;
import auth.service.User;
import mvc.command.CommandHandler;

public class ModifyArticleHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/modifyForm.jsp";

	private ReadArticleService readService = new ReadArticleService();
	private ModifyArticleService modifyService = new ModifyArticleService();

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
			if (!canModify(authUser, articleData)) {
				res.sendError(HttpServletResponse.SC_FORBIDDEN);
				return null;
			}

			// 로그인 된 유저와 게시글 정보로 ModifyRequest객체 생성
			ModifyRequest modReq = new ModifyRequest(authUser.getId(), no, articleData.getArticle().getTitle(),
					articleData.getContent());

			// ModifyRequest객체 modReq를 Request에 저장
			req.setAttribute("modReq", modReq);
			return FORM_VIEW; // modifyForm 페이지주소를 반환

		} catch (ArticleNotFoundException e) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
	}

	// 게시글 작성자와 로그인중인 유저의 아이디가 일치하는지 확인하는 매서드
	private boolean canModify(User authUser, ArticleData articleData) {
		String writerId = articleData.getArticle().getWriter().getId();
		return authUser.getId().equals(writerId);
	}

	private String processSubmit(HttpServletRequest req, HttpServletResponse res) throws Exception {
		User authUser = (User) req.getSession().getAttribute("authUser");
		String noVal = req.getParameter("no");
		int no = Integer.parseInt(noVal);
		// 로그인중인 User 객체와 페이지 번호를 받아옴

		// 받아온 데이터를 이용하여 modReq객체 생성
		ModifyRequest modReq = new ModifyRequest(authUser.getId(), no, req.getParameter("title"),
				req.getParameter("content"));
		// modReq객체를 Request에 저장
		req.setAttribute("modReq", modReq);

		Map<String, Boolean> errors = new HashMap<>();
		req.setAttribute("errors", errors);
		// modReq에 에러가 없는지 확인
		modReq.validate(errors);
		if (!errors.isEmpty()) {
			return FORM_VIEW; // 에러가 있다면 modifyForm 페이지주소를 반환
		}
		try {
			// modReq객체로 게시글 수정
			modifyService.modify(modReq);
			return "/WEB-INF/view/modifySuccess.jsp";
			// 성공하면 modifySuccess 페이지주소를 반환

		} catch (ArticleNotFoundException e) {
			res.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		} catch (PermissionDeniedException e) {
			res.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
	}

}
