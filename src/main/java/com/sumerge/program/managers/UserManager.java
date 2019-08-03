package com.sumerge.program.managers;


import com.google.gson.Gson;
import com.sumerge.program.entities.Group;
import com.sumerge.program.entities.User;
import com.sumerge.program.models.PasswordModel;
import com.sumerge.program.models.UserAdminModel;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.WebApplicationException;
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
    private final static Logger LOGGER = Logger.getLogger(UserManager.class);

    public List<User> readAllUsers(){
        LOGGER.debug("readAllUsers: ENTER");

        TypedQuery<User> query =
                entityManager.createNamedQuery("User.selectAllUsers", User.class);

        List<User> users =  query.getResultList();
        LOGGER.debug("readAllUsers: EXIT");
        return users;
    }

    public List<User> readAllUsersAdmin(){
        LOGGER.debug("readAllUsersAdmin: ENTER");

        TypedQuery<User> query =
                entityManager.createNamedQuery("User.selectAllUsersAdmin", User.class);

        List<User> users =  query.getResultList();
        LOGGER.debug("readAllUsersAdmin: EXIT");
        return users;
    }

    public User readUser(Integer userId){
        LOGGER.debug("readUser: ENTER");
        User user = entityManager.find( User.class, userId);
        LOGGER.debug("readUser: EXIT");
        return user;
    }

    public User readUserByEmail(String email){
        LOGGER.debug("readUserByEmail: ENTER");

        TypedQuery<User> query =
                entityManager.createNamedQuery("User.selectByEmail", User.class).setParameter("email", email);
        try{
            User user = query.getSingleResult();
            LOGGER.debug("readUserByEmail: EXIT");
            return user;
        } catch (NoResultException noResultException){
            LOGGER.debug(noResultException.getMessage());
            LOGGER.debug("readUserByEmail: EXIT");
            return null;
        }
    }

    @Transactional
    public User createUser(User user, String author) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        LOGGER.debug("createUser: ENTER");

        if (user.getEmail()!= null){
            if (readUserByEmail(user.getEmail())!=null){
                LOGGER.debug("createUser: EXIT");
                return null;
            }
        }

        user.setPassword(sha256(user.getPassword()));

        User merged_user =  entityManager.merge(user);
        auditManager.createAudit(author, "CREATE","USER", new Gson().toJson(new UserAdminModel(merged_user)));
        LOGGER.debug("createUser: EXIT");
        return merged_user;
    }

    @Transactional
    public User updateUser(Integer userId, User userUpdated, String author){
        LOGGER.debug("updateUser: ENTER");

        User userDB = entityManager.find( User.class, userId);

        if (userDB == null){
            LOGGER.debug("updateUser: EXIT");
            return null;
        }

        if (userUpdated.getEmail()!= null){
            if (readUserByEmail(userUpdated.getEmail())==null){
                userDB.setEmail(userUpdated.getEmail());
            } else {
                LOGGER.debug("updateUser: EXIT");
                return null;
            }
        }

        if (userUpdated.getFirstName()!= null){
            userDB.setFirstName(userUpdated.getFirstName());
        }
        if (userUpdated.getLastName()!= null){
            userDB.setLastName(userUpdated.getLastName());
        }

        User merged_user = entityManager.merge(userDB);
        auditManager.createAudit(author, "UPDATE", "USER", new Gson().toJson(new UserAdminModel(userDB)));
        LOGGER.debug("updateUser: EXIT");
        return merged_user;
    }

    @Transactional
    public String deleteUser(Integer userId, String author, Integer authorID) throws Exception{
        LOGGER.debug("deleteUser: ENTER");

        User userDB = entityManager.find( User.class, userId);
        if (userDB == null || !userDB.getActive()){
            LOGGER.debug("deleteUser: EXIT");
            return null;
        }

        TypedQuery<User> query;

        if (authorID.intValue() == 1){
            query = entityManager.createNamedQuery("User.deleteUserMasterAdmin", User.class).setParameter("userId", userId);
        } else {
            query = entityManager.createNamedQuery("User.deleteUser", User.class).setParameter("userId", userId);
        }
        int updated =  query.executeUpdate();


        if (updated == 1){
            auditManager.createAudit(author, "DELETE", "USER", new Gson().toJson(new UserAdminModel(userDB)));
            LOGGER.debug("deleteUser: EXIT");
            return "User Deleted Successfully.";
        } else {
            String message = "You do not have an authorization to delete an administrator!";
            WebApplicationException webApplicationException = new WebApplicationException(message);
            LOGGER.debug(webApplicationException.getMessage());
            LOGGER.debug("deleteUser: EXIT");
            throw webApplicationException;
        }
    }

    @Transactional
    public String resetUserPassword(Integer userId, PasswordModel passwordModel, String author) throws Exception {
        LOGGER.debug("resetUserPassword: ENTER");

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
                auditManager.createAudit(author, "UPDATE", "USER", new Gson().toJson(new UserAdminModel(userDB)));
                LOGGER.debug("resetUserPassword: EXIT");
                return "User Password Updated Successfully.";
            } else {
                String message = "Password reset failed for an unknown reason!";
                LOGGER.debug(message);
                Exception exception =  new Exception(message);
                LOGGER.debug("resetUserPassword: EXIT");
                throw  exception;
            }
        } else {
            LOGGER.debug("resetUserPassword: EXIT");
            return null;
        }
    }

    @Transactional
    public User updateUserGroups(Integer userId, Integer groupId, String author, Integer authorId){
        LOGGER.debug("updateUserGroups: ENTER");

        User userDB = entityManager.find( User.class, userId);
        if (userDB == null  || !userDB.getActive()){
            LOGGER.debug("updateUserGroups: EXIT");
            return null;
        }
        Group groupDB = entityManager.find( Group.class, groupId);
        if (groupDB == null || !groupDB.getActive()){
            LOGGER.debug("updateUserGroups: EXIT");
            return null;
        }

        if (authorId.intValue() == 1){
            if (userDB.getGroups().contains(groupDB)){
                String message = "This user is already in this group!";
                WebApplicationException webApplicationException = new WebApplicationException(message);
                LOGGER.debug(webApplicationException.getMessage());
                LOGGER.debug("updateUserGroups: EXIT");
                throw webApplicationException;
            }
            userDB.getGroups().add(groupDB);
        } else {
            if (!userDB.getAdmin()){
                if (userDB.getGroups().contains(groupDB)){
                    String message = "This user is already in this group!";
                    WebApplicationException webApplicationException = new WebApplicationException(message);
                    LOGGER.debug(webApplicationException.getMessage());
                    LOGGER.debug("updateUserGroups: EXIT");
                    throw webApplicationException;
                }
                userDB.getGroups().add(groupDB);
            } else {
                String message = "You do not have an authorization to add an administrator to a group!";
                WebApplicationException webApplicationException = new WebApplicationException(message);
                LOGGER.debug(webApplicationException.getMessage());
                LOGGER.debug("updateUserGroups: EXIT");
                throw webApplicationException;
            }
        }

        User merged_user =  entityManager.merge(userDB);
        auditManager.createAudit(author, "UPDATE", "USER", new Gson().toJson(new UserAdminModel(merged_user)));
        LOGGER.debug("updateUserGroups: EXIT");
        return merged_user;
    }

    @Transactional
    public User deleteUserGroups(Integer userId, Integer groupId, String author, Integer authorId){
        LOGGER.debug("deleteUserGroups: ENTER");

        User userDB = entityManager.find( User.class, userId);
        if (userDB == null || !userDB.getActive()){
            LOGGER.debug("deleteUserGroups: EXIT");
            return null;
        }
        Group groupDB = entityManager.find( Group.class, groupId);
        if (groupDB == null || !groupDB.getActive()){
            LOGGER.debug("deleteUserGroups: EXIT");
            return null;
        }

        if (authorId.intValue() == 1){
            if (!userDB.getGroups().contains(groupDB)){
                String message = "This user is not in this group!";
                WebApplicationException webApplicationException = new WebApplicationException(message);
                LOGGER.debug(webApplicationException.getMessage());
                LOGGER.debug("deleteUserGroups: EXIT");
                throw webApplicationException;
            }
            userDB.getGroups().remove(groupDB);
        } else {
            if (!userDB.getAdmin()){
                if (!userDB.getGroups().contains(groupDB)){
                    String message = "This user is not in this group!";
                    WebApplicationException webApplicationException = new WebApplicationException(message);
                    LOGGER.debug(webApplicationException.getMessage());
                    LOGGER.debug("deleteUserGroups: EXIT");
                    throw webApplicationException;
                }
                userDB.getGroups().remove(groupDB);
            } else {
                String message = "You do not have an authorization to delete an administrator from a group!";
                WebApplicationException webApplicationException = new WebApplicationException(message);
                LOGGER.debug(webApplicationException.getMessage());
                LOGGER.debug("deleteUserGroups: EXIT");
                throw webApplicationException;
            }
        }

        User merged_user =  entityManager.merge(userDB);
        auditManager.createAudit(author, "UPDATE", "USER", new Gson().toJson(new UserAdminModel(merged_user)));
        LOGGER.debug("deleteUserGroups: EXIT");
        return merged_user;
    }
}
