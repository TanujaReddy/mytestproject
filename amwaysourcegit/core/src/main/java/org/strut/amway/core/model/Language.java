package org.strut.amway.core.model;

public class Language {

    private String languageName;

    private String languageUrl;

    public Language(String languageName, String languageUrl) {
        this.languageName = languageName;
        this.languageUrl = languageUrl;
    }

    public String getLanguageName() {
        return languageName;
    }

    public void setLanguageName(String languageName) {
        this.languageName = languageName;
    }

    public String getLanguageUrl() {
        return languageUrl;
    }

    public void setLanguageUrl(String languageUrl) {
        this.languageUrl = languageUrl;
    }
}
