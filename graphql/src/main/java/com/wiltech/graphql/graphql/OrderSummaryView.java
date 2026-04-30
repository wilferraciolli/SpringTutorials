package com.wiltech.graphql.graphql;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity
@Table(name = "order_summary_view")
@Immutable // Tells JPA this is a view/read-only
public class OrderSummaryView {
    @Id
    private Long id;
    private String username;
    private Double totalAmount;
    private String status;
}