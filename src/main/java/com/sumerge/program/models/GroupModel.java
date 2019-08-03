package com.sumerge.program.models;

import com.sumerge.program.entities.Group;

public class GroupModel {

    private String name;
    private String description;

    public GroupModel() {
    }

    public GroupModel(Group group) {
        this.name = group.getName();
        this.description = group.getDescription();
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
}
