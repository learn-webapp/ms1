package com.webapp.ms1;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HealthControllerTest {

    @Test
    void healthCheck() {
        HealthController controller = new HealthController();
        String result = controller.healthCheck();
        assertEquals("Application is up and running", result);
    }
}