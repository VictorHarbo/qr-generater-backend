package com.harbojohnston.qrgeneraterbackend.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {

    @Autowired
    private OrdersRepository repository;

    public void createNewPayment(String UUID, String url) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUuid(UUID);
        orderEntity.setUrl(url);
        orderEntity.setPaymentCompleted(false);
        repository.save(orderEntity);
    }

    @Transactional
    public void updatePaymentCompleted(String uuid, boolean paymentCompleted) {
        int updatedCount = repository.updatePaymentCompletedByUuid(uuid, paymentCompleted);
        if (updatedCount == 0) {
            throw new IllegalArgumentException("No payment record found with UUID: " + uuid);
        }
    }
}
