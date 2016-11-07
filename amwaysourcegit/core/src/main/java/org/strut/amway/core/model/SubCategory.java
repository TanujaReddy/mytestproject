package org.strut.amway.core.model;

public class SubCategory {
	
	private String title;
	
	private String url;
	
	public SubCategory(String title, String url){
		this.title = title;
		this.url = url;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
