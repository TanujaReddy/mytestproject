package org.strut.amway.core.model;

import org.strut.amway.core.enumeration.EntityType;

public class Account {

    private String userName;
    private EntityType entityType;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public EntityType getEntityType() {
        return entityType;
    }

    public void setEntityType(EntityType entityType) {
        this.entityType = entityType;
    }

}
