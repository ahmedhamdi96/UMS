package com.sumerge.program.integration;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import com.sumerge.program.entities.User;
import com.sumerge.program.models.PasswordModel;
import org.glassfish.jersey.client.ClientProperties;
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
public class UserResourcesIT {
    private static Client client;

    @BeforeClass
    public static void init(){
        client = ClientBuilder.newClient();
    }

    @Test
    public void test01createUser() {
        // ----- TEST 1 -----
        System.out.println("> Creating normal user w/o master admin auth:");
        User user = new User();
        user.setFirstName("normal");
        user.setLastName("1");
        user.setEmail("normal1@live.com");
        user.setPassword("password");
        user.setAdmin(false);
        Response response = client.target("http://localhost:8880/app/users")
                .register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.entity(user, MediaType.APPLICATION_JSON));
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
        // ----- TEST 2 -----
        System.out.println("> Creating normal user w/ master admin auth:");
        user = new User();
        user.setFirstName("normal");
        user.setLastName("1");
        user.setEmail("normal1@live.com");
        user.setPassword("password");
        user.setAdmin(false);
        HttpAuthenticationFeature httpAuthenticationFeature = HttpAuthenticationFeature.basic("ahmedhamdi96@live.com", "password");
        response = client.target("http://localhost:8880/app/users")
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(user, MediaType.APPLICATION_JSON))
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
        // ----- TEST 3 -----
        System.out.println("> Creating admin user w/ master admin auth:");
        user = new User();
        user.setFirstName("admin");
        user.setLastName("1");
        user.setEmail("admin1@live.com");
        user.setPassword("password");
        user.setAdmin(true);
        httpAuthenticationFeature = HttpAuthenticationFeature.basic("ahmedhamdi96@live.com", "password");
        response = client.target("http://localhost:8880/app/users")
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(user, MediaType.APPLICATION_JSON))
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
        // ----- TEST 4 -----
        System.out.println("> Creating normal user w/ normal auth:");
        user = new User();
        user.setFirstName("normal");
        user.setLastName("1");
        user.setEmail("normal1@live.com");
        user.setPassword("password");
        user.setAdmin(false);
        httpAuthenticationFeature = HttpAuthenticationFeature.basic("normal1@live.com", "password");
        response = client.target("http://localhost:8880/app/users")
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(user, MediaType.APPLICATION_JSON))
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
        // ----- TEST 5 -----
        System.out.println("> Creating duplicate normal user w/ admin auth:");
        user = new User();
        user.setFirstName("normal");
        user.setLastName("1");
        user.setEmail("normal1@live.com");
        user.setPassword("password");
        user.setAdmin(true);
        httpAuthenticationFeature = HttpAuthenticationFeature.basic("admin1@live.com", "password");
        response = client.target("http://localhost:8880/app/users")
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildPost(Entity.entity(user, MediaType.APPLICATION_JSON))
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
    }

    @Test
    public void test02GetAllUsers() {
        // ----- TEST 1 -----
        System.out.println("> Getting all users w/ admin auth:");
        HttpAuthenticationFeature httpAuthenticationFeature = HttpAuthenticationFeature.basic("admin1@live.com", "password");
        Response response = client.target("http://localhost:8880/app/users")
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
        // ----- TEST 2 -----
        System.out.println("> Getting all users w/ normal auth:");
        httpAuthenticationFeature = HttpAuthenticationFeature.basic("user1@live.com", "password");
        response = client.target("http://localhost:8880/app/users")
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
    }

    @Test
    public void test03GetUser() {
        Integer userId = new Integer(2);
        // ----- TEST 1 -----
        System.out.println("> Getting user w/ admin auth:");
        HttpAuthenticationFeature httpAuthenticationFeature = HttpAuthenticationFeature.basic("admin1@live.com", "password");
        Response response = client.target("http://localhost:8880/app/users").path(userId.toString())
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
        // ----- TEST 2 -----
        System.out.println("> Getting user w/ normal auth:");
        httpAuthenticationFeature = HttpAuthenticationFeature.basic("user1@live.com", "password");
        response = client.target("http://localhost:8880/app/users").path(userId.toString())
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
    }

    @Test
    public void test04PutUser() {
        // ----- TEST 1 -----
        System.out.println("> Editing normal user w/ their auth:");
        Integer userId = new Integer(2);
        User user = new User();
        user.setFirstName("edited normal");
        user.setLastName("edited 1");
        user.setEmail("editednormal1@live.com");
        HttpAuthenticationFeature httpAuthenticationFeature = HttpAuthenticationFeature.basic("normal1@live.com", "password");
        Response response = client.target("http://localhost:8880/app/users").path(userId.toString())
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildPut(Entity.entity(user, MediaType.APPLICATION_JSON))
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
        // ----- TEST 2 -----
        System.out.println("> Editing admin user w/ their auth:");
        userId = new Integer(3);
        user = new User();
        user.setFirstName("edited admin");
        user.setLastName("edited 1");
        user.setEmail("editedadmin1@live.com");
        httpAuthenticationFeature = HttpAuthenticationFeature.basic("admin1@live.com", "password");
        response = client.target("http://localhost:8880/app/users").path(userId.toString())
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildPut(Entity.entity(user, MediaType.APPLICATION_JSON))
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
    }

    @Test
    public void test05DeleteUser() {
        // ----- TEST 1 -----
        System.out.println("> Deleting user w/ admin auth:");
        Integer userId = new Integer(2);
        HttpAuthenticationFeature httpAuthenticationFeature = HttpAuthenticationFeature.basic("editedadmin1@live.com", "password");
        Response response = client.target("http://localhost:8880/app/users").path(userId.toString())
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildDelete()
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
        // ----- TEST 2 -----
        System.out.println("> Deleting user w/ admin auth:");
        userId = new Integer(2);
        httpAuthenticationFeature = HttpAuthenticationFeature.basic("editedadmin1@live.com", "password");
        response = client.target("http://localhost:8880/app/users").path(userId.toString())
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildDelete()
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
    }

    @Test
    public void test06ResetPassword() {
        // ----- TEST 1 -----
        System.out.println("> Reset admin user password w/ their auth:");
        Integer userId = new Integer(3);
        PasswordModel passwordModel = new PasswordModel();
        passwordModel.setOldPassword("password");
        passwordModel.setNewPassword("newpassword");
        HttpAuthenticationFeature httpAuthenticationFeature = HttpAuthenticationFeature.basic("editedadmin1@live.com", "password");
        Response response = client.target("http://localhost:8880/app/users").path(userId.toString()).path("password").path("reset")
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildPut(Entity.entity(passwordModel, MediaType.APPLICATION_JSON))
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
        // ----- TEST 2 -----
        System.out.println("> Reset admin user password w/ their auth:");
        userId = new Integer(3);
        passwordModel = new PasswordModel();
        passwordModel.setOldPassword("newpassword");
        passwordModel.setNewPassword("password");
        httpAuthenticationFeature = HttpAuthenticationFeature.basic("editedadmin1@live.com", "newpassword");
        response = client.target("http://localhost:8880/app/users").path(userId.toString()).path("password").path("reset")
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .buildPut(Entity.entity(passwordModel, MediaType.APPLICATION_JSON))
                .invoke();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
    }

    @Test
    public void test07AddUserGroup() {
        // ----- TEST 1 -----
        client.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, true);
        System.out.println("> Putting user 2 in group 1 w/ master admin auth:");
        Integer userId = new Integer(3);
        Integer groupId = new Integer(1);
        HttpAuthenticationFeature httpAuthenticationFeature = HttpAuthenticationFeature.basic("ahmedhamdi96@live.com", "password");
        Response response = client.target("http://localhost:8880/app/users").path(userId.toString()).path(groupId.toString())
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .put(null);
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
        client.property(ClientProperties.SUPPRESS_HTTP_COMPLIANCE_VALIDATION, false);
    }

    @Test
    public void test08DeleteUserGroup() {
        // ----- TEST 1 -----
        System.out.println("> Deleting user 2 from group 1 w/ master admin auth:");
        Integer userId = new Integer(3);
        Integer groupId = new Integer(1);
        HttpAuthenticationFeature httpAuthenticationFeature = HttpAuthenticationFeature.basic("ahmedhamdi96@live.com", "password");
        Response response = client.target("http://localhost:8880/app/users").path(userId.toString()).path(groupId.toString())
                .register(httpAuthenticationFeature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .delete();
        System.out.println(response.getStatus()+", "+response.getStatusInfo());
    }
}
