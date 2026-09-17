package com.storex.inventory_service.service;

import com.storex.inventory_service.dto.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class InventoryConsumerService {

    @KafkaListener(topics = "storex-order-events", groupId = "inventory-group")
    public void consumeOrderEvent(OrderEvent event) {
        System.out.println("Inventory Service Received Order: " + event.getOrderId());
        // Xử lý trừ kho
    }
}
