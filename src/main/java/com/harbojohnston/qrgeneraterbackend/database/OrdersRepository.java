package com.harbojohnston.qrgeneraterbackend.database;

import org.springframework.data.jpa.repository.JpaRepository;

public interface OrdersRepository extends JpaRepository<OrderEntity, Long> {
}
