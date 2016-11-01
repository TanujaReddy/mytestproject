package org.strut.amway.core.model;

import java.util.List;

import org.strut.amway.core.enumeration.ArticleLabelType;
import org.strut.amway.core.enumeration.SearchType;

public class ArticlesCriteria {

    private final SearchType searchType;
    private final String path;
    private final String fullText;
    private final List<String> tagIds;
    private final String startDate;
    private final String endDate;
    private final Long limit;
    private final Long offset;
    private final String order;
    private final String opsStartDate;
    private final String opsEndDate;
    private final String ignorePath;
    private final List<ArticleLabelType> articleLabelTypes;
    /*********Start of Changes for AmwayToday v3.0**************/
    private final List<String> articleLabels;

    /*********End of Changes for AmwayToday v3.0**************/

	private ArticlesCriteria(Builder builder) {
        this.searchType = builder.searchType;
        this.path = builder.path;
        this.fullText = builder.fullText;
        this.tagIds = builder.tagIds;
        this.startDate = builder.startDate;
        this.endDate = builder.endDate;
        this.limit = builder.limit;
        this.offset = builder.offset;
        this.order = builder.order;
        this.opsStartDate = builder.opsStartDate;
        this.opsEndDate = builder.opsEndDate;
        this.ignorePath = builder.ignorePath;
        this.articleLabelTypes = builder.articleLabelTypes;
        /*********Start of Changes for AmwayToday v3.0**************/
        this.articleLabels = builder.articleLabels;
        /*********End of Changes for AmwayToday v3.0**************/
    }

    public SearchType getSearchType() {
        return searchType;
    }

    public String getPath() {
        return path;
    }

    public String getFullText() {
        return fullText;
    }

    public List<String> getTagIds() {
        return tagIds;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public Long getLimit() {
        return limit;
    }

    public Long getOffset() {
        return offset;
    }

    public String getOrder() {
        return order;
    }

    public String getOpsStartDate() {
        return opsStartDate;
    }

    public String getOpsEndDate() {
        return opsEndDate;
    }

    public String getIgnorePath() {
        return ignorePath;
    }

    public List<ArticleLabelType> getArticleLabelTypes() {
        return articleLabelTypes;
    }
    /*********Start of Changes for AmwayToday v3.0**************/
    public List<String> getArticleLabels() {
		return articleLabels;
	}
    /*********End of Changes for AmwayToday v3.0**************/
    public static class Builder {

        private SearchType searchType;
        private String path;
        private String fullText;
        private List<String> tagIds;
        private String startDate;
        private String endDate;
        private Long limit;
        private Long offset;
        private String order;
        private String opsStartDate;
        private String opsEndDate;
        private String ignorePath;
        private List<ArticleLabelType> articleLabelTypes;
        
        /*********Start of Changes for AmwayToday v3.0**************/
        private List<String > articleLabels;

       

		public Builder setArticleLabels(List<String> articleLabels) {
			this.articleLabels = articleLabels;
			 return this;
		}
		
		/*********End of Changes for AmwayToday v3.0**************/

		public ArticlesCriteria build() {
            return new ArticlesCriteria(this);
        }

        public Builder setSearchType(SearchType searchType) {
            this.searchType = searchType;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setFullText(String fullText) {
            this.fullText = fullText;
            return this;
        }

        public Builder setTagIds(List<String> tagIds) {
            this.tagIds = tagIds;
            return this;
        }

        public Builder setStartDate(String startDate) {
            this.startDate = startDate;
            return this;
        }

        public Builder setEndDate(String endDate) {
            this.endDate = endDate;
            return this;
        }

        public Builder setLimit(Long limit) {
            this.limit = limit;
            return this;
        }

        public Builder setOffset(Long offset) {
            this.offset = offset;
            return this;
        }

        public Builder setOrder(String order) {
            this.order = order;
            return this;
        }

        public Builder setOpsStartDate(String opsStartDate) {
            this.opsStartDate = opsStartDate;
            return this;
        }

        public Builder setOpsEndDate(String opsEndDate) {
            this.opsEndDate = opsEndDate;
            return this;
        }

        public Builder setIgnorePath(String ignorePath) {
            this.ignorePath = ignorePath;
            return this;
        }

        public Builder setArticleLabelTypes(List<ArticleLabelType> articleLabelTypes) {
            this.articleLabelTypes = articleLabelTypes;
            return this;
        }

    }
}
