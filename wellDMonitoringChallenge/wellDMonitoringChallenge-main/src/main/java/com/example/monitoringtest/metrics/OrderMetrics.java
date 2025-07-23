package com.example.monitoringtest.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrderMetrics {

    private final Counter ordersCreatedCounter;
    private final Counter ordersDeletedCounter;  

    public OrderMetrics(MeterRegistry meterRegistry) {
        this.ordersCreatedCounter = meterRegistry.counter("orders_created_total");
        this.ordersDeletedCounter = meterRegistry.counter("orders_deleted_total"); 
    }

    public void incrementOrderCount() {
        ordersCreatedCounter.increment();
    }

    public void incrementOrderDeletedCount() {
        ordersDeletedCounter.increment();
    }
}
