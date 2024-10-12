package com.optimagrowth.licensing_service.controller;

import com.optimagrowth.licensing_service.model.*;
import com.optimagrowth.licensing_service.service.*;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value="v1/organization/ {organizationId}/license")
public class LicenseController {

    @Autowired
    private LicenseService licenseService;
    private static final Logger logger = LoggerFactory.getLogger(LicenseController.class);

    @RequestMapping(value="/{licenseId}/{clientType}",method = RequestMethod.GET)
    public ResponseEntity<License> getLicense(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId,
            @PathVariable("clientType") String clientType) {
        License license = licenseService.getLicense(licenseId,
                organizationId,clientType);
        license.add(linkTo(methodOn(LicenseController.class)
                        .getLicense(organizationId, license.getLicenseId(), clientType))
                        .withSelfRel(),
                linkTo(methodOn(LicenseController.class)
                        .createLicense(organizationId, license, null))
                        .withRel("createLicense"),
                linkTo(methodOn(LicenseController.class)
                        .updateLicense(organizationId, license))
                        .withRel("updateLicense"),
                linkTo(methodOn(LicenseController.class)
                        .deleteLicense(organizationId, license.getLicenseId()))
                        .withRel("deleteLicense"));
        return ResponseEntity.ok(license);
    }
    @RequestMapping(value="/",method = RequestMethod.GET)
                public List<License> getLicenses( @PathVariable("organizationId")
                                      String organizationId) throws TimeoutException {
        logger.debug("LicenseServiceController Correlation id: {}",1);

        return licenseService.getLicensesByOrganization(organizationId);
    }
    @PutMapping
    public ResponseEntity<License> updateLicense(
            @PathVariable("organizationId")
            String organizationId,
            @RequestBody License request) {
        return ResponseEntity.ok(licenseService.updateLicense(request,
                organizationId));
    }
    @PostMapping
    public ResponseEntity<License> createLicense(
            @PathVariable("organizationId") String organizationId,
            @RequestBody License request,
            @RequestHeader(value = "Accept-Language",required = false)
            Locale locale){
        return ResponseEntity.ok(licenseService.createLicense(
                request, organizationId, locale));
    }
    @DeleteMapping(value="/{licenseId}")
    public ResponseEntity<String> deleteLicense(
            @PathVariable("organizationId") String organizationId,
            @PathVariable("licenseId") String licenseId) {
        return ResponseEntity.ok(licenseService.deleteLicense(licenseId,
                organizationId));
    }


}
