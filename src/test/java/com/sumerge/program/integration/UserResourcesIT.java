package com.sumerge.program.integration;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.junit.Assert;
import org.junit.Test;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import java.util.List;

public class UserResourcesIT {

    @Test
    public void testGetAllUsers() {
        Client client= ClientBuilder.newClient();
        HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic("ahmedhamdi96@live.com", "password");

        List target = client.target("http://localhost:8880/app/users").register(feature).register(JacksonJsonProvider.class)
                .request(MediaType.APPLICATION_JSON)
                .get(List.class);

        Assert.assertNotNull(target);
        System.out.println(target);
    }
}
