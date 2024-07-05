package filter;

import java.io.IOException;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

public class NullParameterFilter implements Filter {
	
	private String[] parameterNames = null;

	@Override
	public void init(FilterConfig config) throws ServletException {
		String names = config.getInitParameter("parameterNames");
		StringTokenizer st = new StringTokenizer(names, ",");
		parameterNames = new String[st.countTokens()];
		
		for(int i=0; st.hasMoreTokens();i++) {
			parameterNames[i] = st.nextToken();
		}
	}
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		NullParameterRequestWrapper requestWrapper = new NullParameterRequestWrapper((HttpServletRequest) req);
		
		requestWrapper.checkNull(parameterNames);
		
		chain.doFilter(requestWrapper, res);
	}

	@Override
	public void destroy() {
	}

}
