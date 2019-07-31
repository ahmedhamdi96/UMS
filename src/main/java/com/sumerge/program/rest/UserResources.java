package com.sumerge.program.rest;

import com.sumerge.program.entities.User;
import com.sumerge.program.managers.UserManager;
import org.apache.log4j.Logger;

import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

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
    private final static Logger logger = Logger.getLogger(UserResources.class);

    @GET
    @Path("admin/{id}")
    public Response getUserAdmin(@PathParam("id") Integer userId) {
        try {
            if(logger.isDebugEnabled()){
                logger.debug("GET/ getUserAdmin");
            }
            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException("You do not have an Admin Authorization!", Response.Status.FORBIDDEN);
            }

            return Response.ok().
                    entity(userManager.readUser(userId)).
                    build();
        } catch (Exception e) {
            return Response.serverError().
                    entity(e.getClass() + ": " + e.getMessage()).
                    build();
        }
    }

    @GET
    public Response getAllUsers() {
        try {
            if(logger.isDebugEnabled()){
                logger.debug("GET/ getAllUsers");
            }
            return Response.ok().
                    entity(userManager.readAllUsers()).
                    build();
        } catch (Exception e) {
            return Response.serverError().
                    entity(e.getClass() + ": " + e.getMessage()).
                    build();
        }
    }

    @GET
    @Path("admin")
    public Response getAllUsersAdmin() {
        try {
            if(logger.isDebugEnabled()){
                logger.debug("GET/ getAllUsersAdmin");
            }
            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException("You do not have an Admin Authorization!", Response.Status.FORBIDDEN);
            }

            return Response.ok().
                    entity(userManager.readAllUsersAdmin()).
                    build();
        } catch (Exception e) {
            return Response.serverError().
                    entity(e.getClass() + ": " + e.getMessage()).
                    build();
        }
    }

    @PUT
    @Path("{id}")
    public Response putInfo(@PathParam("id") Integer id, User user){
        try{
            if(logger.isDebugEnabled()){
                logger.debug("PUT/ putInfo");
            }
            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);

            if(authenticated_user.getUserId().intValue() !=  id.intValue()){
                throw new WebApplicationException("You are authorized to edit your information only!", Response.Status.FORBIDDEN);
            }

            return Response.ok().
                    entity(userManager.updateUser(id, user, email)).
                    build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (Exception e) {
            return Response.serverError().
                    entity(e.getClass() + ": " + e.getMessage()).
                    build();
        }
    }

    @POST
    public Response postUser(User user){
        try{
            if(logger.isDebugEnabled()){
                logger.debug("POST/ postUser");
            }
            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException("You do not have an Admin Authorization!", Response.Status.FORBIDDEN);
            }

            return Response.ok().
                    entity(userManager.createUser(user, email)).
                    build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (Exception e) {
            return Response.serverError().
                    entity(e.getClass() + ": " + e.getMessage()).
                    build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteUser(@PathParam("id") Integer id){
        try{
            if(logger.isDebugEnabled()){
                logger.debug("DELETE/ deleteUser");
            }
            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException("You do not have an Admin Authorization!", Response.Status.FORBIDDEN);
            }

            return Response.ok().
                    entity(userManager.deleteUser(id, email)).
                    build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (Exception e) {
            return Response.serverError().
                    entity(e.getClass() + ": " + e.getMessage()).
                    build();
        }
    }

    @PUT
    @Path("{userId}/{groupId}")
    public Response putUserGroups(@PathParam("userId") Integer userId, @PathParam("groupId") Integer groupId){
        try{
            if(logger.isDebugEnabled()){
                logger.debug("PUT/ putUserGroups");
            }
            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException("You do not have an Admin Authorization!", Response.Status.FORBIDDEN);
            }

            return Response.ok().
                    entity(userManager.updateUserGroups(userId, groupId, email)).
                    build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (Exception e) {
            return Response.serverError().
                    entity(e.getClass() + ": " + e.getMessage()).
                    build();
        }
    }

    @DELETE
    @Path("{userId}/{groupId}")
    public Response deleteUserGroups(@PathParam("userId") Integer userId, @PathParam("groupId") Integer groupId){
        try{
            if(logger.isDebugEnabled()){
                logger.debug("DELETE/ deleteUserGroups");
            }
            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException("You do not have an Admin Authorization!", Response.Status.FORBIDDEN);
            }

            return Response.ok().
                    entity(userManager.deleteUserGroups(userId, groupId, email)).
                    build();
        } catch (WebApplicationException e) {
            return e.getResponse();
        } catch (Exception e) {
            return Response.serverError().
                    entity(e.getClass() + ": " + e.getMessage()).
                    build();
        }
    }
}
