package com.sumerge.program.models;

import com.sumerge.program.entities.Group;

public class GroupAdminModel {

    private Integer groupId;
    private String name;
    private String description;
    private Boolean active;

    public GroupAdminModel() {
    }

    public GroupAdminModel(Group group) {
        this.groupId = group.getGroupId();
        this.name = group.getName();
        this.description = group.getDescription();
        this.active = group.getActive();
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
