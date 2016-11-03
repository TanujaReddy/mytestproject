package org.strut.amway.au.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;
import org.strut.amway.au.integration.util.EntityTypeUtils;
import org.strut.amway.core.enumeration.EntityType;

public class EntityTypeUtilsTest {

    @Test
    public void shouldResolveProperWhenEntityIsEmployee() {
        final String employee = "EMPLOYEE";
        final EntityType entityType = EntityTypeUtils.resolve(employee);
        assertEquals(EntityType.IBO, entityType);
    }

    @Test
    public void shouldResolveProperWhenEntityIsClient() {
        final String client = "CLIENT";
        final EntityType entityType = EntityTypeUtils.resolve(client);
        assertEquals(EntityType.CLIENT, entityType);
    }

    @Test
    public void shouldReturnNullWhenParamIsNull() {
        final String entityTypeInStr = null;
        final EntityType entityType = EntityTypeUtils.resolve(entityTypeInStr);
        assertNull(entityType);
    }

    @Test
    public void shouldReturnNullWhenParamIsNotMatch() {
        final String entityTypeInStr = "Boss";
        final EntityType entityType = EntityTypeUtils.resolve(entityTypeInStr);
        assertNull(entityType);
    }

}
