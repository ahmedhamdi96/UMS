package com.sumerge.program.managers;

import com.google.gson.Gson;
import com.sumerge.program.entities.Group;
import com.sumerge.program.entities.User;
import com.sumerge.program.models.PasswordModel;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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

    public User readUserByEmail(String email){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("readUserByEmail");
        }

        TypedQuery<User> query =
                entityManager.createNamedQuery("User.selectByEmail", User.class).setParameter("email", email);
        try{
            return query.getSingleResult();
        } catch (NoResultException noResultException){
            LOGGER.debug(noResultException.getMessage());
            return null;
        }
    }

    public List<User> readAllUsers(){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("readAllUsers");
        }
        TypedQuery<User> query =
                entityManager.createNamedQuery("User.selectAllUsers", User.class);
        return query.getResultList();
    }

    public List<User> readAllUsersAdmin(){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("readAllUsersAdmin");
        }
        TypedQuery<User> query =
                entityManager.createNamedQuery("User.selectAllUsersAdmin", User.class);
        return query.getResultList();
    }

    public User readUser(Integer userId){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("readUser");
        }
        return entityManager.find( User.class, userId);
    }

    public User updateUser(Integer userId, User userUpdated, String author){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("updateUser");
        }
        User userDB = entityManager.find( User.class, userId);

        if (userDB == null){
            return null;
        }

        if (userUpdated.getFirstName()!= null){
            userDB.setFirstName(userUpdated.getFirstName());
        }
        if (userUpdated.getLastName()!= null){
            userDB.setLastName(userUpdated.getLastName());
        }

        if (userUpdated.getEmail()!= null){
            if (readUserByEmail(userUpdated.getEmail())==null){
                userDB.setEmail(userUpdated.getEmail());
            } else {
                return null;
            }
        }

        User merged_user = entityManager.merge(userDB);
        auditManager.createAudit(author, "UPDATE", new Gson().toJson(userDB));
        return merged_user;
    }

    public User createUser(User user, String author) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("createUser");
        }

        user.setPassword(sha256(user.getPassword()));

        User merged_user =  entityManager.merge(user);
        auditManager.createAudit(author, "CREATE", new Gson().toJson(merged_user));
        return merged_user;
    }

    public String deleteUser(Integer userId, String author) throws Exception{
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("deleteUser");
        }

        TypedQuery<User> query =
                entityManager.createNamedQuery("User.deleteUser", User.class).setParameter("userId", userId);
        int updated =  query.executeUpdate();

        User userDB = entityManager.find( User.class, userId);
        if (userDB == null){
            return null;
        }

        if (updated == 1){
            auditManager.createAudit(author, "DELETE", new Gson().toJson(userDB));
            return "User Deleted Successfully.";
        } else {
            String message = "Update failed for an unknown reason!";
            LOGGER.debug(message);
            throw new Exception(message);
        }
    }

    public User updateUserGroups(Integer userId, Integer groupId, String author){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("updateUserGroups");
        }

        User userDB = entityManager.find( User.class, userId);
        if (userDB == null){
            return null;
        }
        Group groupDB = entityManager.find( Group.class, groupId);
        if (groupDB == null){
            return null;
        }

        userDB.getGroups().add(groupDB);
        User merged_user =  entityManager.merge(userDB);
        //auditManager.createAudit(author, "UPDATE", new Gson().toJson(merged_user));
        return merged_user;
    }

    public User deleteUserGroups(Integer userId, Integer groupId, String author){
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("deleteUserGroups");
        }

        User userDB = entityManager.find( User.class, userId);
        if (userDB == null){
            return null;
        }
        Group groupDB = entityManager.find( Group.class, groupId);
        if (groupDB == null){
            return null;
        }

        userDB.getGroups().remove(groupDB);
        User merged_user =  entityManager.merge(userDB);
        //auditManager.createAudit(author, "UPDATE", new Gson().toJson(merged_user));
        return merged_user;
    }

    public String resetUserPassword(Integer userId, PasswordModel passwordModel, String author) throws Exception {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("resetUserPassword");
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
                String message = "Password reset failed for an unknown reason!";
                LOGGER.debug(message);
                throw new Exception(message);
            }
        } else {
            return null;
        }
    }
}
