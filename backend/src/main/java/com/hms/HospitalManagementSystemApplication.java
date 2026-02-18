package com.hms;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.SpringApplication;

@SpringBootApplication
@ConfigurationPropertiesScan   // <-- this is the key line
public class HospitalManagementSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(HospitalManagementSystemApplication.class, args);
    }
}
