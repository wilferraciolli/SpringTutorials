package com.wiltech.activemq.service;

import com.wiltech.activemq.messaging.messages.Product;

public interface OrderService {

	public void processOrder(Product product);
}
