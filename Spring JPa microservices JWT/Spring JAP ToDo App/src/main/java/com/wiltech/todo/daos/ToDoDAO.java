/*
 * (c) Midland Software Limited 2019
 * Name     : ToDoDAO.java
 * Author   : ferraciolliw
 * Date     : 24 Sep 2019
 */
package com.wiltech.todo.daos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wiltech.todo.entities.ToDo;

/**
 * The interface To do dao.
 */
public interface ToDoDAO extends JpaRepository<ToDo, Long> {

    List<ToDo> findAllByUserId(final Long userId);
}
