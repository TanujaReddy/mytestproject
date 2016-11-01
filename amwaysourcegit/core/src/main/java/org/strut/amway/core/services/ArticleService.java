package org.strut.amway.core.services;

import java.util.ArrayList;
import java.util.Map;

import org.apache.sling.api.SlingHttpServletRequest;
import org.strut.amway.core.model.AuthorUserDetails;

import com.day.cq.wcm.api.Page;

public interface ArticleService {

    public int getLikeNumberByArticleName(String articleName);

    public int updateLikeNumberByArticleName(String articleName);
    
    public Map<String, String> getImages(SlingHttpServletRequest request,String url, Page currentPage);
    public Map<String, String> getImageString(SlingHttpServletRequest request,String url, Page currentPage);
    public ArrayList<AuthorUserDetails> getUserDetails(SlingHttpServletRequest request) ;
    public Map<String, String> getImageDescriptions(SlingHttpServletRequest request,String url, Page currentPage);
  
}
