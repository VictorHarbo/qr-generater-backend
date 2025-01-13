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

    /**
     * Update the "paymentCompleted" field for a specific Orders entry
     * @param id of the order to update
     * @param paymentCompleted boolean value
     * @return
     */
    @PatchMapping("/{id}")
    public String updatePaymentStatus(@PathVariable Long id, @RequestBody boolean paymentCompleted) {
        int updated = ordersRepository.updatePaymentStatus(id, paymentCompleted);

        if (updated > 0) {
            return "Payment status updated successfully!";
        } else {
            return "Payment not found.";
        }
    }
}