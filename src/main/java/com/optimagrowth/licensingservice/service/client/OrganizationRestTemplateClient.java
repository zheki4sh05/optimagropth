package com.optimagrowth.licensingservice.service.client;


import com.optimagrowth.licensingservice.model.*;
import org.keycloak.adapters.springsecurity.client.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.stereotype.*;
import org.springframework.web.client.*;


@Component
public class OrganizationRestTemplateClient {
    @Autowired
   private KeycloakRestTemplate restTemplate;

    public Organization getOrganization(String organizationId){
        ResponseEntity<Organization> restExchange =
                restTemplate.exchange("http://gateway:8072/organization/ v1/organization/{organizationId}",
                        HttpMethod.GET,
                        null, Organization.class, organizationId);
        return restExchange.getBody();
    }
}

