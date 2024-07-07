package member.command;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import auth.service.User;
import member.service.ChangePasswordService;
import member.service.InvalidPasswordException;
import member.service.MemberNotFoundException;
import mvc.command.CommandHandler;

public class ChangePasswordHandler implements CommandHandler {
	private static final String FORM_VIEW = "WEB-INF/view/changePwdForm.jsp";
	private ChangePasswordService changePwdSvc = new ChangePasswordService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if(req.getMethod().equalsIgnoreCase("get")) {			//get 요청이 오면 FORM_VIEW
			return processForm(req, res);
		}else if(req.getMethod().equalsIgnoreCase("post")) {	//post 요청이 오면 processSubmit()로 이동
			return processSubmit(req,res);
		}else {
			res.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}
	
	private String processForm(HttpServletRequest req, HttpServletResponse res) {
		return FORM_VIEW;
	}
	
	private String processSubmit(HttpServletRequest req, HttpServletResponse res) throws Exception{
		//세션에 저장해논 User 객체를 받아옴
		User user = (User)req.getSession().getAttribute("authUser");
		
		Map<String,Boolean> errors = new HashMap<>();
		req.setAttribute("errors", errors);
		
		//Request에 저장된 파라미터를 받아옴
		String curPwd = req.getParameter("curPwd");
		String newPwd = req.getParameter("newPwd");
		
		//비밀번호들이 비어있다면 errors의 값은 True
		if(curPwd == null || curPwd.isEmpty()) {
			errors.put("curPwd", Boolean.TRUE);
		}
		if(newPwd == null || newPwd.isEmpty()) {
			errors.put("newPwd", Boolean.TRUE);
		}
		// 에러가있으면 changePwdForm으로 돌아감
		if(!errors.isEmpty()) {
			return FORM_VIEW;
		}
		
		try {
			// 비밀번호 변경 성공시 changePwdSuccess로 이동
			changePwdSvc.changePassword(user.getId(), curPwd, newPwd);
			return "/WEB-INF/view/changePwdSuccess.jsp";
		}catch(InvalidPasswordException e) {
			errors.put("badCurPwd", Boolean.TRUE);
			return FORM_VIEW;
		}catch(MemberNotFoundException e) {
			res.sendError(HttpServletResponse.SC_BAD_REQUEST);
			return null;
		}
	}
	
}
