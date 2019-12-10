package com.wiltech.activemq.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.wiltech.activemq.messaging.MessageSender;
import com.wiltech.activemq.messaging.messages.InventoryResponse;
import com.wiltech.activemq.messaging.messages.Product;

@Service("orderService")
public class OrderServiceImpl implements OrderService{

	static final Logger LOG = LoggerFactory.getLogger(OrderServiceImpl.class);
	
	@Autowired
    MessageSender messageSender;
	
	@Override
	public void processOrder(Product product) {
		
		InventoryResponse response = prepareResponse(product);
		LOG.info("Inventory : sending order confirmation {}", response);
		messageSender.sendMessage(response);
	}
	
	private InventoryResponse prepareResponse(Product product){
		InventoryResponse response = new InventoryResponse();
		response.setProductId(product.getProductId());
		response.setReturnCode(200);
		response.setComment("Order Processed successfully");

		return response;
	}

	
	
}
