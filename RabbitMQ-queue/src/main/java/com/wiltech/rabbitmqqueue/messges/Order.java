package com.wiltech.rabbitmqqueue.messges;

import java.io.Serializable;

import javax.sql.DataSource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order  implements Serializable {

    private String orderNumber;
    private String productId;
    private double amount;
}
