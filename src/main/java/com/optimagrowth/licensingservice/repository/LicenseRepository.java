package com.optimagrowth.licensing_service.repository;

import com.optimagrowth.licensing_service.model.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface LicenseRepository extends CrudRepository<License,String> {

    public List<License> findByOrganizationId(String organizationId);

    public License findByOrganizationIdAndLicenseId(String organizationId, String licenseId);

}
