package com.sumerge.program.rest;


import com.sumerge.program.entities.User;
import com.sumerge.program.managers.UserManager;
import com.sumerge.program.models.PasswordModel;
import com.sumerge.program.models.UserAdminModel;
import com.sumerge.program.models.UserModel;
import com.sumerge.program.utils.Regex;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@RequestScoped
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
@Path("users")
public class UserResources {
    @EJB
    private UserManager userManager;
    @Context
    private HttpServletRequest httpRequest;
    private final static Logger LOGGER = Logger.getLogger(UserResources.class);

    @GET
    public Response getAllUsers() {
        try {
            LOGGER.debug("getAllUsers: ENTER");

            String email = httpRequest.getRemoteUser();
            if(email == null){
                String message =  "You are unauthorized!";
                throw new WebApplicationException(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity(message)
                                .build()
                );
            }
            User authenticated_user = userManager.readUserByEmail(email);

            List<User> users;
            if(authenticated_user.getAdmin()){
                users = userManager.readAllUsersAdmin();
                List<UserAdminModel> usersModel = users.stream().map(u -> new UserAdminModel(u)).
                        collect(Collectors.toList());
                LOGGER.debug("getAllUsers: EXIT");
                return Response.ok().
                        entity(usersModel).
                        build();
            } else {
                users = userManager.readAllUsers();
                List<UserModel> usersModel = users.stream().map(u -> new UserModel(u)).
                        collect(Collectors.toList());
                LOGGER.debug("getAllUsers: EXIT");
                return Response.ok().
                        entity(usersModel).
                        build();
            }


        } catch (WebApplicationException e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("getAllUsers: EXIT");
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("getAllUsers: EXIT");
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @GET
    @Path("{id}")
    public Response getUser(@PathParam("id") Integer userId) {
        try{
            LOGGER.debug("getUser: ENTER");

            String email = httpRequest.getRemoteUser();
            if(email == null){
                String message =  "You are unauthorized!";
                throw new WebApplicationException(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity(message)
                                .build()
                );
            }
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                String message =  "You do not have an administrator authorization!";
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity(message)
                                .build()
                );
            }

            User user = userManager.readUser(userId);
            if(user == null){
                String message =  "User does not exist!";
                throw new WebApplicationException(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity(message)
                                .build()
                );
            }

            LOGGER.debug("getUser: EXIT");
            return Response.ok().
                    entity(new UserAdminModel(user)).
                    build();
        } catch (WebApplicationException e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("getUser: EXIT");
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("getUser: EXIT");
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @POST
    public Response postUser(User user){
        try{
            LOGGER.debug("postUser: ENTER");

            String email = httpRequest.getRemoteUser();
            if(email == null){
                String message =  "You are unauthorized!";
                throw new WebApplicationException(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity(message)
                                .build()
                );
            }
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                String message =  "You do not have an administrator authorization!";
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity(message)
                                .build()
                );
            }

            Boolean allFieldsNotNull = true;
            allFieldsNotNull &= (user.getFirstName() != null);
            allFieldsNotNull &= (user.getLastName()!= null);
            allFieldsNotNull &= (user.getEmail() != null);
            allFieldsNotNull &= (user.getPassword() != null);
            allFieldsNotNull &= (user.getAdmin() != null);

            Boolean allFieldsValid = false;
            if (allFieldsNotNull){
                allFieldsValid = true;
                allFieldsValid &= Regex.isValidEmail(user.getEmail());
                allFieldsValid &= user.getFirstName().length()<=50;
                allFieldsValid &= user.getLastName().length()<=50;
                allFieldsValid &= user.getEmail().length()<=50;
                allFieldsValid &= user.getPassword().length()<=50;
            }

            if (!(allFieldsNotNull && allFieldsValid)) {
                String message =  "Send all required fields in a valid form!";
                throw new WebApplicationException(
                        Response.status(Response.Status.BAD_REQUEST)
                                .entity(message)
                                .build()
                );
            }

            user.setUserId(null);
            user.setActive(true);
            user.setGroups(null);
            User userCreated = userManager.createUser(user, email);
            if(userCreated == null){
                String message =  "Email already used!";
                throw new WebApplicationException(
                        Response.status(Response.Status.CONFLICT)
                                .entity(message)
                                .build()
                );
            }

            LOGGER.debug("postUser: EXIT");
            return Response.ok().
                    entity(new UserModel(userCreated)).
                    build();
        } catch (WebApplicationException e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("postUser: EXIT");
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("postUser: EXIT");
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @PUT
    @Path("{id}")
    public Response putInfo(@PathParam("id") Integer id, User user){
        try{
            LOGGER.debug("putInfo: ENTER");

            if (id.intValue() == 1){
                String message = "You do not have an authorization to edit the master administrator!";
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity(message)
                                .build()
                );
            }

            String email = httpRequest.getRemoteUser();
            if(email == null){
                String message =  "You are unauthorized!";
                throw new WebApplicationException(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity(message)
                                .build()
                );
            }
            User authenticated_user = userManager.readUserByEmail(email);

            if(authenticated_user.getUserId().intValue() !=  id.intValue()){
                String message =  "You are authorized to edit your own information only!";
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity(message)
                                .build()
                );
            }

            Boolean atLeastOneField = false;
            atLeastOneField |= user.getFirstName()!=null;
            atLeastOneField |= user.getLastName()!=null;
            atLeastOneField |= user.getEmail()!=null;

            if (!atLeastOneField) {
                String message =  "You have to edit at least one field!";
                throw new WebApplicationException(
                        Response.status(Response.Status.BAD_REQUEST)
                                .entity(message)
                                .build()
                );
            }

            Boolean allFieldsValid = true;
            allFieldsValid &= user.getFirstName() == null || ( user.getFirstName()!=null &&user.getFirstName().length()<=50 );
            allFieldsValid &= user.getLastName() == null || ( user.getLastName()!=null &&user.getLastName().length()<=50 );
            allFieldsValid &= user.getEmail() == null || ( user.getEmail()!=null && user.getEmail().length()<=50 );
            allFieldsValid &= user.getEmail() == null || ( user.getEmail()!=null && Regex.isValidEmail(user.getEmail()) );

            if (!allFieldsValid) {
                String message =  "Send all fields in a valid form!";
                throw new WebApplicationException(
                        Response.status(Response.Status.BAD_REQUEST)
                                .entity(message)
                                .build()
                );
            }

            User userUpdated = userManager.updateUser(id, user, email);
            if(userUpdated == null){
                String message =  "Email already used!";
                throw new WebApplicationException(
                        Response.status(Response.Status.CONFLICT)
                                .entity(message)
                                .build()
                );
            }

            if (authenticated_user.getAdmin()){
                LOGGER.debug("putInfo: EXIT");
                return Response.ok().
                        entity(new UserAdminModel(userUpdated)).
                        build();
            } else {
                LOGGER.debug("putInfo: EXIT");
                return Response.ok().
                        entity(new UserModel(userUpdated)).
                        build();
            }
        } catch (WebApplicationException e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("putInfo: EXIT");
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("putInfo: EXIT");
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") Integer id){
        try{
            LOGGER.debug("deleteUser: ENTER");

            if (id.intValue() == 1){
                String message = "You do not have an authorization to delete the master administrator!";
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity(message)
                                .build()
                );
            }

            String email = httpRequest.getRemoteUser();
            if(email == null){
                String message =  "You are unauthorized!";
                throw new WebApplicationException(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity(message)
                                .build()
                );
            }
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity("You do not have an administrator authorization!")
                                .build()
                );
            }


