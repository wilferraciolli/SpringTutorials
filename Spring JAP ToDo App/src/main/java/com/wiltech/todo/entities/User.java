/*
 * (c) Midland Software Limited 2019
 * Name     : User.java
 * Author   : ferraciolliw
 * Date     : 24 Sep 2019
 */
package com.wiltech.todo.entities;

import javax.persistence.Entity;

import lombok.Data;

/**
 * The type User.
 */
@Data
@Entity
public class User {

    private  String email;
    private  String name;
    private  String password;
}
