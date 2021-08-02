package preprocessing;

import java.util.HashMap;
import java.util.Map;

public class Articles {
	
	private Map<String, Article> articles;
	
	public Articles() {
		setArticles();
	}
	
	public void addArticle(Article article) {
		getArticles().put(article.getFileName(), article);
	}

	public Map<String, Article> getArticles() {
		return this.articles;
	}

	private void setArticles() {
		this.articles = new HashMap<String, Article>();
	}
	
	public Article getArticle(String artName) {
		return getArticles().get(artName);
	}
}
