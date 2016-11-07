package org.strut.amway.core.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import com.day.cq.tagging.Tag;

public final class TagUtils {

    public static List<String> getTagIds(final List<Tag> tags) {
        final List<String> tagIds = new ArrayList<String>();
        if (CollectionUtils.isNotEmpty(tags)) {
            for (Tag tag : tags) {
                tagIds.add(tag.getTagID());
            }
        }
        return tagIds;
    }

    public static List<String> getTagIds(final Tag[] tags) {
        if (tags == null) {
            return null;
        }
        return getTagIds(Arrays.asList(tags));
    }

}
