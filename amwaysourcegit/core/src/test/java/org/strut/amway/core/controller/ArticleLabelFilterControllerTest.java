package org.strut.amway.core.controller;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import javax.servlet.http.HttpSession;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.wrappers.ValueMapDecorator;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.strut.amway.core.enumeration.ArticleLabelType;
import org.strut.amway.core.enumeration.EntityType;
import org.strut.amway.core.model.Account;
import org.strut.amway.core.util.AuthenticationConstants;
import org.strut.amway.core.util.CategoryConstants;

import com.day.cq.wcm.api.Page;

public class ArticleLabelFilterControllerTest {

    ArticleLabelFilterController classUnderTest;

    @Mock
    Page currentPage;

    @Mock
    Page homePage;

    String homePagePath;

    @Mock
    HttpSession httpSession;

    @Mock
    ResourceResolver resourceResolver;

    @Mock
    SlingHttpServletRequest request;

    @Mock
    SlingHttpServletResponse response;

    boolean isPublishedInstance;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        classUnderTest = new ArticleLabelFilterController();
        isPublishedInstance = true;

        homePagePath = "/content/amway-today/en";
        when(currentPage.getAbsoluteParent(CategoryConstants.ABS_LEVEL)).thenReturn(homePage);
        when(homePage.getPath()).thenReturn(homePagePath);
        when(request.getSession()).thenReturn(httpSession);
        when(request.getResourceResolver()).thenReturn(resourceResolver);
        when(resourceResolver.map(eq(homePagePath))).thenReturn(homePagePath);
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldAllowToViewArticlePageWithPublicLabelWhenUserHasNotLogined() throws Exception {
        final ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("label", new String[] { ArticleLabelType.PUBLIC.getLabel() });
            }
        });
        when(currentPage.getProperties()).thenReturn(new ValueMapDecorator(pageValueMap));

        classUnderTest.checkUserIsEligibleForViewArticlePage(currentPage, request, response, isPublishedInstance);

        verify(response, times(0)).sendRedirect(homePagePath + ".html");
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldAllowToViewArticlePageWithPublicLabelWhenUserAlreadyLogined() throws Exception {
        final ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("label", new String[] { ArticleLabelType.PUBLIC.getLabel() });
            }
        });
        when(currentPage.getProperties()).thenReturn(new ValueMapDecorator(pageValueMap));

        Account loginedUser = createAccountWithEntityType("hadoan", null);
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);

        classUnderTest.checkUserIsEligibleForViewArticlePage(currentPage, request, response, isPublishedInstance);

        verify(response, times(0)).sendRedirect(homePagePath + ".html");
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldAllowToViewArticlePageWithPrivateLabelWhenUserAlreadyLogined() throws Exception {
        final ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("label", new String[] { ArticleLabelType.PRIVATE.getLabel() });
            }
        });
        when(currentPage.getProperties()).thenReturn(new ValueMapDecorator(pageValueMap));

        Account loginedUser = createAccountWithEntityType("hadoan", null);
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);

        classUnderTest.checkUserIsEligibleForViewArticlePage(currentPage, request, response, isPublishedInstance);

        verify(response, times(0)).sendRedirect(homePagePath + ".html");
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldAllowToViewArticlePageWithIBOLabelWhenIBOUserLogined() throws Exception {
        final ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("label", new String[] { ArticleLabelType.IBO.getLabel() });
            }
        });
        when(currentPage.getProperties()).thenReturn(new ValueMapDecorator(pageValueMap));

        Account loginedUser = createAccountWithEntityType("hadoan", EntityType.IBO);
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);

        classUnderTest.checkUserIsEligibleForViewArticlePage(currentPage, request, response, isPublishedInstance);

        verify(response, times(0)).sendRedirect(homePagePath + ".html");
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldAllowToViewArticlePageWithIBOLabelWhenClientUserLogined() throws Exception {
        final ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("label", new String[] { ArticleLabelType.CLIENT.getLabel() });
            }
        });
        when(currentPage.getProperties()).thenReturn(new ValueMapDecorator(pageValueMap));

        Account loginedUser = createAccountWithEntityType("hadoan", EntityType.CLIENT);
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);

        classUnderTest.checkUserIsEligibleForViewArticlePage(currentPage, request, response, isPublishedInstance);

        verify(response, times(0)).sendRedirect(homePagePath + ".html");
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldAllowToViewArticlePageWithIBOAndClientLabelWhenIBOUserLogined() throws Exception {
        final ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("label", new String[] { ArticleLabelType.IBO.getLabel(), ArticleLabelType.CLIENT.getLabel() });
            }
        });
        when(currentPage.getProperties()).thenReturn(new ValueMapDecorator(pageValueMap));

        Account loginedUser = createAccountWithEntityType("hadoan", EntityType.IBO);
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);

        classUnderTest.checkUserIsEligibleForViewArticlePage(currentPage, request, response, isPublishedInstance);

        verify(response, times(0)).sendRedirect(homePagePath + ".html");
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldAllowToViewArticlePageWithIBOAndClientLabelWhenClientUserLogined() throws Exception {
        final ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("label", new String[] { ArticleLabelType.IBO.getLabel(), ArticleLabelType.CLIENT.getLabel() });
            }
        });
        when(currentPage.getProperties()).thenReturn(new ValueMapDecorator(pageValueMap));

        Account loginedUser = createAccountWithEntityType("hadoan", EntityType.CLIENT);
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);

        classUnderTest.checkUserIsEligibleForViewArticlePage(currentPage, request, response, isPublishedInstance);

        verify(response, times(0)).sendRedirect(homePagePath + ".html");
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldNotAllowToViewArticlePageWithPrivateLabelWhenUserHasNotLogined() throws Exception {
        final ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("label", new String[] { ArticleLabelType.PRIVATE.getLabel() });
            }
        });
        when(currentPage.getProperties()).thenReturn(new ValueMapDecorator(pageValueMap));

        classUnderTest.checkUserIsEligibleForViewArticlePage(currentPage, request, response, isPublishedInstance);

        verify(response, times(1)).sendRedirect(homePagePath + ".html");
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldNotAllowToViewArticlePageWithIBOLabelWhenAnotherUserLogined() throws Exception {
        final ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("label", new String[] { ArticleLabelType.IBO.getLabel() });
            }
        });
        when(currentPage.getProperties()).thenReturn(new ValueMapDecorator(pageValueMap));

        Account loginedUser = createAccountWithEntityType("hadoan", null);
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);

        classUnderTest.checkUserIsEligibleForViewArticlePage(currentPage, request, response, isPublishedInstance);

        verify(response, times(1)).sendRedirect(homePagePath + ".html");
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldNotAllowToViewArticlePageWithClientLabelWhenAnotherUserLogined() throws Exception {
        final ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("label", new String[] { ArticleLabelType.CLIENT.getLabel() });
            }
        });
        when(currentPage.getProperties()).thenReturn(new ValueMapDecorator(pageValueMap));

        Account loginedUser = createAccountWithEntityType("hadoan", null);
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);

        classUnderTest.checkUserIsEligibleForViewArticlePage(currentPage, request, response, isPublishedInstance);

        verify(response, times(1)).sendRedirect(homePagePath + ".html");
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldNotAllowToViewArticlePageWithClientLabelWhenIBOUserLogined() throws Exception {
        final ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("label", new String[] { ArticleLabelType.CLIENT.getLabel() });
            }
        });
        when(currentPage.getProperties()).thenReturn(new ValueMapDecorator(pageValueMap));

        Account loginedUser = createAccountWithEntityType("hadoan", EntityType.IBO);
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);

        classUnderTest.checkUserIsEligibleForViewArticlePage(currentPage, request, response, isPublishedInstance);

        verify(response, times(1)).sendRedirect(homePagePath + ".html");
    }

    @SuppressWarnings("serial")
    @Test
    public void shouldNotAllowToViewArticlePageWithIBOLabelWhenClientUserLogined() throws Exception {
        final ValueMap pageValueMap = new ValueMapDecorator(new HashMap<String, Object>() {
            {
                put("label", new String[] { ArticleLabelType.IBO.getLabel() });
            }
        });
        when(currentPage.getProperties()).thenReturn(new ValueMapDecorator(pageValueMap));

        Account loginedUser = createAccountWithEntityType("hadoan", EntityType.CLIENT);
        when(httpSession.getAttribute(AuthenticationConstants.LOGINED_USER)).thenReturn(loginedUser);

        classUnderTest.checkUserIsEligibleForViewArticlePage(currentPage, request, response, isPublishedInstance);

        verify(response, times(1)).sendRedirect(homePagePath + ".html");
    }

    @Test
    public void shouldAllowViewArticleOnAuthorInstance() throws Exception {
        isPublishedInstance = false;
        classUnderTest.checkUserIsEligibleForViewArticlePage(currentPage, request, response, isPublishedInstance);
        verify(response, times(0)).sendRedirect(homePagePath + ".html");
    }

    private Account createAccountWithEntityType(final String username, final EntityType entityType) {
        final Account loginedUser = new Account();
        loginedUser.setUserName(username);
        loginedUser.setEntityType(entityType);
        return loginedUser;
    }

}
