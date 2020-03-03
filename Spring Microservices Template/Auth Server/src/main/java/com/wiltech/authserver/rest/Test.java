package com.wiltech.authserver.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class Test {

    @GetMapping("/protected")
    public String helloProtected(){

        return "Hello protected";
    }

    @GetMapping("/public")
    public String helloPublic(){

        return "Hello public";
    }
}
