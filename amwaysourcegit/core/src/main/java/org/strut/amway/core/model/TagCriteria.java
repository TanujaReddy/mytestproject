package org.strut.amway.core.model;

public class TagCriteria {
    private final String localeCode;
    private final String title;
    private final Boolean exactMatch;

    private TagCriteria(Builder builder) {
        this.localeCode = builder.localeCode;
        this.title = builder.title;
        this.exactMatch = builder.exactMatch;
    }

    public String getTitle() {
        return title;
    }

    public String getLocaleCode() {
        return localeCode;
    }

    public Boolean getExactMatch() {
        return exactMatch;
    }

    public static class Builder {
        private String localeCode;
        private String title;
        private Boolean exactMatch;

        public TagCriteria build() {
            return new TagCriteria(this);
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setLocaleCode(String localeCode) {
            this.localeCode = localeCode;
            return this;
        }

        public Builder setExactMatch(Boolean exactMatch) {
            this.exactMatch = exactMatch;
            return this;
        }

    }

}
