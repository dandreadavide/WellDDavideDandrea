package com.example.monitoringtest.service;

import com.example.monitoringtest.model.Order;
import com.example.monitoringtest.repository.OrderRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository repository;
    private final MeterRegistry meterRegistry;

    private final DistributionSummary quantitySummary;
    private final Timer orderProcessingTimer;

    

    public OrderService(OrderRepository repository, MeterRegistry meterRegistry) {
        this.repository = repository;
        this.meterRegistry = meterRegistry;

        this.quantitySummary = DistributionSummary.builder("order_quantity_avg")
                .description("Average quantity for order")
                .register(meterRegistry);

        this.orderProcessingTimer = Timer.builder("order_processing_timer")
                .description("Time taken to process orders")
                .publishPercentiles(0.95, 0.99)       
                .publishPercentileHistogram(true)      
                .register(meterRegistry);
    }

    public Order createOrder(Order order) {
        return orderProcessingTimer.record(() -> {
            Order savedOrder = repository.save(order);
            String quantityRange = getQuantityRange(order.getQuantity());

            meterRegistry.counter("orders_per_product", "product", order.getProductName(), "quantity_range", quantityRange).increment();

            quantitySummary.record(order.getQuantity());

            return savedOrder;
        });
    }

    public List<Order> getAllOrders() {
        return repository.findAll();
    }

    public Order getOrderById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public void deleteOrder(Long id) {
        repository.deleteById(id);
    }

    private String getQuantityRange(Integer quantity) {
        if (quantity <= 1) return "1";
        else if (quantity <= 5) return "2-5";
        else if (quantity <= 10) return "6-10";
        else return "10+";
    }
}