            String message = userManager.deleteUser(id, email, authenticated_user.getUserId());
            if (message == null){
                throw new WebApplicationException(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity("User does not exist!")
                                .build()
                );
            }

            LOGGER.debug("deleteUser: EXIT");
            return Response.ok().
                    entity(message).
                    build();
        } catch (EJBException e) {
            LOGGER.debug(e.getMessage());
            throw new WebApplicationException(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity(e.getCause().getMessage())
                            .build()
            );
        } catch (WebApplicationException e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("deleteUser: EXIT");
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("deleteUser: EXIT");
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @PUT
    @Path("{id}/password/reset")
    public Response resetPassword(@PathParam("id") Integer id, PasswordModel passwordModel){
        try{
            LOGGER.debug("resetPassword: ENTER");

            if (id.intValue() == 1){
                String message = "You do not have an authorization to edit the master administrator!";
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity(message)
                                .build()
                );
            }

            String email = httpRequest.getRemoteUser();
            if(email == null){
                String message =  "You are unauthorized!";
                throw new WebApplicationException(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity(message)
                                .build()
                );
            }
            User authenticated_user = userManager.readUserByEmail(email);

            if(authenticated_user.getUserId().intValue() !=  id.intValue()){
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity("You are authorized to reset your own password only!")
                                .build()
                );
            }

            String message = userManager.resetUserPassword(id, passwordModel, email);
            if (message == null){
                throw new WebApplicationException(
                        Response.status(Response.Status.BAD_REQUEST)
                                .entity("Old Password is not correct!")
                                .build()
                );
            }

            LOGGER.debug("resetPassword: EXIT");
            return Response.ok().
                    entity(message).
                    build();
        } catch (WebApplicationException e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("resetPassword: EXIT");
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("resetPassword: EXIT");
            return Response.serverError().
                    entity(e.getClass() + ": " + e.getMessage()).
                    build();
        }
    }

    @PUT
    @Path("{userId}/{groupId}")
    public Response putUserGroups(@PathParam("userId") Integer userId, @PathParam("groupId") Integer groupId){
        try{
            LOGGER.debug("putUserGroups: ENTER");

            String email = httpRequest.getRemoteUser();
            if(email == null){
                String message =  "You are unauthorized!";
                throw new WebApplicationException(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity(message)
                                .build()
                );
            }
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity("You do not have an administrator authorization!")
                                .build()
                );
            }

            User updatedUser = userManager.updateUserGroups(userId, groupId, email, authenticated_user.getUserId());
            if (updatedUser == null){
                throw new WebApplicationException(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity("User or Group does not exist!")
                                .build()
                );
            }

            LOGGER.debug("putUserGroups: EXIT");
            return Response.ok().
                    entity(new UserAdminModel(updatedUser)).
                    build();
        } catch (EJBException e) {
            LOGGER.debug(e.getMessage());
            throw new WebApplicationException(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity(e.getCause().getMessage())
                            .build()
            );
        } catch (WebApplicationException e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("putUserGroups: EXIT");
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("putUserGroups: EXIT");
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @DELETE
    @Path("{userId}/{groupId}")
    public Response deleteUserGroups(@PathParam("userId") Integer userId, @PathParam("groupId") Integer groupId){
        try{
            LOGGER.debug("deleteUserGroups: ENTER");

            String email = httpRequest.getRemoteUser();
            if(email == null){
                String message =  "You are unauthorized!";
                throw new WebApplicationException(
                        Response.status(Response.Status.UNAUTHORIZED)
                                .entity(message)
                                .build()
                );
            }
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity("You do not have an administrator authorization!")
                                .build()
                );
            }

            User updatedUser = userManager.deleteUserGroups(userId, groupId, email, authenticated_user.getUserId());
            if (updatedUser == null){
                throw new WebApplicationException(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity("User or Group does not exist!")
                                .build()
                );
            }

            LOGGER.debug("deleteUserGroups: EXIT");
            return Response.ok().
                    entity(new UserAdminModel(updatedUser)).
                    build();
        } catch (EJBException e) {
            LOGGER.debug(e.getMessage());
            throw new WebApplicationException(
                    Response.status(Response.Status.FORBIDDEN)
                            .entity(e.getCause().getMessage())
                            .build()
            );
        } catch (WebApplicationException e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("deleteUserGroups: EXIT");
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("deleteUserGroups: EXIT");
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }
}
