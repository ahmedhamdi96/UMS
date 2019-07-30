package com.sumerge.program.rest;

import com.sumerge.program.entities.User;
import com.sumerge.program.managers.UserManager;

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

    @GET
    @Path("admin/{id}")
    public Response getAllUsersAdmin(@PathParam("id") Integer userId) {
        try {
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
            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);

            if(authenticated_user.getUserId()!=id){
                throw new WebApplicationException("You are authorized to edit your information only!", Response.Status.FORBIDDEN);
            }

            return Response.ok().
                    entity(userManager.updateUser(id, user)).
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
            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException("You do not have an Admin Authorization!", Response.Status.FORBIDDEN);
            }

            return Response.ok().
                    entity(userManager.createUser(user)).
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
            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException("You do not have an Admin Authorization!", Response.Status.FORBIDDEN);
            }

            return Response.ok().
                    entity(userManager.deleteUser(id)).
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
            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException("You do not have an Admin Authorization!", Response.Status.FORBIDDEN);
            }

            return Response.ok().
                    entity(userManager.updateUserGroups(userId, groupId)).
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
            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException("You do not have an Admin Authorization!", Response.Status.FORBIDDEN);
            }

            return Response.ok().
                    entity(userManager.deleteUserGroups(userId, groupId)).
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
