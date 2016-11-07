package org.strut.amway.core.servlets.article;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;

import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.Resource;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.services.ArticleService;
import org.strut.amway.core.services.ArticleStatisticsService;
import org.strut.amway.core.util.ArticleConstants;

public class ArticleLikeNumberServletTest {

    @Mock
    ArticleStatisticsService articleStatisticsService;

    @Mock
    SlingHttpServletRequest slingHttpServletRequest;

    @Mock
    SlingHttpServletResponse slingHttpServletResponse;

    @Mock
    HttpSession httpSession;

    @Mock
    Resource resource;

    ByteArrayOutputStream outputStream;

    ArticleLikesServlet articleUpdateLikeNumberServlet;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);

        articleUpdateLikeNumberServlet = new ArticleLikesServlet();
        final Field articleServiceField = FieldUtils.getField(ArticleLikesServlet.class, "articleStatisticsService", true);
        articleServiceField.setAccessible(true);
        articleServiceField.set(articleUpdateLikeNumberServlet, articleStatisticsService);
        articleUpdateLikeNumberServlet.init();

        outputStream = new ByteArrayOutputStream(1024);
        PrintWriter writer = new PrintWriter(outputStream);

        when(slingHttpServletRequest.getMethod()).thenReturn("GET");
        when(slingHttpServletRequest.getResource()).thenReturn(resource);
        when(slingHttpServletRequest.getSession()).thenReturn(httpSession);
        when(slingHttpServletResponse.getWriter()).thenReturn(writer);

    }

    @Test
    public void testArticlePathIsNull() throws ServletException, IOException {
        when(resource.getPath()).thenReturn(null);

        articleUpdateLikeNumberServlet.service(slingHttpServletRequest, slingHttpServletResponse);
        String output = outputStream.toString();
        assertEquals("", output);
    }

    @Test
    public void testArticlePathIsNotNullInSession() throws ServletException, IOException {
        StringBuilder articlePath = new StringBuilder();
        articlePath.append(ArticleConstants.PATH_STORED_NODE).append(ArticleConstants.NODE_REGEX).append("article1");
        when(resource.getPath()).thenReturn(articlePath.toString());
        when(httpSession.getAttribute(articlePath.toString())).thenReturn(true);
        when(articleStatisticsService.incrementStatistic(ArticleConstants.STATISTIC_LIKES, articlePath.toString())).thenReturn(7L);

        articleUpdateLikeNumberServlet.service(slingHttpServletRequest, slingHttpServletResponse);
        String output = outputStream.toString();
        assertEquals("{\"likes\":0,\"views\":0}", output);
    }

}
