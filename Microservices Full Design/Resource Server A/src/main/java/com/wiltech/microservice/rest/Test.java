package com.wiltech.microservice.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
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
