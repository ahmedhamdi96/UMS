package com.sumerge.program.rest;


import com.sumerge.program.entities.Group;
import com.sumerge.program.entities.User;
import com.sumerge.program.managers.GroupManager;
import com.sumerge.program.managers.UserManager;
import com.sumerge.program.models.GroupAdminModel;
import com.sumerge.program.models.GroupModel;
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
@Path("groups")
public class GroupResources {
    @EJB
    private UserManager userManager;
    @EJB
    private GroupManager groupManager;
    @Context
    private HttpServletRequest httpRequest;
    private final static Logger LOGGER = Logger.getLogger(GroupResources.class);

    @GET
    public Response getAllGroups() {
        try {
            LOGGER.debug("getAllGroups: ENTER");

            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);
            List<Group> groups;
            if(authenticated_user.getAdmin()){
                groups = groupManager.readAllGroupsAdmin();
                List<GroupAdminModel> groupsModel = groups.stream().map(g -> new GroupAdminModel(g)).
                        collect(Collectors.toList());
                LOGGER.debug("getAllGroups: EXIT");
                return Response.ok().
                        entity(groupsModel).
                        build();
            } else {
                groups = groupManager.readAllGroups();
                List<GroupModel> groupsModel = groups.stream().map(g -> new GroupModel(g)).
                        collect(Collectors.toList());
                LOGGER.debug("getAllGroups: EXIT");
                return Response.ok().
                        entity(groupsModel).
                        build();
            }


        } catch (WebApplicationException e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("getAllGroups: EXIT");
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("getAllGroups: EXIT");
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @GET
    @Path("{id}")
    public Response getGroup(@PathParam("id") Integer groupId) {
        try{
            LOGGER.debug("getGroup: ENTER");

            String email = httpRequest.getRemoteUser();
            User authenticated_user = userManager.readUserByEmail(email);
            if(!authenticated_user.getAdmin()){
                String message =  "You do not have an administrator authorization!";
                throw new WebApplicationException(
                        Response.status(Response.Status.FORBIDDEN)
                                .entity(message)
                                .build()
                );
            }

            Group group = groupManager.readGroup(groupId);
            if(group == null){
                String message =  "Group does not exist!";
                throw new WebApplicationException(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity(message)
                                .build()
                );
            }

            LOGGER.debug("getGroup: EXIT");
            return Response.ok().
                    entity(new GroupAdminModel(group)).
                    build();
        } catch (WebApplicationException e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("getGroup: EXIT");
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("getGroup: EXIT");
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }


    @POST
    public Response postGroup(Group group){
        try{
            LOGGER.debug("postGroup: ENTER");

            String email = httpRequest.getRemoteUser();
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
            allFieldsNotNull &= (group.getName() != null);
            allFieldsNotNull &= (group.getDescription()!= null);
            allFieldsNotNull &= (group.getActive()!= null);

            Boolean allFieldsValid = false;

            if (allFieldsNotNull){
                allFieldsValid = true;
                allFieldsValid &= group.getName().length()<=50;
                allFieldsValid &= group.getDescription().length()<=500;
            }

            if (!(allFieldsNotNull && allFieldsValid)) {
                String message =  "Send all required fields in a valid form.";
                throw new WebApplicationException(
                        Response.status(Response.Status.BAD_REQUEST)
                                .entity(message)
                                .build()
                );
            }

            group.setGroupId(null);
            Group groupCreated = groupManager.createGroup(group, email);
            if(groupCreated == null){
                String message =  "Name already used!";
                throw new WebApplicationException(
                        Response.status(Response.Status.CONFLICT)
                                .entity(message)
                                .build()
                );
            }

            LOGGER.debug("postGroup: EXIT");
            return Response.ok().
                    entity(new GroupModel(groupCreated)).
                    build();
        } catch (WebApplicationException e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("postGroup: EXIT");
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("postGroup: EXIT");
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deleteGroup(@PathParam("id") Integer id){
        try{
            LOGGER.debug("deleteGroup: ENTER");

            if (id.intValue() == 1){
                String message = "You do not have an authorization to delete the master group!";
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
                                .entity("You do not have an administrator authorization!")
                                .build()
                );
            }


            String message = groupManager.deleteGroup(id, email);
            if (message == null){
                throw new WebApplicationException(
                        Response.status(Response.Status.NOT_FOUND)
                                .entity("Group does not exist!")
                                .build()
                );
            }

            LOGGER.debug("deleteGroup: EXIT");
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
            LOGGER.debug("deleteGroup: EXIT");
            return e.getResponse();
        } catch (Exception e) {
            LOGGER.debug(e.getMessage());
            LOGGER.debug("deleteGroup: EXIT");
            return Response.serverError().
                    entity("Something went wrong! Contact Development Team.").
                    build();
        }
    }
}
