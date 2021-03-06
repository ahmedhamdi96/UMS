package com.sumerge.program.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;

@Entity
@Table(name = "USERS", schema = "UMSDB")
@NamedQueries({
        @NamedQuery(name = "User.selectByEmail",
                    query = "SELECT u FROM User u WHERE u.email = :email and u.active = true"),
        @NamedQuery(name = "User.selectUser",
                query = "SELECT u FROM User u WHERE u.userId = :userId  and u.active = true"),
        @NamedQuery(name = "User.selectAllUsers",
                    query = "SELECT u FROM User u WHERE u.active = true"),
        @NamedQuery(name = "User.selectAllUsersAdmin",
                    query = "SELECT u FROM User u"),
        @NamedQuery(name = "User.deleteUser",
                    query = "UPDATE User u SET u.active = false, u.email = concat(u.email, '_DELETED') WHERE u.userId = :userId and u.active = true and u.admin = false"),
        @NamedQuery(name = "User.deleteUserMasterAdmin",
                query = "UPDATE User u SET u.active = false, u.email = concat(u.email, '_DELETED') WHERE u.userId = :userId and u.active = true"),
        @NamedQuery(name = "User.updateUserPassword",
                query = "UPDATE User u SET u.password = :newPassword WHERE u.userId = :userId  and u.active = true")
})
public class User implements Serializable {
    @Id
    @Column(name = "USER_ID")
    private Integer userId;
    @Column(name = "FIRST_NAME")
    private String firstName;
    @Column(name = "LAST_NAME")
    private String lastName;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "ADMIN")
    private Boolean admin;
    @Column(name = "ACTIVE")
    private Boolean active;
    @JoinTable(name = "GROUPS_USERS", schema = "UMSDB",
            joinColumns = {@JoinColumn(name = "USER_ID")},
            inverseJoinColumns = {@JoinColumn(name = "GROUP_ID")})
    @ManyToMany
    private Collection<Group> groups;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Collection<Group> getGroups() {
        return groups;
    }

    public void setGroups(Collection<Group> groups) {
        this.groups = groups;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", admin=" + admin +
                ", active=" + active +
                ", groups=" + groups +
                '}';
    }
}
