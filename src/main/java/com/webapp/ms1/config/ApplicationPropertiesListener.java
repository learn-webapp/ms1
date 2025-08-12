package com.webapp.ms1.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;

@Slf4j
public class ApplicationPropertiesListener implements ApplicationListener<ApplicationPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        log.info("application properties listener started ...");
        try {
            AppConfig.initialize(event);
        } catch (Exception e) {
            log.error("Exception while initializing config at application startup : ", e);
            throw new RuntimeException("Exception while initializing config");
        }
        log.info("application properties listener completed");
    }
}
