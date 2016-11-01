package org.strut.amway.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import com.day.cq.tagging.Tag;

public class TagUtilsTest {

    @Test
    public void shouldExtractTagIdProperly() {
        final List<Tag> tags = createTags();
        final List<String> tagIds = TagUtils.getTagIds(tags);

        assertEquals(2, tagIds.size());
        assertEquals("tagA", tagIds.get(0));
        assertEquals("tagB", tagIds.get(1));
    }

    @Test
    public void shouldExtractTagIdProperlyForTagArray() {
        final List<Tag> tags = createTags();
        final Tag[] tagArray = new Tag[] { tags.get(0), tags.get(1) };
        final List<String> tagIds = TagUtils.getTagIds(tagArray);

        assertEquals(2, tagIds.size());
        assertEquals("tagA", tagIds.get(0));
        assertEquals("tagB", tagIds.get(1));
    }

    @Test
    public void shouldReturnNullWhenTagArrayParamIsNull() {
        final Tag[] tagArray = null;
        final List<String> tagIds = TagUtils.getTagIds(tagArray);
        assertNull(tagIds);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldReturnEmptyCollectionWhenParamIsNullOrEmpty() {
        List<Tag> tags = Collections.EMPTY_LIST;
        List<String> tagIds = TagUtils.getTagIds(tags);
        assertEquals(0, tagIds.size());

        tags = null;
        tagIds = TagUtils.getTagIds(tags);
        assertEquals(0, tagIds.size());
    }

    private List<Tag> createTags() {
        final Tag tagA = Mockito.mock(Tag.class);
        when(tagA.getTagID()).thenReturn("tagA");

        final Tag tagB = Mockito.mock(Tag.class);
        when(tagB.getTagID()).thenReturn("tagB");

        final List<Tag> tags = new ArrayList<Tag>();
        tags.add(tagA);
        tags.add(tagB);
        return tags;
    }

}
