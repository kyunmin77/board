package member.command;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import member.service.DuplicateIdException;
import member.service.JoinRequest;
import member.service.JoinService;
import mvc.command.CommandHandler;

public class JoinHandler implements CommandHandler {

	private static final String FORM_VIEW = "/WEB-INF/view/joinForm.jsp";
	private JoinService joinService = new JoinService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		if(req.getMethod().equalsIgnoreCase("get")) {			//get 요청이 오면
			return processForm(req,res);						// joinForm 화면으로 이동
		}else if(req.getMethod().equalsIgnoreCase("post")) {	//post 요청이 오면..
			return processSubmit(req,res);
		}else {
			res.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
	}
	
	private String processForm(HttpServletRequest req, HttpServletResponse res) {
		return FORM_VIEW;		
	}

	private String processSubmit(HttpServletRequest req, HttpServletResponse res) {
		JoinRequest joinReq = new JoinRequest();
		
		joinReq.setId(req.getParameter("id"));
		joinReq.setName(req.getParameter("name"));
		joinReq.setPassword(req.getParameter("password"));
		joinReq.setConfirmPassword(req.getParameter("confirmPassword"));	
		//joinReq객체에 회원정보를 입력 받아
		
		Map<String, Boolean> errors = new HashMap<>();
		req.setAttribute("errors", errors);
		
		// 유효한지 확인하고
		joinReq.validate(errors);
		
		if(!errors.isEmpty()) {
			return FORM_VIEW;		
		}
		
		try {
			joinService.join(joinReq);
			return "/WEB-INF/view/joinSuccess.jsp";		//회원가입 성공시 joinSuccess화면으로 이동
		}catch(DuplicateIdException e) {
			errors.put("duplicateId", Boolean.TRUE);
			return FORM_VIEW;							//실패시 joinForm 화면으로 이동
		}
	}
}
