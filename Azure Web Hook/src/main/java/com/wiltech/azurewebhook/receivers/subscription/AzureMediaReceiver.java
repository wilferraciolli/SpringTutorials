package com.wiltech.azurewebhook.receivers.subscription;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Method to receive an authentication request form Azure Event Grid and to echo back the code.
 * This is to validate that the web hook is wainting for connections.
 */
@RestController
@RequestMapping("/webhooks")
public class AzureMediaReceiver {

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(value = "/api/updates")
    public AuthorizationResponse webhooks(@RequestBody String payload) throws IOException, InterruptedException {
        System.out.println("Received call webhooks api updates");

        if (payload.contains("validationCode")) {
            // validate
            final List<AuthorizationMapper> authorizationMapper = objectMapper.readValue(payload, new TypeReference<List<AuthorizationMapper>>() {
            });
            System.out.println(authorizationMapper);

            System.out.println(new ResponseEntity<>(authorizationMapper.get(0).getData().getValidationCode(), HttpStatus.OK));

            return new AuthorizationResponse(authorizationMapper.get(0).getData().getValidationCode());

        } else if (payload.contains("BlobDeleted")) {
            // fire encode job for asset id event
            System.out.println("Blob Deleted ");
            System.out.println(payload);
        }else{
            System.out.println(payload);
        }


        return null;
    }

    @GetMapping("/test")
    public String getSomething() {

        return "GET";
    }
}
