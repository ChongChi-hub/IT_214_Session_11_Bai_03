package com.storex.loyalty_service.service;

import com.storex.loyalty_service.dto.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class LoyaltyConsumerService {

    @KafkaListener(topics = "storex-order-events", groupId = "loyalty-group")
    public void consumeOrderEvent(OrderEvent event) {
        System.out.println("Loyalty Service Received Order: " + event.getOrderId());
        // Xử lý cộng điểm
    }
}
