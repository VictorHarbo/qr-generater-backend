package com.harbojohnston.qrgeneraterbackend.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface OrdersRepository extends JpaRepository<OrderEntity, Long> {

    @Modifying
    @Query("UPDATE OrderEntity o SET o.paymentCompleted = :paymentCompleted WHERE o.uuid = :uuid")
    int updatePaymentCompletedByUuid(@Param("uuid") String uuid, @Param("paymentCompleted") boolean paymentCompleted);



    Optional<OrderEntity> findByUuid(String uuid);

}
