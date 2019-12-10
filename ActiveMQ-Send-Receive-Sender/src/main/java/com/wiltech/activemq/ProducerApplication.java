package com.wiltech.activemq;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;


import com.wiltech.activemq.messaging.config.AppConfig;
import com.wiltech.activemq.messaging.messages.Product;
import com.wiltech.activemq.service.ProductService;

public class ProducerApplication {

    static final Logger LOG = LoggerFactory.getLogger(ProducerApplication.class);

    private static AtomicInteger id = new AtomicInteger();

    public static void main(String[] args){
        AbstractApplicationContext context = new AnnotationConfigApplicationContext(
                AppConfig.class);

        ProductService productService = (ProductService) context.getBean("productService");


        Product product = getProduct();
        LOG.info("Application : sending order {}", product);
        productService.sendProduct(product);

        try {
            Thread.sleep(60000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ((AbstractApplicationContext)context).close();
    }


    private static Product getProduct(){
        Product p = new Product();
        p.setName("Product "+id.incrementAndGet());
        p.setProductId(UUID.randomUUID().toString());
        p.setQuantity(2);
        return p;
    }
}
