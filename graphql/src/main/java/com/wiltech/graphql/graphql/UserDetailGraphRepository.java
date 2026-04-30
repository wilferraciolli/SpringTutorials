package com.wiltech.graphql.graphql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.graphql.data.GraphQlRepository;

@GraphQlRepository
public interface UserDetailGraphRepository
        extends JpaRepository<UserDetailsView, Long>,
        QuerydslPredicateExecutor<UserDetailsView> {
}
