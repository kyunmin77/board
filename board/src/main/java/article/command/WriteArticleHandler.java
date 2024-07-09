package article.command;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import article.model.Writer;
import article.service.WriteArticleService;
import article.service.WriteRequest;
import auth.service.User;
import mvc.command.CommandHandler;

public class WriteArticleHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/newArticleForm.jsp";
	private WriteArticleService writeService = new WriteArticleService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if(req.getMethod().equalsIgnoreCase("get")) {		//get 요청이 오면 FORM_VIEW
			return processForm(req,res);
		}else if(req.getMethod().equalsIgnoreCase("post")) {//post 요청이 오면 processSubmit()로 이동
			return processSubmit(req,res);
		} else {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}
	
	private String processForm(HttpServletRequest req, HttpServletResponse res) {
		return FORM_VIEW;
	}
	private String processSubmit(HttpServletRequest req, HttpServletResponse res) {
		Map<String, Boolean> errors = new HashMap<>();
		req.setAttribute("errors", errors);
		
		// 세션에서 User객체를 받아와서
		User user = (User)req.getSession(false).getAttribute("authUser"); //"authUser"는 LoginHandler에서 등록함
		//WriteRequest 객체를 만들고 에러가 없나 확인.
		WriteRequest writeReq = createWriteRequest(user, req);
		writeReq.validate(errors);
		
		if(!errors.isEmpty()) {	// 에러가있으면 newArticleForm 주소를 반환
			return FORM_VIEW;
		}
		
		//작성과 DB에 저장이 성공적으로 완료되면 저장한 Article의 number를 반환받음
		int newArticleNo = writeService.write(writeReq);
		req.setAttribute("newArticleNo", newArticleNo);
		//newArticleNo를 Request 영역에 저장
		
		return "/WEB-INF/view/newArticleSuccess.jsp";
		//newArticleSuccess 주소를 반환
	}
	
	private WriteRequest createWriteRequest(User user, HttpServletRequest req) {
		//User와 파라미터 값을 받아와서 WriteRequest객체 생성후 반환
		return new WriteRequest(
				new Writer(user.getId(),user.getName()),
				req.getParameter("title"),
				req.getParameter("content"));	
	}
	
}