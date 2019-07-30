package com.sumerge.program.rest;

import com.sumerge.program.entities.Group;
import com.sumerge.program.entities.User;
import com.sumerge.program.managers.GroupManager;
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
@Path("groups")
public class GroupResources {
    @EJB
    private UserManager userManager;
    @EJB
    private GroupManager groupManager;
    @Context
    private HttpServletRequest httpRequest;

    @POST
    public Response postGroup(Group group){
        try{
            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);

            if(!authenticated_user.getAdmin()){
                throw new WebApplicationException("You do not have an Admin Authorization!", Response.Status.FORBIDDEN);
            }

            return Response.ok().
                    entity(groupManager.createGroup(group, email)).
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
