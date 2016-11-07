package org.strut.amway.core.util;

import org.slf4j.Logger;

import com.day.cq.wcm.api.Page;
import com.google.common.base.Preconditions;

public class CategoryUtils {

	private static Logger log = org.slf4j.LoggerFactory
			.getLogger(CategoryUtils.class);

	/**
	 * @param articlePage
	 *            assumes a path structure like
	 *            {@code variable-sized/language/cat-name/sub-cat-name/article-name}
	 * @return {@code language}
	 */
	public static String getLanguage(Page articlePage) {
		assertArticlePage(articlePage);
		return articlePage.getParent(3).getName();
	}

	/**
	 * @param articlePage
	 *            assumes a path structure like
	 *            {@code variable-sized/language/cat-name/sub-cat-name/article-name}
	 * @return {@code language/cat-name}
	 */
	public static String getCategoryPath(Page articlePage) {
		assertArticlePage(articlePage);
		String catAbsPath = articlePage.getParent(2).getPath();
		String variablePath = articlePage.getParent(4).getPath();
		return catAbsPath.substring(variablePath.length() + 1);
	}

	/**
	 * @param articlePage
	 *            assumes a path structure like
	 *            {@code variable-sized/language/cat-name/sub-cat-name/article-name}
	 * @return {@code cat-name}
	 */
	public static String getCategoryName(Page articlePage) {

		assertArticlePage(articlePage);
		return articlePage.getParent(2).getName();
	}


	/**
	 * @param iframePage
	 *            assumes a path structure like
	 *            {@code variable-sized/language/cat-name/sub-cat-name/article-name}
	 * @return {@code cat-name}
	 */
	public static String getIframeCategoryName(Page iframePage) {

		assertIframePage(iframePage);
		return iframePage.getParent(2).getName();
	}
	
	
	/**
	 * Tries to return the category name for a given page. Assumes the content
	 * structure specified in {@link CategoryConstants}. A category might not be
	 * available for this page.
	 *
	 * @param page
	 *            any page
	 * @return {@code cat-name} or null
	 */
	public static String getCategoryNameIfPossible(Page page) {
		Page catPage = page.getAbsoluteParent(CategoryConstants.CATEGORY_LEVEL);
		return catPage != null ? catPage.getName() : null;
	}

	/**
	 * @param articlePage
	 *            assumes a path structure like
	 *            {@code variable-sized/language/cat-name/sub-cat-name/article-name}
	 * @return {@code language/cat-name/sub-cat-name}
	 */
	public static String getSubCategoryPath(Page articlePage) {
		assertArticlePage(articlePage);
		String subCatAbsPath = articlePage.getParent(1).getPath();
		String variablePath = articlePage.getParent(4).getPath();
		return subCatAbsPath.substring(variablePath.length() + 1);
	}

	/**
	 * @param articlePage
	 *            assumes a path structure like
	 *            {@code variable-sized/language/cat-name/sub-cat-name/article-name}
	 * @return {@code sub-cat-name}
	 */
	public static String getSubCategoryName(Page articlePage) {
		assertArticlePage(articlePage);
		return articlePage.getParent(1).getName();
	}
	
	
	/**
	 * @param iframePage
	 *            assumes a path structure like
	 *            {@code variable-sized/language/cat-name/sub-cat-name/article-name}
	 * @return {@code sub-cat-name}
	 */
	public static String getIframeSubCategoryName(Page iframePage) {
		assertIframePage(iframePage);
		return iframePage.getParent(1).getName();
	}
	
	/**
	 * Tries to return the subcategory name for a given page. Assumes the
	 * content structure specified in {@link CategoryConstants}. A sub category
	 * might not be available for this page.
	 *
	 * @param page
	 *            any page
	 * @return {@code sub-cat-name} or null
	 */
	public static String getSubCategoryNameIfPossible(Page page) {
		Page catPage = page
				.getAbsoluteParent(CategoryConstants.SUB_CATEGORY_LEVEL);
		return catPage != null ? catPage.getName() : null;
	}

	private static void assertArticlePage(Page articlePage) {
		Preconditions.checkNotNull(articlePage);
		log.info("Resource Type"
				+ articlePage.getContentResource().getResourceType());
		boolean isArticle = articlePage.getContentResource().isResourceType(
				ArticleUtils.ARTICLE_RESOURCE_TYPE)
				|| articlePage.getContentResource().isResourceType(
						ArticleUtils.SEA_ARTICLE_RESOURCE_TYPE);
		log.info("Condition>>>" + isArticle);
		Preconditions.checkArgument(isArticle);
	}
	
	private static void assertIframePage(Page iframePage) {
		Preconditions.checkNotNull(iframePage);
		log.info("Resource Type"
				+ iframePage.getContentResource().getResourceType());
		boolean isIframe = iframePage.getContentResource().isResourceType(
				IframeUtils.IFRAME_RESOURCE_TYPE)
				|| iframePage.getContentResource().isResourceType(
						IframeUtils.SEA_IFRAME_RESOURCE_TYPE);
		log.info("Condition>>>" + isIframe);
		Preconditions.checkArgument(isIframe);
	}
}
