/*
 * (c) Midland Software Limited 2019
 * Name     : DBLoader.java
 * Author   : ferraciolliw
 * Date     : 24 Sep 2019
 */
package com.wiltech.todo.bootstrap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

import com.wiltech.todo.daos.ToDoDAO;
import com.wiltech.todo.daos.UserDAO;
import com.wiltech.todo.entities.ToDo;
import com.wiltech.todo.entities.User;
import com.wiltech.todo.utils.EncryptionUtils;

import lombok.extern.java.Log;

/**
 * The type Db loader.
 */
@Log
public class DBLoader  implements CommandLineRunner {

    @Autowired
    private UserDAO userDAO;
    @Autowired
    private ToDoDAO toDoDAO;
    @Autowired
    private EncryptionUtils encryptionUtils;

    @Override
    public void run(String... args) throws Exception {
        log.info("Starting application");

        userDAO.save(User.builder().email("wil@wil").id(1l).name("Wiliam Ferraciolli").password(encryptionUtils.encrypt("password")).build());
        userDAO.save(User.builder().email("george@wil").id(2l).name("George Ferraciolli").password(encryptionUtils.encrypt("password")).build());

        toDoDAO.save(ToDo.builder().id(10L).description("Description").priority("low").userId(1l).build());
        toDoDAO.save(ToDo.builder().id(11L).description("Description").priority("medium").userId(1l).build());
        toDoDAO.save(ToDo.builder().id(12L).description("Description").priority("high").userId(1l).build());

        toDoDAO.save(ToDo.builder().id(13L).description("Description").priority("low").userId(2l).build());
        toDoDAO.save(ToDo.builder().id(14L).description("Description").priority("medium").userId(2l).build());
        toDoDAO.save(ToDo.builder().id(15L).description("Go to the gym").priority("high").userId(2l).build());
    }
}
