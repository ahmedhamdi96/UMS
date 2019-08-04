package com.sumerge.program.integration;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sumerge.program.entities.Group;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class GroupResourcesIT {
    private static Client client;

    @BeforeClass
    public static void init(){
        client = ClientBuilder.newClient();
    }

    @Test
    public void test01createGroup() {
        // ----- TEST 1 -----
        System.out.println("> Creating group w/ master admin auth:");
        Group group = new Group();
        group.setName("Group 1");
        group.setDescription("This is group 1.");
        HttpAuthenticationFeature httpAuthenticationFeature = HttpAuthenticationFeature.basic("ahmedhamdi96@live.com", "password");
        Response response = client.target("http://localhost:8880/app/groups")
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(group, MediaType.APPLICATION_JSON))
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
        // ----- TEST 2 -----
        System.out.println("> Creating group w/ master admin auth:");
        group = new Group();
        group.setName("Group 2");
        group.setDescription("This is group 2.");
        httpAuthenticationFeature = HttpAuthenticationFeature.basic("ahmedhamdi96@live.com", "password");
        response = client.target("http://localhost:8880/app/groups")
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(group, MediaType.APPLICATION_JSON))
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
    }

    @Test
    public void test02GetAllGroups() {
        // ----- TEST 1 -----
        System.out.println("> Getting all groups w/ master admin auth:");
        HttpAuthenticationFeature httpAuthenticationFeature = HttpAuthenticationFeature.basic("ahmedhamdi96@live.com", "password");
        Response response = client.target("http://localhost:8880/app/groups")
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
    }

    @Test
    public void test03GetGroup() {
        Integer groupId = new Integer(2);
        // ----- TEST 1 -----
        System.out.println("> Getting group w/ master admin auth:");
        HttpAuthenticationFeature httpAuthenticationFeature = HttpAuthenticationFeature.basic("ahmedhamdi96@live.com", "password");
        Response response = client.target("http://localhost:8880/app/groups").path(groupId.toString())
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
    }

    @Test
    public void test04DeleteGroup() {
        // ----- TEST 1 -----
        System.out.println("> Deleting group w/ master admin auth:");
        Integer groupId = new Integer(3);
        HttpAuthenticationFeature httpAuthenticationFeature = HttpAuthenticationFeature.basic("ahmedhamdi96@live.com", "password");
        Response response = client.target("http://localhost:8880/app/groups").path(groupId.toString())
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildDelete()
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
        // ----- TEST 2 -----
        System.out.println("> Deleting group w/ master admin auth:");
        groupId = new Integer(3);
        httpAuthenticationFeature = HttpAuthenticationFeature.basic("ahmedhamdi96@live.com", "password");
        response = client.target("http://localhost:8880/app/groups").path(groupId.toString())
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildDelete()
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
    }
}
