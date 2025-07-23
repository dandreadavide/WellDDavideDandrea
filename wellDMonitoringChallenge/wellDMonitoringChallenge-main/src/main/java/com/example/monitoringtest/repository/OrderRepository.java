package com.example.monitoringtest.repository;

import com.example.monitoringtest.model.Order;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class OrderRepository {
    private final Map<Long, Order> orders = new ConcurrentHashMap<>();
    private final AtomicLong counter = new AtomicLong(1);

    public Order save(Order order) {
        Long id = counter.getAndIncrement();
        order.setId(id);
        orders.put(id, order);
        return order;
    }

    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(orders.get(id));
    }

    public void deleteById(Long id) {
        orders.remove(id);
    }
}