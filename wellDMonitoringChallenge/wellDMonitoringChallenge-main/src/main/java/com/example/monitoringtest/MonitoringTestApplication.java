package com.example.monitoringtest;

import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import io.micrometer.core.instrument.MeterRegistry;

@SpringBootApplication
public class MonitoringTestApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonitoringTestApplication.class, args);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> metricsCommonTags() {
        return registry -> registry.config().commonTags("application", "monitoring-test-app");
    }
}