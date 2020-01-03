package com.wiltech.timer.timer.component;

import java.util.UUID;

import org.quartz.SchedulerException;
import org.quartz.spi.InstanceIdGenerator;

/**
 * Custom class to create a id generation strategy. This class can be removed and the property can be addeed to the app.properties file.
 */
public class CustomQuartzInstanceIdGenerator implements InstanceIdGenerator {

    @Override
    public String generateInstanceId() throws SchedulerException {
        try {
            return UUID.randomUUID().toString();
        } catch (Exception ex) {
            throw new SchedulerException("Couldn't generate UUID!", ex);
        }
    }
}
