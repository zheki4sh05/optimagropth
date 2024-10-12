package com.optimagrowth.licensing_service.service;

import com.optimagrowth.licensing_service.config.*;
import com.optimagrowth.licensing_service.model.*;
import com.optimagrowth.licensing_service.repository.*;
import com.optimagrowth.licensing_service.service.client.*;
import io.github.resilience4j.bulkhead.annotation.*;
import io.github.resilience4j.circuitbreaker.annotation.*;
import io.github.resilience4j.ratelimiter.annotation.*;
import io.github.resilience4j.retry.annotation.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.context.*;
import org.springframework.stereotype.*;

import java.util.*;
import java.util.concurrent.*;

@Service
public class LicenseService {

    @Autowired
    private MessageSource messages;
    @Autowired
    private  LicenseRepository licenseRepository;
    @Autowired
    private  ServiceConfig config;
    @Autowired
    private  OrganizationFeignClient organizationFeignClient;
    @Autowired
    private  OrganizationRestTemplateClient organizationRestClient;
    @Autowired
    private  OrganizationDiscoveryClient organizationDiscoveryClient;


    public License getLicense(String licenseId, String organizationId, String clientType){
        License license = licenseRepository
                .findByOrganizationIdAndLicenseId(organizationId, licenseId);
        if (null == license) {
            throw new IllegalArgumentException(
                    String.format(messages.getMessage(
                                    "license.search.error.message", null, null),
                            licenseId, organizationId));
        }

        Organization organization = retrieveOrganizationInfo(organizationId,
                clientType);
        if (null != organization) {
            license.setOrganizationName(organization.getName());
            license.setContactName(organization.getContactName());
            license.setContactEmail(organization.getContactEmail());
            license.setContactPhone(organization.getContactPhone());
        }

        return license.withComment(config.getProperty());
    }
    public License createLicense(License license,
                                String organizationId,
                                Locale locale){
        license.setLicenseId(UUID.randomUUID().toString());
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }
    public License updateLicense(License license, String organizationId){
        licenseRepository.save(license);
        return license.withComment(config.getProperty());
    }
    public String deleteLicense(String licenseId, String organizationId){
        String responseMessage = null;
        License license = new License();
        license.setLicenseId(licenseId);
        licenseRepository.delete(license);
        responseMessage = String.format(messages.getMessage(
                "license.delete.message", null, null),licenseId);
        return responseMessage;
    }
    @CircuitBreaker(name= "licenseService", fallbackMethod= "buildFallbackLicenseList")
    @Bulkhead(name= "bulkheadLicenseService", fallbackMethod= "buildFallbackLicenseList")
    @Retry(name = "retryLicenseService", fallbackMethod="buildFallbackLicenseList")
    @RateLimiter(name = "licenseService", fallbackMethod = "buildFallbackLicenseList")
    public List<License> getLicensesByOrganization(
            String organizationId) throws TimeoutException {

        return licenseRepository.findByOrganizationId(organizationId);
    }

    private List<License> buildFallbackLicenseList(String organizationId, Throwable t){
        List<License> fallbackList = new ArrayList<>();
        License license = new License();
        license.setLicenseId("0000000-00-00000");
        license.setOrganizationId(organizationId);
        license.setProductName(
                "Sorry no licensing information currently available");
        fallbackList.add(license);
        return fallbackList;
    }

    private Organization retrieveOrganizationInfo(String organizationId, String clientType) {
        Organization organization = null;

        switch (clientType) {
            case "feign":
                System.out.println("I am using the feign client");
                organization = organizationFeignClient.getOrganization(organizationId);
                break;
            case "rest":
                System.out.println("I am using the rest client");
                organization = organizationRestClient.getOrganization(organizationId);
                break;
            case "discovery":
                System.out.println("I am using the discovery client");
                organization = organizationDiscoveryClient.getOrganization(organizationId);
                break;
            default:
                organization = organizationRestClient.getOrganization(organizationId);
                break;
        }

        return organization;
    }

}
