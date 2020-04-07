package com.wiltech.orderservice.security;

import java.security.Principal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Test {

    @GetMapping("/test")
    public String getMe(){
        return "You";
    }

    @GetMapping("/test/test")
    public String getMe2(){
        return "You";
    }

    @GetMapping("/sec")
    public String getMe1(@AuthenticationPrincipal Principal principal){
           System.out.println("********************");
           System.out.println("********************");
           System.out.println("********************");
           System.out.println(principal);
        return "You";
    }

}
