package org.strut.amway.core.services.impl;

import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import com.day.cq.replication.*;
import com.day.cq.wcm.api.NameConstants;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import com.day.cq.wcm.commons.Constants;
import com.day.cq.wcm.core.stats.PageViewStatistics;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Reference;
import org.apache.felix.scr.annotations.Service;
import org.apache.jackrabbit.commons.JcrUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.apache.sling.jcr.api.SlingRepository;
import org.apache.sling.jcr.resource.JcrResourceConstants;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.ComponentContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.services.ArticleService;
import org.strut.amway.core.services.ArticleStatisticsService;
import org.strut.amway.core.util.AEMUtils;
import org.strut.amway.core.util.ArticleConstants;

import javax.jcr.*;
import java.util.Calendar;
import java.util.UUID;

@Service(value = ArticleStatisticsService.class)
@Component
public class ArticleStatisticsServiceImpl implements ArticleStatisticsService {
	
	private static final String INSTANCE_ID = "instanceId";
	private static final String AMWAYTODAY_VAR = "/var/amway/amwaytoday"; 
	private static final String CORE_NODE = "core";

	private static Logger LOGGER = LoggerFactory.getLogger(ArticleStatisticsServiceImpl.class);
	
	private static String instanceId = null;
	
    @Reference
    SlingRepository repository;
    
    @Reference
    SlingSettingsService slingSettingsService;
    
    @Reference
    Replicator replicator; 
    
    @Reference
    ResourceResolverFactory resourceResolverFactory;
    
    @Reference
    PageViewStatistics pageViewStatistics; // used to migrate existing views
    
    @Reference
    ArticleService articleService; // used only to migrate existing likes
    
	@Override
	public long getAggregatedStatisticsLong(String statisticsName, String contentPath) {
		long aggregratedValue = 0;
		
		ResourceResolver resourceResolver = null;
		try {
			resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			Session session = resourceResolver.adaptTo(Session.class);
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			
			// in publish instances only, migrate v1 views/likes if we no views/likes statics exist for this page on this instance
			Node instanceContentNode = getUGCPageInstanceContentNode(session, contentPath);
			if (AEMUtils.isPublish(slingSettingsService) && (instanceContentNode == null || !instanceContentNode.hasProperty(statisticsName))) {
				instanceContentNode = getOrCreateUGCPageInstanceContentNode(session, pageManager, contentPath);
				migrateV1Statistics(session, pageManager, statisticsName, contentPath, instanceContentNode);
			}
			
			// Iterate through all instance nodes and aggregate the statistics value stored in jcr:content
			Node pageNode = getOrCreateUGCPageNode(session, contentPath);
			NodeIterator nodeIterator = pageNode.getNodes();
			while(nodeIterator.hasNext()) {
				Node instanceNode = nodeIterator.nextNode();
				if (instanceNode.hasNode(NameConstants.NN_CONTENT)) {
					Node contentNode = instanceNode.getNode(NameConstants.NN_CONTENT);
					if (contentNode.hasProperty(statisticsName)) {
						Property property = contentNode.getProperty(statisticsName);
						Value value = property.getValue();
						if (value != null && value.getType() == PropertyType.LONG)
							aggregratedValue += value.getLong();
					}
				}
			}
		} catch (LoginException e) {
			LOGGER.error("Could not get administrative resource resolver: " + e);
		} catch (RepositoryException e) {
			LOGGER.error("Error retrieving aggregated value: " + statisticsName, e);
		} catch (WCMException e) {
			LOGGER.error("Error retrieving page view statistics value: " + statisticsName, e);
		} finally {
			if (resourceResolver != null) {
				resourceResolver.close();
			}
		}

		return aggregratedValue;
	}

	private long migrateV1Statistics(Session session, PageManager pageManager, String statisticsName,
			String contentPath, Node instanceContentNode) throws WCMException, RepositoryException {
		long v1Value = getV1StatisticsValue(pageManager, statisticsName, contentPath);
		setStatisticsValue(instanceContentNode, statisticsName, v1Value, session.getUserID());
		session.save();
		LOGGER.info("Migrated V1 {} statistics for {}", statisticsName, contentPath);
		return v1Value;
	}
	
	@Override
	public long incrementStatistic(String statisticsName, String contentPath) {
		// For author instance we just retrieve the statistic but don't increment otherwise the increase will be replicated onto publish instances
		if (AEMUtils.isAuthor(slingSettingsService)) {
			return getAggregatedStatisticsLong(statisticsName, contentPath);
		}

		long currentValue = 0;

		ResourceResolver resourceResolver = null;
		try {
			resourceResolver = resourceResolverFactory.getAdministrativeResourceResolver(null);
			Session session = resourceResolver.adaptTo(Session.class);
			PageManager pageManager = resourceResolver.adaptTo(PageManager.class);
			
			Node instanceContentNode = getOrCreateUGCPageInstanceContentNode(session, pageManager, contentPath);
			if (instanceContentNode.hasProperty(statisticsName)) {
				Property property = instanceContentNode.getProperty(statisticsName);
				Value value = property.getValue();
				if (value != null && value.getType() == PropertyType.LONG)
					currentValue = value.getLong();
			} else {
				currentValue = migrateV1Statistics(session, pageManager, statisticsName, contentPath, instanceContentNode);
			}
			
			currentValue++;
			setStatisticsValue(instanceContentNode, statisticsName, currentValue, session.getUserID());
			session.save();

		} catch (RepositoryException e) {
			LOGGER.error("Error incrementing statistic: " + statisticsName, e);
		} catch (WCMException e) {
			LOGGER.error("Error incrementing statistic: " + statisticsName, e);
		} catch (LoginException e) {
			LOGGER.error("Error retrieving ResourceResolver: " + statisticsName, e);
		} finally  {
			if (resourceResolver != null) {
				resourceResolver.close();
			}
		}

		return currentValue;
	}

