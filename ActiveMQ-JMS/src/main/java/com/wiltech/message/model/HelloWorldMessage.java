package com.wiltech.message.model;

import java.io.Serializable;
import java.util.UUID;

import lombok.Data;

@Data
public class HelloWorldMessage implements Serializable {
    static final long serialVersionUID = 1L;


    private UUID id;
    private String text;

}
