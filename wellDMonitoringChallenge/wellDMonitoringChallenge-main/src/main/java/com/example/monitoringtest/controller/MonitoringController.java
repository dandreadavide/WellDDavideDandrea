package com.example.monitoringtest.controller;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicInteger;

@RestController
class MonitoringController {

    private final Counter helloCounter;
    private final AtomicInteger queueSize;

    public MonitoringController(MeterRegistry registry) {
        this.helloCounter = Counter.builder("app_requests_hello_total")
                .description("Total requests to /hello endpoint")
                .register(registry);

        this.queueSize = new AtomicInteger(0);
        Gauge.builder("app_queue_size", queueSize, AtomicInteger::get)
                .description("Current size of the processing queue")
                .register(registry);
    }

    @GetMapping("/hello")
    public String hello() {
        helloCounter.increment();
        // Simulate queue increase
        queueSize.incrementAndGet();
        return "Hello, Monitoring Engineer!";
    }

    @GetMapping("/dequeue")
    public String dequeue() {
        queueSize.updateAndGet(size -> size > 0 ? size - 1 : 0);
        return "Dequeued one item. Current queue size: " + queueSize.get();
    }
}