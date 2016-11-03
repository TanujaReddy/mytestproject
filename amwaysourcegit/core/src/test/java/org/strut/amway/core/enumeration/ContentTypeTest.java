package org.strut.amway.core.enumeration;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ContentTypeTest {

    @Test
    public void shouldSetJsonContentTypeProperly() {
        final ContentType classUnderTest = ContentType.APPLICATION_JSON;

        assertEquals("application/json", classUnderTest.getValue());
    }

}
