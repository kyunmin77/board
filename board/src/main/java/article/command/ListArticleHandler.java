package article.command;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import article.service.ArticlePage;
import article.service.ListArticleService;
import mvc.command.CommandHandler;

public class ListArticleHandler implements CommandHandler {

	private ListArticleService listService = new ListArticleService();
	
	@Override
	public String process(HttpServletRequest req, HttpServletResponse res) throws Exception {
		String pageNoVal = req.getParameter("pageNo");	//Request 파라미터에서 pageNo를 받아옴
		int pageNo = 1;				// pageNo는 기본적으로 1이고
		if(pageNoVal != null) {		// 파라미터에서 받아온 값이 있다면 그값으로 변경
			pageNo = Integer.parseInt(pageNoVal);
		}
		
		//해당 페이지 넘버에 표시될 내용을 담은 ArticlePage객체를 반환받음
		ArticlePage articlePage = listService.getArticlePage(pageNo);
		
		//Request에 articlePage을 저장
		req.setAttribute("articlePage", articlePage);
		return "/WEB-INF/view/listArticle.jsp";	
		//listArticle 페이지 주소를 반환
	}

}
