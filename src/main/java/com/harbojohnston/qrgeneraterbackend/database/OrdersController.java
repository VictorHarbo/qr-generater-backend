package com.harbojohnston.qrgeneraterbackend.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersRepository ordersRepository;

    @GetMapping
    public List<OrderEntity> getAllOrders() {
        return ordersRepository.findAll();
    }

    @PostMapping
    public OrderEntity createOrder(@RequestBody OrderEntity orderEntity) {
        return ordersRepository.save(orderEntity);
    }
}