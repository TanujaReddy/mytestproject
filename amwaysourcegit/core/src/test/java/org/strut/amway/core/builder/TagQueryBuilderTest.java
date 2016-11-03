package org.strut.amway.core.builder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.strut.amway.core.model.TagCriteria;

public class TagQueryBuilderTest {

    @Test
    public void testBuildSearchTagQueryWithExactMatch() {
        TagCriteria tagCriteria = new TagCriteria.Builder().setTitle("title").setExactMatch(true).build();

        String expected =
                "SELECT [cq:Tag] FROM [cq:Tag] AS tag WHERE ISDESCENDANTNODE([/etc/tags]) AND (LOWER(tag.[jcr:title.en_au]) = 'title' OR (tag.[jcr:title.en_au] IS NULL AND LOWER(tag.[jcr:title]) = 'title'))";

        String actual = TagQueryBuilder.buildSearchTagQuery(tagCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchTagQueryWithPartialMatch() {
        TagCriteria tagCriteria = new TagCriteria.Builder().setTitle("title").setExactMatch(false).build();

        String expected =
                "SELECT [cq:Tag] FROM [cq:Tag] AS tag WHERE ISDESCENDANTNODE([/etc/tags]) AND (LOWER(tag.[jcr:title.en_au]) LIKE '%title%' OR (tag.[jcr:title.en_au] IS NULL AND LOWER(tag.[jcr:title]) LIKE '%title%'))";

        String actual = TagQueryBuilder.buildSearchTagQuery(tagCriteria);

        assertEquals(expected, actual);
    }

    @Test
    public void testBuildSearchTagQueryWithTagCriteriaIsNull() {
        String expected = "";

        String actual = TagQueryBuilder.buildSearchTagQuery(null);

        assertEquals(expected, actual);
    }

}
