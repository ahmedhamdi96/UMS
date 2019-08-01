package com.sumerge.program.rest;


import com.sumerge.program.entities.User;
import com.sumerge.program.managers.UserManager;
import com.sumerge.program.models.PasswordModel;
import com.sumerge.program.utils.Regex;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import java.util.List;

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
            LOGGER.debug("GET/ getAllUsers");

            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);
            List<User> users;
            if(authenticated_user.getAdmin()){
                users = userManager.readAllUsersAdmin();
            } else {
                users = userManager.readAllUsers();
            }

            return Response.ok().
                    entity(users).
                    build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @GET
    @Path("{id}")
    public Response getUser(@PathParam("id") Integer userId) {
        try{
            LOGGER.debug("GET/ getUser");

            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);
            if(!authenticated_user.getAdmin()){
                String message =  "You do not have an Admin Authorization!";
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

            return Response.ok().
                    entity(user).
                    build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @PUT
    @Path("{id}")
    public Response putInfo(@PathParam("id") Integer id, User user){
        try{
            LOGGER.debug("PUT/ putInfo");

            if (id.intValue() == 1){
                String message = "You do not have an authorization to edit the master administrator!";
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity(message)
                                .build()
                );
            }

            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);
            if(authenticated_user.getUserId().intValue() !=  id.intValue()){
                String message =  "You are authorized to edit your own information only!";
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity(message)
                                .build()
                );
            }

            Boolean allFieldsValid = true;
            allFieldsValid &= ( user.getEmail()!=null && Regex.isValidEmail(user.getEmail()) );
            allFieldsValid &= ( user.getFirstName()!=null &&user.getFirstName().length()<=50 );
            allFieldsValid &= ( user.getLastName()!=null &&user.getLastName().length()<=50 );
            allFieldsValid &= ( user.getEmail()!=null && user.getEmail().length()<=50 );

            if (allFieldsValid) {
                String message =  "Send all fields in a valid form.";
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

            return Response.ok().
                    entity(userUpdated).
                    build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @POST
    public Response postUser(User user){
        try{
            LOGGER.debug("POST/ postUser");

            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);
            if(!authenticated_user.getAdmin()){
                String message =  "You do not have an Admin Authorization!";
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
            allFieldsNotNull &= (user.getActive()!= null);

            Boolean allFieldsValid = true;
            allFieldsValid &= Regex.isValidEmail(user.getEmail());
            allFieldsValid &= user.getFirstName().length()<=50;
            allFieldsValid &= user.getLastName().length()<=50;
            allFieldsValid &= user.getEmail().length()<=50;
            allFieldsValid &= user.getPassword().length()<=50;

            if (!allFieldsNotNull && allFieldsValid) {
                String message =  "Send all required fields in a valid form.";
                throw new WebApplicationException(
                        Response.status(Response.Status.BAD_REQUEST)
                                .entity(message)
                                .build()
                );
            }

            User userCreated = userManager.createUser(user, email);

            return Response.ok().
                    entity(userCreated).
                    build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") Integer id){
        try{
            LOGGER.debug("DELETE/ deleteUser");

            if (id.intValue() == 1){
                String message = "You do not have an authorization to delete the master administrator!";
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity(message)
                                .build()
                );
            }

            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);
            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity("You do not have an Admin Authorization!")
                                .build()
                );
            }


            String message = userManager.deleteUser(id, email);
            if (message == null){
                throw new WebApplicationException(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity("User does not exist!")
                                .build()
                );
            }

            return Response.ok().
                    entity(message).
                    build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @PUT
    @Path("{userId}/{groupId}")
    public Response putUserGroups(@PathParam("userId") Integer userId, @PathParam("groupId") Integer groupId){
        try{
            LOGGER.debug("PUT/ putUserGroups");

            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);
            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity("You do not have an Admin Authorization!")
                                .build()
                );
            }

            User updatedUser = userManager.updateUserGroups(userId, groupId, email);
            if (updatedUser == null){
                throw new WebApplicationException(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity("User or Group does not exist!")
                                .build()
                );
            }

            return Response.ok().
                    entity(updatedUser).
                    build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @DELETE
    @Path("{userId}/{groupId}")
    public Response deleteUserGroups(@PathParam("userId") Integer userId, @PathParam("groupId") Integer groupId){
        try{
            LOGGER.debug("DELETE/ deleteUserGroups");

            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);
            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity("You do not have an Admin Authorization!")
                                .build()
                );
            }

            User updatedUser = userManager.deleteUserGroups(userId, groupId, email);
            if (updatedUser == null){
                throw new WebApplicationException(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity("User or Group does not exist!")
                                .build()
                );
            }

            return Response.ok().
                    entity(updatedUser).
                    build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @PUT
    @Path("{id}/password/reset")
    public Response resetPassword(@PathParam("id") Integer id, PasswordModel passwordModel){
        try{
            LOGGER.debug("PUT/ resetPassword");

            if (id.intValue() == 1){
                String message = "You do not have an authorization to edit the master administrator!";
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity(message)
                                .build()
                );
            }

            String email = httpRequest.getRemoteUser();
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
            return Response.ok().
                    entity(message).
                    build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            return Response.serverError().
                    entity(e.getClass() + ": " + e.getMessage()).
                    build();
        }
    }
}
