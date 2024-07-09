package article.service;

import java.util.List;

import article.model.Article;

public class ArticlePage {
	private int total;				//총 게시글 수
	private int currentPage;
	private List<Article> content;	//현재 페이지에 표시되는 게시글들
	private int totalPages;
	private int startPage;
	private int endPage;
	
	public ArticlePage(int total, int currentPage, int size, List<Article> content) {
		this.total=total;
		this.currentPage = currentPage;
		this.content = content;
		if(total == 0) {	//total==0 이면, 모든 page번수들을 0으로 초기화
			totalPages = 0;
			startPage = 0;
			endPage = 0;
		}else {
			totalPages = total/size;	// 총 페이지 수는 (총 게시글 수)/(한페이지에 보이는 게시글 수)
			if(total % size > 0) {		// (총 게시글 수)/(한페이지에 보이는 게시글 수)에 나머지가 있다면
				totalPages++;			// 총 페이지 수++;
			}
			int modVal = currentPage % 5;		
			startPage = currentPage / 5 * 5 + 1;	// (int)[현재 페이지/5] *5 +1
			if(modVal == 0) startPage -= 5;			//(현재페이지)/5의 나머지가 0이면 startPage-5
			
			//ex) 1,2,3,4,5는 startPage = 1, 6,7,8,9,10은 startPage = 6
			
			endPage = startPage + 4;
			if(endPage > totalPages) endPage = totalPages;	//endPage는 totalPages보다 클수없음
		}
	}

	public int getTotal() {
		return total;
	}

	public boolean hasNoArticles() {
		return total == 0;
	}
	
	public boolean hasArticles() {
		return total > 0;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getTotalPages() {
		return totalPages;
	}

	public List<Article> getContent() {
		return content;
	}

	public int getStartPage() {
		return startPage;
	}

	public int getEndPage() {
		return endPage;
	}	
	
}



