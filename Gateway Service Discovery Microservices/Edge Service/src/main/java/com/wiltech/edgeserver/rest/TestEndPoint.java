package com.wiltech.edgeserver.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/gateway")
public class TestEndPoint {

    @GetMapping("/test")
    public String test(){

        return "Test gateway response";
    }
}
