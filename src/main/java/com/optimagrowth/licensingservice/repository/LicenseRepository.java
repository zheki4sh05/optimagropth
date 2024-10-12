package com.optimagrowth.licensingservice.repository;


import com.optimagrowth.licensingservice.model.*;
import org.springframework.data.repository.*;

import java.util.*;

public interface LicenseRepository extends CrudRepository<License,String> {

    public List<License> findByOrganizationId(String organizationId);

    public License findByOrganizationIdAndLicenseId(String organizationId, String licenseId);

}
