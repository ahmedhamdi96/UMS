package com.sumerge.program.managers;

import com.google.gson.Gson;
import com.sumerge.program.entities.Group;
import com.sumerge.program.entities.User;
import com.sumerge.program.models.PasswordModel;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import static com.sumerge.program.utils.Hashing.sha256;

@Stateless
public class UserManager {
    @PersistenceContext(unitName = "umsdb-pu")
    private EntityManager entityManager;
    @EJB
    private AuditManager auditManager;
    private final static Logger logger = Logger.getLogger(UserManager.class);


    public User readUser(Integer userId){
        if(logger.isDebugEnabled()){
            logger.debug("readUser");
        }
        User userDB = entityManager.find( User.class, userId);
        return userDB;
    }

    public List<User> readAllUsers(){
        if(logger.isDebugEnabled()){
            logger.debug("readAllUsers");
        }
        TypedQuery<User> query =
                entityManager.createNamedQuery("User.selectAllUsers", User.class);
        List<User> users = query.getResultList();
        return users;
    }

    public List<User> readAllUsersAdmin(){
        if(logger.isDebugEnabled()){
            logger.debug("readAllUsersAdmin");
        }
        TypedQuery<User> query =
                entityManager.createNamedQuery("User.selectAllUsersAdmin", User.class);
        List<User> users = query.getResultList();
        return users;
    }

    public User readUserByEmail(String email){
        if(logger.isDebugEnabled()){
            logger.debug("readUserByEmail");
        }
        TypedQuery<User> query =
                entityManager.createNamedQuery("User.selectByEmail", User.class).setParameter("email", email);
        User user = query.getSingleResult();
        return user;
    }

    public User updateUser(Integer userId, User userUpdated, String author){
        if(logger.isDebugEnabled()){
            logger.debug("updateUser");
        }
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

    public User createUser(User user, String author) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if(logger.isDebugEnabled()){
            logger.debug("createUser");
        }
        user.setPassword(sha256(user.getPassword()));
        User merged_user =  entityManager.merge(user);
        auditManager.createAudit(author, "CREATE", new Gson().toJson(merged_user));
        return merged_user;
    }

    public String deleteUser(Integer userId, String author){
        if(logger.isDebugEnabled()){
            logger.debug("deleteUser");
        }
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
        if(logger.isDebugEnabled()){
            logger.debug("updateUserGroups");
        }
        User userDB = entityManager.find( User.class, userId);
        Group groupDB = entityManager.find( Group.class, groupId);
        userDB.getGroups().add(groupDB);
        User merged_user =  entityManager.merge(userDB);
        //auditManager.createAudit(author, "UPDATE", new Gson().toJson(merged_user));
        return merged_user;
    }

    public User deleteUserGroups(Integer userId, Integer groupId, String author){
        if(logger.isDebugEnabled()){
            logger.debug("deleteUserGroups");
        }
        User userDB = entityManager.find( User.class, userId);
        Group groupDB = entityManager.find( Group.class, groupId);
        userDB.getGroups().remove(groupDB);
        User merged_user =  entityManager.merge(userDB);
        //auditManager.createAudit(author, "UPDATE", new Gson().toJson(merged_user));
        return merged_user;
    }

    public String resetUserPassword(Integer userId, PasswordModel passwordModel, String author) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if(logger.isDebugEnabled()){
            logger.debug("resetUserPassword");
        }

        passwordModel.setOldPassword(sha256(passwordModel.getOldPassword()));
        passwordModel.setNewPassword(sha256(passwordModel.getNewPassword()));

        TypedQuery<User> selectUserQuery =
                entityManager.createNamedQuery("User.selectUser", User.class).setParameter("userId", userId);
        String passwordDB = selectUserQuery.getSingleResult().getPassword();

        if (passwordDB.equals(passwordModel.getOldPassword())){
            TypedQuery<User> updateUserPasswordQuery =
                    entityManager.createNamedQuery("User.updateUserPassword", User.class).
                            setParameter("userId", userId).
                            setParameter("newPassword", passwordModel.getNewPassword());
            int updated =  updateUserPasswordQuery.executeUpdate();
            if (updated == 1){
                User userDB = entityManager.find( User.class, userId);
                auditManager.createAudit(author, "UPDATE", new Gson().toJson(userDB));
                return "User Password Updated Successfully.";
            } else {
                throw new WebApplicationException("Error Updating User Password", Response.Status.BAD_REQUEST);
            }
        } else {
            throw new WebApplicationException("Old Password is not correct!", Response.Status.BAD_REQUEST);
        }
    }
}
