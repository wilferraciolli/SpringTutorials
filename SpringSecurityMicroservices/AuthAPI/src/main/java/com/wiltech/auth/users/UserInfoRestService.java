package com.wiltech.auth.users;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.wiltech.users.ApplicationUser;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserInfoRestService {

    @GetMapping(path = "/info", produces = MediaType.APPLICATION_JSON_VALUE)
    //@ApiOperation(value = "Will retrieve the information from the user available in the token", response = ApplicationUser.class)
    public ResponseEntity<ApplicationUser> getUserInfo(Principal principal) {

        // get the user logged on from the context security ont he thread
        ApplicationUser applicationUser = (ApplicationUser) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();

        return new ResponseEntity<>(applicationUser, HttpStatus.OK);
    }
}
