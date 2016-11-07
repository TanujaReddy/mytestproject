package org.strut.amway.core.model;

import java.util.List;

import com.day.cq.wcm.api.Page;

public class ArticleSearchResult {

    private List<Page> pages;
    private Long limit;
    private Long offset;

    public List<Page> getPages() {
        return pages;
    }

    public void setPages(List<Page> pages) {
        this.pages = pages;
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

}
