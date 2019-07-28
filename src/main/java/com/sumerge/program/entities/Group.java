package com.sumerge.program.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "GROUPS", schema = "UMSDB")
public class Group implements Serializable {
    @Id
    @Column(name = "GROUP_ID")
    private Integer groupId;
    @Column(name = "NAME")
    private String name;
    @Column(name = "DESCRIPTION")
    private String description;
    @Column(name = "ACTIVE")
    private Boolean active;
    @JoinTable(name = "GROUPS_USERS", schema = "UMSDB",
            joinColumns = {@JoinColumn(name = "GROUP_ID")},
            inverseJoinColumns = {@JoinColumn(name = "USER_ID")})
    @ManyToMany()
    @JsonIgnore
    private Collection<User> users;

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

    public Collection<User> getUsers() {
        return users;
    }

    public void setUsers(Collection<User> users) {
        this.users = users;
    }
}
