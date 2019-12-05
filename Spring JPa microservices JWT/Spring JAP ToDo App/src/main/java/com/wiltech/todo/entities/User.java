/*
 * (c) Midland Software Limited 2019
 * Name     : User.java
 * Author   : ferraciolliw
 * Date     : 24 Sep 2019
 */
package com.wiltech.todo.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.experimental.Tolerate;

/**
 * The type User.
 */
@Data
@Builder
@Entity
@Table(name = "users")
public class User {

    @Tolerate
    User() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotNull
    private  String email;

    @NotNull
    private  String name;

    @NotNull
    private  String password;
}
