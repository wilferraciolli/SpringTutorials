package com.wiltech.orderservice.security;

import java.security.Principal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestProtected {

    @GetMapping("/protected")
    public String getMe(){
        return "You";
    }

    @GetMapping("/protected/test")
    public String getMe2(){
        return SecurityContextHolder.getContext().getAuthentication().toString();
    }

    @GetMapping("/protected/sec")
    public Principal getMe1(@AuthenticationPrincipal Principal principal){
        System.out.println(principal);

        return principal;
    }

}
