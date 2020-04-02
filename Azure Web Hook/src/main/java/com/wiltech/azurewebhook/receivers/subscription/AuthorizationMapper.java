package com.wiltech.azurewebhook.receivers.subscription;

import lombok.Data;

@Data
public class AuthorizationMapper {

    private String id;
    private String topic;
    private String subject;
    private AuthorizationData data;
    private String eventType;
    private String eventTime;
    private String metadataVersion;
    private String dataVersion;

}

@Data
class AuthorizationData {
    private String validationCode;
    private String validationUrl;

}
