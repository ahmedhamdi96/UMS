package com.sumerge.program.models;

import com.sumerge.program.entities.User;

import java.util.Collection;
import java.util.stream.Collectors;

public class UserModel {

    private String firstName;
    private String lastName;
    private String email;
    private Collection<GroupModel> groups;

    public UserModel() {
    }

    public UserModel(User user) {
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.email = user.getEmail();
        this.groups = user.getGroups().stream().map(g->new GroupModel(g)).collect(Collectors.toList());
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

    public Collection<GroupModel> getGroups() {
        return groups;
    }

    public void setGroups(Collection<GroupModel> groups) {
        this.groups = groups;
    }
}
