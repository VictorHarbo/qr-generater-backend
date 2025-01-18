package com.harbojohnston.qrgeneraterbackend.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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

    public OrderEntity getEntityByUuid(String uuid) {
        Optional<OrderEntity> entity = repository.findByUuid(uuid);
        return entity.orElseThrow(() -> new RuntimeException("Entity not found with uuid: " + uuid));
    }
}
