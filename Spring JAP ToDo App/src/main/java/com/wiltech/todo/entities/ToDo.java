/*
 * (c) Midland Software Limited 2019
 * Name     : ToDo.java
 * Author   : ferraciolliw
 * Date     : 24 Sep 2019
 */
package com.wiltech.todo.entities;

import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Builder;
import lombok.Data;
import lombok.experimental.Tolerate;

/**
 * The type To do.
 */
@Data
@Builder
@Entity
@Table(name = "todos")
public class ToDo {

    @Tolerate
    ToDo() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    private Long userId;

    @NotNull
    private  String description;

    private LocalDate createdDate;

    private  String priority;

    /**
     * Set up time stamp.
     */
    @PrePersist
    void setUpTimeStamp(){
        this.createdDate = LocalDate.now();
    }

}
