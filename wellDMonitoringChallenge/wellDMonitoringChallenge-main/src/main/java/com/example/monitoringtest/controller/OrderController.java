package com.example.monitoringtest.controller;

import com.example.monitoringtest.model.Order;
import com.example.monitoringtest.service.OrderService;
import com.example.monitoringtest.metrics.OrderMetrics;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMetrics orderMetrics;

    public OrderController(OrderService orderService, OrderMetrics orderMetrics) {
        this.orderService = orderService;
        this.orderMetrics = orderMetrics;
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order);
        orderMetrics.incrementOrderCount();
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<List<Order>> getOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        try {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}