package org.strut.amway.core.util;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.enumeration.ArticleLabelType;
import org.strut.amway.core.enumeration.EntityType;
import org.strut.amway.core.model.Account;

public class ArticleLabelUtilsTest {

    ArticleLabelUtils classUnderTest;

    @Mock
    HttpSession httpSession;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldReturnProperArticleListForUnLoginedUser() {
        final Account user = null;
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(user);

        final List<ArticleLabelType> articleLabelTypes = ArticleLabelUtils.getArticleLabelBySession(httpSession);

        assertEquals(1, articleLabelTypes.size());
        assertEquals(ArticleLabelType.PUBLIC, articleLabelTypes.get(0));
    }

    @Test
    public void shouldReturnProperArticleListWhenSessionIsNull() {
        httpSession = null;

        final List<ArticleLabelType> articleLabelTypes = ArticleLabelUtils.getArticleLabelBySession(httpSession);

        assertEquals(1, articleLabelTypes.size());
        assertEquals(ArticleLabelType.PUBLIC, articleLabelTypes.get(0));
    }

    @Test
    public void shouldReturnProperArticleListForLoginedUser() {
        final Account user = new Account();
        user.setUserName("hadoan");
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(user);

        final List<ArticleLabelType> articleLabelTypes = ArticleLabelUtils.getArticleLabelBySession(httpSession);

        assertEquals(2, articleLabelTypes.size());
        assertEquals(ArticleLabelType.PUBLIC, articleLabelTypes.get(0));
        assertEquals(ArticleLabelType.PRIVATE, articleLabelTypes.get(1));
    }

    @Test
    public void shouldReturnProperArticleListForLoginedUserWithIBOEntityType() {
        final Account user = new Account();
        user.setUserName("hadoan");
        user.setEntityType(EntityType.IBO);
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(user);

        final List<ArticleLabelType> articleLabelTypes = ArticleLabelUtils.getArticleLabelBySession(httpSession);

        assertEquals(3, articleLabelTypes.size());
        assertEquals(ArticleLabelType.PUBLIC, articleLabelTypes.get(0));
        assertEquals(ArticleLabelType.PRIVATE, articleLabelTypes.get(1));
        assertEquals(ArticleLabelType.IBO, articleLabelTypes.get(2));
    }

    @Test
    public void shouldReturnProperArticleListForLoginedUserWithClientEntityType() {
        final Account user = new Account();
        user.setUserName("hadoan");
        user.setEntityType(EntityType.CLIENT);
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(user);

        final List<ArticleLabelType> articleLabelTypes = ArticleLabelUtils.getArticleLabelBySession(httpSession);

        assertEquals(3, articleLabelTypes.size());
        assertEquals(ArticleLabelType.PUBLIC, articleLabelTypes.get(0));
        assertEquals(ArticleLabelType.PRIVATE, articleLabelTypes.get(1));
        assertEquals(ArticleLabelType.CLIENT, articleLabelTypes.get(2));
    }

}
