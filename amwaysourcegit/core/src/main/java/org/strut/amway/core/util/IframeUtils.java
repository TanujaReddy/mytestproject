package org.strut.amway.core.util;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.wcm.api.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.commons.JcrUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.util.ArrayList;
import java.util.GregorianCalendar;

import java.util.List;
import java.util.Locale;
import com.day.cq.tagging.Tag;

public final class IframeUtils {

    private static Logger LOGGER = LoggerFactory.getLogger(IframeUtils.class);
    static final String TEXT_NODE_PATH = "jcr:content/iframe-content-text-center/text";
    static final String TEXT_PROPERTY = "text";

    static final String IFRAME_RESOURCE_TYPE = "corporate/amway-today/components/iframe-page";
    static final String SEA_IFRAME_RESOURCE_TYPE = "/apps/corporate/amway-today/components/sea/iframe-page";

    public static final String SOCIAL_WHATSAPP_PROP = "socialWhatsapp";
    public static final String SOCIAL_WECHAT_PROP = "socialWechat";
    public static final String SOCIAL_LINE_PROP = "socialLine";

    /**
     * See {@link org.strut.amway.core.controller.HeroArticleController} to bring date formatting functions here.
     */
    public static GregorianCalendar getPublishedDate(final Page iframe) {
        if (iframe == null) {
            throw new IllegalArgumentException("iframe param can not null");
        }

        final Object publishedDate = iframe.getProperties().get(ArticleConstants.PUBLISH_DATE);
        if (publishedDate != null) {
            return (GregorianCalendar) publishedDate;
        }
        return null;
    }

    /**
     * @return Snippet or null if can't extract one. Can return html text. Defaulting to "" is up to the caller.
     */
    public static String extractSnippet(Page iframe) {
        String result = null;
        try {
            final Node iframeNode = iframe.adaptTo(Node.class);

            if (iframeNode.hasNode(TEXT_NODE_PATH)) {
                Node textNode = iframeNode.getNode(TEXT_NODE_PATH);
                if (textNode.hasProperty(TEXT_PROPERTY)) {
                    result = textNode.getProperty(TEXT_PROPERTY).getString();
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Couldn't generate snippet for iframe {}", iframe.getPath(), e);
        }

        return result;
    }

    /**
     * @return Snippet or null if can't extract one. HTML tags are fully stripped. Defaulting to "" is up to the caller.
     */
    public static String extractPlaintextSnippet(Page iframe) {
        String result = extractSnippet(iframe);
        if (StringUtils.isNotEmpty(result)) {
            result = Jsoup.clean(result, Whitelist.none());
        }
        return result;
    }

    /**
     * @return List of iframe tag titles or null if can't extract one. Can return html text. Defaulting to "" is up to the caller.
     */
    public static List<String> extractTags(Page iframe) {
        List<String> result = new ArrayList<>();

        try {
            Locale locale = iframe.getLanguage(false);

            Tag[] tags = iframe.getTags();

            if(tags != null){
                for (Tag tag : tags) {
                    result.add(tag.getTitle(locale));
                }

            }

        } catch (Exception e) {
            LOGGER.warn("Couldn't generate tags for iframe {}", iframe.getPath(), e);
        }

        return result;
    }

    /**
     * @param page any page
     */
    public static boolean enableWhatsapp(Page page) {
        try {
            Node siteNode = (Node) page.adaptTo(Node.class).getAncestor(CategoryConstants.SITE_LEVEL + 2); // api level mismatch
            return JcrUtils.getBooleanProperty(siteNode, JcrConstants.JCR_CONTENT + "/" + SOCIAL_WHATSAPP_PROP, true);
        } catch (RepositoryException e) {
            LOGGER.error("Error getting Whatsapp property", e);
            return true;
        }
    }

    /**
     * @param page any page
     */
    public static boolean enableLine(Page page) {
        try {
            Node siteNode = (Node) page.adaptTo(Node.class).getAncestor(CategoryConstants.SITE_LEVEL + 2); // api level mismatch
            return JcrUtils.getBooleanProperty(siteNode, JcrConstants.JCR_CONTENT + "/" + SOCIAL_LINE_PROP, true);
        } catch (RepositoryException e) {
            LOGGER.error("Error getting Line property", e);
            return true;
        }
    }
}

