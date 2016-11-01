package org.strut.amway.core.services;

import java.util.List;

import org.apache.sling.api.resource.ResourceResolver;
import org.strut.amway.core.model.TagCriteria;

import com.day.cq.tagging.Tag;

public interface TagService {

    public List<Tag> search(ResourceResolver resourceResolver, TagCriteria tagCriterias);

}
