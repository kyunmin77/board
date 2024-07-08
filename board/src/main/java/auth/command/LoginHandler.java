package auth.command;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import auth.service.LoginFailException;
import auth.service.LoginService;
import auth.service.User;
import mvc.command.CommandHandler;

public class LoginHandler implements CommandHandler {
	private static final String FORM_VIEW = "/WEB-INF/view/loginForm.jsp";
	private LoginService loginService = new LoginService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if(req.getMethod().equalsIgnoreCase("get")) {			//get 요청이 오면
			return processForm(req,res);						//loginForm 화면으로 이동
		}else if(req.getMethod().equalsIgnoreCase("post")) {	//post 요청이 오면 processSubmit()로 이동
			return processSubmit(req,res);
		}else {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}
	
	private String processForm(HttpServletRequest req, HttpServletResponse res) {
		return FORM_VIEW;
	}

	private String processSubmit(HttpServletRequest req, HttpServletResponse res) throws Exception{
		String id = trim(req.getParameter("id"));
		String password = trim(req.getParameter("password"));
		Map<String,Boolean> errors = new HashMap<>();
		req.setAttribute("errors", errors);
		
		if(id == null || id.isEmpty())
			errors.put("id", Boolean.TRUE);
		if(password == null || password.isEmpty())
			errors.put("password", Boolean.TRUE);
		if(!errors.isEmpty())
			return FORM_VIEW;			// 파라미터에 공백이 있다면 loginForm 화면을 다시 불러옴
		
		try {
			User user = loginService.login(id, password);		
			//로그인에 성공하면 session에 User객체를 저장
			req.getSession().setAttribute("authUser", user);
				
			res.sendRedirect(req.getContextPath() + "/index.jsp");	//index.jsp로 이동.
			return null;
		}catch(LoginFailException e) {
			errors.put("idOrPwNotMatch", Boolean.TRUE);
			return FORM_VIEW;
		}
	}
	
	private String trim(String str) {
		return str == null ? null : str.trim();
	}

}
