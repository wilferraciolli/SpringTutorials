package com.wiltech.activemq.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wiltech.activemq.messaging.MessageSender;
import com.wiltech.activemq.messaging.messages.Product;

@Service("productService")
public class ProductServiceImpl implements ProductService {

    static final Logger LOG = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    MessageSender messageSender;

    @Override
    public void sendProduct(Product product) {
        LOG.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
        LOG.info("Application : sending order request {}", product);
        messageSender.sendMessage(product);
        LOG.info("+++++++++++++++++++++++++++++++++++++++++++++++++++++");
    }
}
