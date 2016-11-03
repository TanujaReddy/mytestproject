package org.strut.amway.au.integration.util;

import org.strut.amway.core.enumeration.EntityType;

public final class EntityTypeUtils {

    private static final String EMPLOYEE = "EMPLOYEE";
    private static final String CLIENT = "CLIENT";

    public static EntityType resolve(final String entityTypeInStr) {
        if (entityTypeInStr == null) {
            return null;
        }

        if (EMPLOYEE.equals(entityTypeInStr)) {
            return EntityType.IBO;
        } else if (CLIENT.equals(entityTypeInStr)) {
            return EntityType.CLIENT;
        }
        return null;
    }

}
