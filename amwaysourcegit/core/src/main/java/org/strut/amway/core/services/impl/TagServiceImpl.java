package org.strut.amway.core.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryManager;
import javax.jcr.query.QueryResult;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Service;
import org.apache.sling.api.resource.ResourceResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.strut.amway.core.builder.TagQueryBuilder;
import org.strut.amway.core.model.TagCriteria;
import org.strut.amway.core.services.TagService;

import com.day.cq.tagging.Tag;

@Service(value = TagService.class)
@Component(immediate = true, name="Amway Today SEA Login Service")
public class TagServiceImpl implements TagService {

    private static Logger LOGGER = LoggerFactory.getLogger(TagServiceImpl.class);

    @Override
    public List<Tag> search(ResourceResolver resourceResolver, TagCriteria tagCriterias) {
        try {
        	
            List<Tag> tags = new ArrayList<Tag>();

            if (resourceResolver != null && tagCriterias != null) {

                Session session = resourceResolver.adaptTo(Session.class);
                QueryManager queryManager = session.getWorkspace().getQueryManager();

                String expression = TagQueryBuilder.buildSearchTagQuery(tagCriterias);

                if (StringUtils.isNotEmpty(expression)) {

                    Query query = queryManager.createQuery(expression, Query.JCR_SQL2);
                    QueryResult queryResult = query.execute();

                    NodeIterator nodeIterator = queryResult.getNodes();
                    if (nodeIterator != null) {
                        while (nodeIterator.hasNext()) {
                            String path = nodeIterator.nextNode().getPath();
                            Tag tag = resourceResolver.getResource(path).adaptTo(Tag.class);
                            tags.add(tag);
                        }
                    }
                }
            }
            return tags;
        } catch (IllegalArgumentException | RepositoryException e) {
            LOGGER.error("Error while get  data " + ExceptionUtils.getStackTrace(e));
            return null;
        }
    }
}
