package com.wiltech.azurewebhook.receivers.subscription;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AuthorizationResponse {

    private String validationResponse;
}
