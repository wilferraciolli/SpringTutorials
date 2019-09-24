package com.wiltech.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.wiltech.todo.daos.ToDoDAO;
import com.wiltech.todo.daos.UserDAO;
import com.wiltech.todo.entities.ToDo;
import com.wiltech.todo.entities.User;
import com.wiltech.todo.utils.EncryptionUtils;

import lombok.extern.java.Log;

@Log
@SpringBootApplication
public class ToDoApplication {



    public static void main(String[] args) {
        SpringApplication.run(ToDoApplication.class, args);
    }


}
