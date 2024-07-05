package filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginCheckFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) req;
		HttpSession session = httpRequest.getSession(false);	
		
		// 세션이 비어있거나 User가 없으면(로그인이 확인되지 않으면)
		if(session == null || session.getAttribute("authUser") == null) {
			HttpServletResponse response = (HttpServletResponse)res;
			response.sendRedirect(httpRequest.getContextPath()+"/login.do");
			// 로그인 화면으로 이동.
		}else {
			chain.doFilter(req, res);		// 요청을 다른필터나 서블릿으로 전달함
		}
	}

	@Override
	public void destroy() {
	}

}
