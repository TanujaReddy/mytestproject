package org.strut.amway.core.util;

import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.Test;
import org.mockito.Mockito;

import javax.jcr.Node;
import javax.jcr.Property;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

public class ArticleUtilsTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenParamIsNull() {
        final Page article = null;
        ArticleUtils.getPublishedDate(article);
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldReturnPublishedDateProperly() {
        final Page article = Mockito.mock(Page.class);
        final Calendar publishedDate = new GregorianCalendar(2014, 4, 10);
        ValueMap valueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put(ArticleConstants.PUBLISH_DATE, publishedDate);
            }
        });
        when(article.getProperties()).thenReturn(valueMap);

        Calendar result = ArticleUtils.getPublishedDate(article);

        assertEquals(publishedDate, result);
    }

    @Test
    public void shouldReturnNullWhenPublishedDateIsNotPresent() {
        final Page article = Mockito.mock(Page.class);
        ValueMap valueMap = new ValueMapDecorator(new HashMap<String, Object>());
        when(article.getProperties()).thenReturn(valueMap);

        Calendar result = ArticleUtils.getPublishedDate(article);

        assertNull(result);
    }

    @Test
    public void extractArticleHTMLSnippet() throws Exception {
        final Page article = Mockito.mock(Page.class);
        final Node articleNode = Mockito.mock(Node.class);
        final Node textNode = Mockito.mock(Node.class);
        final Property property = Mockito.mock(Property.class);

        final String HTML_STRING = "<p>some valid <span>html</span> string</p>";

        when(article.adaptTo(any(Class.class))).thenReturn(articleNode);
        when(articleNode.hasNode(ArticleUtils.TEXT_NODE_PATH)).thenReturn(true);
        when(articleNode.getNode(ArticleUtils.TEXT_NODE_PATH)).thenReturn(textNode);

        when(textNode.hasProperty(ArticleUtils.TEXT_PROPERTY)).thenReturn(true);
        when(textNode.getProperty(ArticleUtils.TEXT_PROPERTY)).thenReturn(property);

        when(property.getString()).thenReturn(HTML_STRING);

        String snippet = ArticleUtils.extractSnippet(article);
        assertEquals(HTML_STRING, snippet);
    }

    @Test
    public void extractArticlePlaintextSnippet() throws Exception {
        final Page article = Mockito.mock(Page.class);
        final Node articleNode = Mockito.mock(Node.class);
        final Node textNode = Mockito.mock(Node.class);
        final Property property = Mockito.mock(Property.class);

        final String HTML_STRING = "<p>some valid <span>html</span> string</p>";
        final String PLAINTEXT_STRING = "some valid html string";

        when(article.adaptTo(any(Class.class))).thenReturn(articleNode);
        when(articleNode.hasNode(ArticleUtils.TEXT_NODE_PATH)).thenReturn(true);
        when(articleNode.getNode(ArticleUtils.TEXT_NODE_PATH)).thenReturn(textNode);

        when(textNode.hasProperty(ArticleUtils.TEXT_PROPERTY)).thenReturn(true);
        when(textNode.getProperty(ArticleUtils.TEXT_PROPERTY)).thenReturn(property);

        when(property.getString()).thenReturn(HTML_STRING);

        String snippet = ArticleUtils.extractPlaintextSnippet(article);
        assertEquals(PLAINTEXT_STRING, snippet);
    }

    @Test
    public void nullSnippetIfArticleDoesntHaveTextNode() throws Exception {
        final Page article = Mockito.mock(Page.class);
        final Node articleNode = Mockito.mock(Node.class);

        when(article.adaptTo(any(Class.class))).thenReturn(articleNode);
        when(articleNode.hasNode(ArticleUtils.TEXT_NODE_PATH)).thenReturn(false);

        String snippet = ArticleUtils.extractSnippet(article);
        assertEquals(null, snippet);
    }

    @Test
    public void nullSnippetIfArticleDoesntHaveTextProperty() throws Exception {
        final Page article = Mockito.mock(Page.class);
        final Node articleNode = Mockito.mock(Node.class);
        final Node textNode = Mockito.mock(Node.class);

        when(article.adaptTo(any(Class.class))).thenReturn(articleNode);
        when(articleNode.hasNode(ArticleUtils.TEXT_NODE_PATH)).thenReturn(true);
        when(articleNode.getNode(ArticleUtils.TEXT_NODE_PATH)).thenReturn(textNode);

        when(textNode.hasProperty(ArticleUtils.TEXT_PROPERTY)).thenReturn(false);

        String snippet = ArticleUtils.extractSnippet(article);
        assertEquals(null, snippet);
    }
}
