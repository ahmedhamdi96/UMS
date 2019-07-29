package com.sumerge.program.managers;

import com.sumerge.program.entities.User;

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

    public User updateUser(Integer userId, User userUpdated){
        User userDB = entityManager.find( User.class, userId);

        if (userUpdated.getFirstName()!= null){
            userDB.setFirstName(userUpdated.getFirstName());
        }
        if (userUpdated.getLastName()!= null){
            userDB.setLastName(userUpdated.getLastName());
        }

        return entityManager.merge(userDB);
    }

    public User createUser(User user){
        return entityManager.merge(user);
    }

    public String deleteUser(Integer userId){
        TypedQuery<User> query =
                entityManager.createNamedQuery("User.deleteUser", User.class).setParameter("userId", userId);
        int updated =  query.executeUpdate();
        if (updated == 1){
            return "User Deleted Successfully.";
        } else {
            throw new WebApplicationException("userId does not exist!", Response.Status.BAD_REQUEST);
        }
    }
}
