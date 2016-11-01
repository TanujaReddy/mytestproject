package org.strut.amway.core.enumeration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ArticleLabelTypeTest {

    @Test
    public void shouldReturnCorrectArticleLabelForLabel_Public() {
        String label = "Public";
        final ArticleLabelType result = ArticleLabelType.resolve(label);
        assertEquals(ArticleLabelType.PUBLIC, result);
    }

    @Test
    public void shouldReturnCorrectArticleLabelForLabel_Private() {
        String label = "Private";
        final ArticleLabelType result = ArticleLabelType.resolve(label);
        assertEquals(ArticleLabelType.PRIVATE, result);
    }

    @Test
    public void shouldReturnCorrectArticleLabelForLabel_IBO() {
        String label = "IBO";
        final ArticleLabelType result = ArticleLabelType.resolve(label);
        assertEquals(ArticleLabelType.IBO, result);
    }

    @Test
    public void shouldReturnCorrectArticleLabelForLabel_Client() {
        String label = "Client";
        final ArticleLabelType result = ArticleLabelType.resolve(label);
        assertEquals(ArticleLabelType.CLIENT, result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenInvalidLabel() {
        String label = "0";
        ArticleLabelType.resolve(label);
    }

}
