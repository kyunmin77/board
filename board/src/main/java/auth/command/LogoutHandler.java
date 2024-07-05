package auth.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mvc.command.CommandHandler;

public class LogoutHandler implements CommandHandler {
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		HttpSession session = req.getSession(false);
		if(session != null) {
			session.invalidate();	//세션을 종료하고
		}
		res.sendRedirect(req.getContextPath() + "/index.jsp");	//index.jsp로 돌아감
		return null;
	}

}
