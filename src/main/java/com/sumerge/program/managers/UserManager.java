package com.sumerge.program.managers;

import com.google.gson.Gson;
import com.sumerge.program.entities.Group;
import com.sumerge.program.entities.User;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.util.List;

@Stateless
public class UserManager {
    @PersistenceContext(unitName = "umsdb-pu")
    private EntityManager entityManager;
    @EJB
    private AuditManager auditManager;

    public User readUser(Integer userId){
        User userDB = entityManager.find( User.class, userId);
        return userDB;
    }

    public List<User> readAllUsers(){
        TypedQuery<User> query =
                entityManager.createNamedQuery("User.selectAllUsers", User.class);
        List<User> users = query.getResultList();
        return users;
    }

    public List<User> readAllUsersAdmin(){
        TypedQuery<User> query =
                entityManager.createNamedQuery("User.selectAllUsersAdmin", User.class);
        List<User> users = query.getResultList();
        return users;
    }

    public User readUserByEmail(String email){
        TypedQuery<User> query =
                entityManager.createNamedQuery("User.selectByEmail", User.class).setParameter("email", email);
        User user = query.getSingleResult();
        return user;
    }

    public User updateUser(Integer userId, User userUpdated, String author){
        User userDB = entityManager.find( User.class, userId);

        if (userUpdated.getFirstName()!= null){
            userDB.setFirstName(userUpdated.getFirstName());
        }
        if (userUpdated.getLastName()!= null){
            userDB.setLastName(userUpdated.getLastName());
        }

        User merged_user = entityManager.merge(userDB);
        auditManager.createAudit(author, "UPDATE", new Gson().toJson(userDB));
        return merged_user;
    }

    public User createUser(User user, String author){
        User merged_user =  entityManager.merge(user);
        auditManager.createAudit(author, "CREATE", new Gson().toJson(merged_user));
        return merged_user;
    }

    public String deleteUser(Integer userId, String author){
        TypedQuery<User> query =
                entityManager.createNamedQuery("User.deleteUser", User.class).setParameter("userId", userId);
        int updated =  query.executeUpdate();
        if (updated == 1){
            User userDB = entityManager.find( User.class, userId);
            auditManager.createAudit(author, "DELETE", new Gson().toJson(userDB));
            return "User Deleted Successfully.";
        } else {
            throw new WebApplicationException("userId does not exist!", Response.Status.BAD_REQUEST);
        }
    }

    public User updateUserGroups(Integer userId, Integer groupId, String author){
        User userDB = entityManager.find( User.class, userId);
        Group groupDB = entityManager.find( Group.class, groupId);
        userDB.getGroups().add(groupDB);
        User merged_user =  entityManager.merge(userDB);
        //auditManager.createAudit(author, "UPDATE", new Gson().toJson(merged_user));
        return merged_user;
    }

    public User deleteUserGroups(Integer userId, Integer groupId, String author){
        User userDB = entityManager.find( User.class, userId);
        Group groupDB = entityManager.find( Group.class, groupId);
        userDB.getGroups().remove(groupDB);
        User merged_user =  entityManager.merge(userDB);
        //auditManager.createAudit(author, "UPDATE", new Gson().toJson(merged_user));
        return merged_user;
    }
}
