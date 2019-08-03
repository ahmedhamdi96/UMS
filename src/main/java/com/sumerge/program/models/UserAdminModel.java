package com.sumerge.program.models;

import com.sumerge.program.entities.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserAdminModel {

    private Integer userId;
    private String firstName;
    private String lastName;
    private String email;
    private Boolean admin;
    private Boolean active;
    private Collection<GroupAdminModel> groups;

    public UserAdminModel() {
    }

    public UserAdminModel(User user) {
        this.userId = user.getUserId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.admin = user.getAdmin();
        this.active = user.getActive();
        this.groups = user.getGroups().stream().map(g->new GroupAdminModel(g)).collect(Collectors.toList());
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getAdmin() {
        return admin;
    }

    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Collection<GroupAdminModel> getGroups() {
        return groups;
    }

    public void setGroups(Collection<GroupAdminModel> groups) {
        this.groups = groups;
    }
}
