package com.wiltech.microservicetwo.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/two")
public class RestEndPoint {

    @GetMapping("/test")
    public String test(){

        return "Test response frm Microservice Two";
    }
}
