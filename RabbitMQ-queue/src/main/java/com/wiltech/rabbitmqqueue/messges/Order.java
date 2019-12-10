package com.wiltech.rabbitmqqueue.messges;

import java.io.Serializable;

/**
 *
 */
public class Order  implements Serializable {

    private String orderNumber;
    private String productId;
    private double amount;
}