	protected void activate(ComponentContext context)
    {
    }
	
	protected void deactivate(ComponentContext context)
    {
    }

	private Page getOrCreateUGPageInstanceNode(Session session, PageManager pageManager, String contentPath) throws RepositoryException, WCMException {
		getOrCreateUGCPageNode(session, contentPath);
		
		Page page = pageManager.getPage(Constants.PATH_UGC + contentPath + "/" + getInstanceId(session));
		if (page == null) {
			page = pageManager.create(Constants.PATH_UGC + contentPath, getInstanceId(session), null, null);
		}
		return page;
	}
	
	private long getV1StatisticsValue(PageManager pageManager, String statisticsName, String contentPath) throws WCMException {
		long previousValue = 0L;
		if (ArticleConstants.STATISTIC_VIEWS.equals(statisticsName)) {
			previousValue = getPageViewStatistics(pageManager, contentPath); 
		} else if (ArticleConstants.STATISTIC_LIKES.equals(statisticsName)) {
			previousValue = articleService.getLikeNumberByArticleName(contentPath.replace("/", "-").substring(1));
		}
		return previousValue;
	}

	private long getPageViewStatistics(PageManager pageManager, String contentPath) throws WCMException {
		Page contentPage = pageManager.getPage(contentPath);
		if (contentPage != null) {
			Object[] values = pageViewStatistics.report(contentPage);
			return values != null && values.length > 2 ? (Long)values[2] : 0; // Can't find a constant for this magic number 2!
		}
		else {
			LOGGER.error("Error retrieving page view statistics. Could not find page for " + contentPath);
		}
		
		return 0;
	}

	private void setStatisticsValue(Node node, String statisticsName, long value, String userId) throws RepositoryException {
		node.setProperty(statisticsName, value);
		node.setProperty(NameConstants.PN_PAGE_LAST_MOD, Calendar.getInstance());
		node.setProperty(NameConstants.PN_PAGE_LAST_MOD_BY, userId);
		node.setProperty(AgentConfig.CQ_DISTRIBUTE, true);
	}

	private synchronized String getInstanceId(Session session) {
		if (instanceId != null) {
			return instanceId;
		}
		
        try {
			Node varNode = JcrUtils.getOrCreateByPath(AMWAYTODAY_VAR, JcrResourceConstants.NT_SLING_FOLDER, session);
			Node coreNode = JcrUtils.getOrAddNode(varNode, CORE_NODE, JcrConstants.NT_UNSTRUCTURED);

			if (coreNode.hasProperty(INSTANCE_ID)) {
				instanceId = JcrUtils.getStringProperty(coreNode, INSTANCE_ID, "default");
			} else {
				instanceId = UUID.randomUUID().toString();
				JcrUtil.setProperty(coreNode, INSTANCE_ID, instanceId);
				session.save();
			}

		} catch (RepositoryException e) {
			LOGGER.error("Error retrieving core/instanceId property from " + AMWAYTODAY_VAR, e);
		}
        
		return instanceId;
	}

	/**
	 * return the node at /content/usergenerated/content/...
	 * @param session
	 * @param contentPath
	 * @return
	 * @throws RepositoryException
	 */
	private Node getOrCreateUGCPageNode(Session session, String contentPath) throws RepositoryException {
		Node ugc = session.getNode(Constants.PATH_UGC);
		String relativePageNodePath = contentPath.charAt(0) == '/' ? contentPath.substring(1) : contentPath;
		Node pageNode = JcrUtils.getOrCreateByPath(ugc, relativePageNodePath, false, NameConstants.NT_PAGE, NameConstants.NT_PAGE, true);
		
		return pageNode;
	}

	private Node getUGCPageInstanceContentNode(Session session, String contentPath) throws RepositoryException {
		Node ugcPageNode = getOrCreateUGCPageNode(session, contentPath);
		Node ugcPageInstanceNode = JcrUtils.getNodeIfExists(ugcPageNode, getInstanceId(session));

		return ugcPageInstanceNode != null ? JcrUtils.getNodeIfExists(ugcPageInstanceNode, NameConstants.NN_CONTENT) : null;
	}
	
	private Node getOrCreateUGCPageInstanceContentNode(Session session, PageManager pageManager, String contentPath) throws WCMException, RepositoryException {
		Page ugcPageInstance = getOrCreateUGPageInstanceNode(session, pageManager, contentPath);
		Node instanceContentNode = null;
		Resource contentResource = ugcPageInstance.getContentResource();
		if (contentResource == null) {
			instanceContentNode = ugcPageInstance.adaptTo(Node.class).addNode(NameConstants.NN_CONTENT);
			session.save();
		} else {
			instanceContentNode = contentResource.adaptTo(Node.class);
		}
		
		return instanceContentNode;

	}
}
