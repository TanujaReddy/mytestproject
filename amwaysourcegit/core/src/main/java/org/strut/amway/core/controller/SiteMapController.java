package org.strut.amway.core.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.sling.api.SlingHttpServletRequest;
import org.strut.amway.core.model.CategoryModel;
import org.strut.amway.core.model.SubCategory;
import org.strut.amway.core.util.CategoryConstants;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageFilter;
import com.day.cq.wcm.api.designer.Style;
import com.day.cq.wcm.foundation.Navigation;
import com.day.cq.wcm.foundation.Navigation.Element;

/**
 * Handle when user click on site-map link
 */
public class SiteMapController {

	private Page currentPage;
	private Style currentStyle;
	private SlingHttpServletRequest request;

	public SiteMapController(Page currentPage, Style currentStyle,
			SlingHttpServletRequest request) {
		this.currentPage = currentPage;
		this.currentStyle = currentStyle;
		this.request = request;
	}

	/**
	 * Get all categories and sub-categories.
	 * 
	 * @return list categories and sub-categories.
	 */
	public List<CategoryModel> getCategories() {
		int absParent = currentStyle.get(CategoryConstants.ABS_PARENT,
				CategoryConstants.ABS_LEVEL);
		PageFilter filter = new PageFilter(request);
		Navigation navigation = new Navigation(currentPage, absParent, filter,
				CategoryConstants.TOP_NAVIGATION_DEPTH);
		return getCategoriesInNav(navigation);
	}

	/**
	 * Get all categories and sub-categories follow navigation.
	 * 
	 * @param navigation
	 * @return List<CategoryModel> in navigation
	 */
	public List<CategoryModel> getCategoriesInNav(Navigation navigation) {
		List<CategoryModel> models = new ArrayList<>();
		if (navigation != null) {
			for (Element element : navigation) {
				Page categoryPage = element.getPage();
				if (categoryPage != null && !categoryPage.isHideInNav()) {
					CategoryModel categoryModel = new CategoryModel();
					StringBuilder categoryUrl = new StringBuilder("#");
					if ( element.getPath() != null) {
						categoryUrl = new StringBuilder(element.getPath()).append(".html");
					}
					categoryModel.setUrl(categoryUrl.toString());
					categoryModel.setTitle(element.getTitle());
					Iterator<Page> subCategories = categoryPage.listChildren();
					while (subCategories.hasNext()) {
						Page childPage = subCategories.next();
						StringBuilder subCategoryUrl = new StringBuilder("#");
						if (!childPage.isHideInNav()) {
							if (childPage.getPath() != null) {
								subCategoryUrl = new StringBuilder(childPage.getPath()).append(".html");
							}
							String subCategoryTitle = childPage.getTitle();
							SubCategory subCategory = new SubCategory(
									subCategoryTitle, subCategoryUrl.toString());
							categoryModel.getSubCategories().add(subCategory);
						}
					}
					if (!models.contains(categoryModel)) {
						models.add(categoryModel);
					}
				}
			}
		}
		return models;
	}
}
