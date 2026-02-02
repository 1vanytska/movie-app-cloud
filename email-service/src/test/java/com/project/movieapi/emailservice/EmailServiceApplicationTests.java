package com.project.movieapi.emailservice;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled
class EmailServiceApplicationTests extends AbstractIntegrationTest {

    @Test
    void contextLoads() {
        System.out.println("=== DOCKER DIAGNOSTICS ===");
        System.out.println("Docker Host config: " + System.getProperty("docker.host"));
        System.out.println("==========================");
    }
}