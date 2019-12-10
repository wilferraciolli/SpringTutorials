package com.wiltech.activemq.service;

import com.wiltech.activemq.messaging.messages.Product;

public interface ProductService {

    public void sendProduct(Product product);

}
