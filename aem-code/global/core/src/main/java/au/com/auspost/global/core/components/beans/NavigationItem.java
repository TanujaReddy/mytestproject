package au.com.auspost.global.core.components.beans;


import java.util.ArrayList;
import java.util.List;

/**
 * A bean object to represent a link in the navigation.
 */
public class NavigationItem {

	private String title;

	private String link;
	
	private boolean active;

	private boolean follow;

	private boolean hideInNav;

	private List<NavigationItem> children = new ArrayList<NavigationItem>();

	private String name;

	private String summary;

	private String image;
	
	private String path;

	private int itemDepth;

	private int firstItem;

	public NavigationItem() {
	}

	/**
	 * NavigationItem Constructor.
	 *
	 * @param title         link text
	 * @param link          link path
	 * @param active        the boolean value to add active style class
	 * @param follow        the boolean value to add rel attribute
	 * @param hideInNav     the boolean value to render the item
	 * @param megaMenuTitle
	 */
	public NavigationItem(String title,String link,String pagePath, boolean active, int firstItem,
						  boolean follow, boolean hideInNav, String name, String summary, String image) {
		setTitle(title);
		setActive(active);
		setFirstItem(firstItem);
		setLink(link);
		setFollow(follow);
		setHideInNav(hideInNav);
		setName(name);
		setSummary(summary);
		setImage(image);
		setPath(pagePath);
	}


	public int getFirstItem() {
		return firstItem;
	}

	public void setFirstItem(int firstItem) {
		this.firstItem = firstItem;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public List<NavigationItem> getChildren() {
		return children;
	}

	public int getItemDepth() {
		return itemDepth;
	}

	public void setItemDepth(int itemDepth) {
		this.itemDepth = itemDepth;
	}

	/**
	 * @param item child object of current object
	 */
	public void addChild(NavigationItem item) {
		children.add(item);
	}

	public boolean isFollow() {
		return follow;
	}

	public void setFollow(boolean nofollow) {
		this.follow = nofollow;
	}

	public boolean isHideInNav() {
		return hideInNav;
	}

	public void setHideInNav(boolean hideInNav) {
		this.hideInNav = hideInNav;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public void setChildren(List<NavigationItem> children) {
		this.children = children;
	}

}
