package article.service;

import java.util.Map;

public class DeleteRequest {
	private String userId;
	private int articleNumber;
	private String title;
	private String content;

	public DeleteRequest(String userId, int articleNumber, String title, String content) {
		super();
		this.userId = userId;
		this.articleNumber = articleNumber;
		this.title = title;
		this.content = content;
	}

	public String getUserId() {
		return userId;
	}

	public int getArticleNumber() {
		return articleNumber;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	// 제목이 비어있다면 errors의 "title"은 참이 됨
	public void validate(Map<String, Boolean> errors) {
		if (title == null || title.trim().isEmpty()) {
			errors.put("title", Boolean.TRUE);
		}
	}
}
