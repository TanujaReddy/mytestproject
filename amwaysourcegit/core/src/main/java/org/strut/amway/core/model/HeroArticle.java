package org.strut.amway.core.model;

public class HeroArticle {

    private String title;
    private String category;
    private String snippet;
    private String categoryPath;
    private String url;
    private String type;
    private String publishedDate;
    private String imageUrl;
    private Long impression;

    public HeroArticle(String title, String category, String snippet, String categoryPath, String url, String type,
                       String publishedDate, String imageUrl, Long impression) {
        this.title = title;
        this.category = category;
        this.snippet = snippet;
        this.categoryPath = categoryPath;
        this.url = url;
        this.type = type;
        this.publishedDate = publishedDate;
        this.imageUrl = imageUrl;
        this.impression = impression;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategoryPath() {
        return categoryPath;
    }

    public void setCategoryPath(String categoryPath) {
        this.categoryPath = categoryPath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Long getImpression() {
        return impression;
    }

    public void setImpression(Long impression) {
        this.impression = impression;
    }

    public String getSnippet() {
        return snippet;
    }
}
