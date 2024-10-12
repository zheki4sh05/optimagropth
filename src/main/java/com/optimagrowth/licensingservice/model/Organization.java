package com.optimagrowth.licensingservice.model;

import lombok.*;
import org.springframework.hateoas.*;

@Getter
@Setter
@ToString
public class Organization extends RepresentationModel<Organization> {

    String id;
    String name;
    String contactName;
    String contactEmail;
    String contactPhone;

}
