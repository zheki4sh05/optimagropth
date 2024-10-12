package com.optimagrowth.licensingservice.config;

import org.springframework.boot.context.properties.*;
import org.springframework.context.annotation.*;

@Configuration
@ConfigurationProperties(prefix= "example")
public class ServiceConfig{
    private String property;
    public String getProperty(){
        return property;
    }
}