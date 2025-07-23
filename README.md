# In this project I completed the tasks assigned to me, both the mandatory and optional ones.
The dashboard can be downloaded from the graphana/monitoring folder in this repository. It's in json format and can be imported into Graphana locally for testing.
I'll continue in this file, answering the various explanations and questions I received in the project's readme.md file, which you emailed me.


-----------------------------------------------------------------------------------------------------------------------------------------------------------------------


# 1. Graphana , Graphana panel and Custom Metrics Documentation

As part of the monitoring strategy for our application, we have instrumented and exposed several custom metrics using **Micrometer**, which are then scraped by **Prometheus** and visualized via **Grafana** dashboards. These metrics provide essential insights into business KPIs related to orders and application performance.

---

## 1. Orders Per Product (`orders_per_product_total`)

- **Type**: Prometheus Counter
- **Description**: Tracks the total number of orders created, segmented by the product name (via the `product` label).
- **Incremented When**: Every time a new order is successfully created in the system, specifically within the `OrderService.createOrder` method.

**Example instrumentation:**
```java
meterRegistry.counter("orders_per_product", "product", order.getProductName()).increment();
```

**Usage:**  
Enables fine-grained analysis of product popularity and sales trends by monitoring how many orders have been placed per individual product.

---

## 2. Average Quantity Per Order (`order_quantity_avg`)

- **Type**: Micrometer DistributionSummary
- **Description**: Records the quantity of items in each order.

**Example instrumentation:**
```java
quantitySummary.record(order.getQuantity());
```

**Prometheus Exposes:**
- `order_quantity_avg_sum` – cumulative sum of all recorded quantities
- `order_quantity_avg_count` – the number of recorded orders

**Calculation:**  
To compute the average quantity of items ordered per transaction:
```
Average = order_quantity_avg_sum / order_quantity_avg_count
```

**Usage:**  
Helps understand customer purchasing behavior and detect significant changes in order sizes.

---

## 3. Order Processing Time (`order_processing_timer`)

- **Type**: Micrometer Timer
- **Description**: Measures the latency of the `createOrder` operation.

**Prometheus Exposes:**
- `order_processing_timer_seconds_count` – total number of recorded timings
- `order_processing_timer_seconds_sum` – total accumulated time spent
- Percentile metrics (p95, p99) for latency distribution

**Usage:**  
Provides insights into the responsiveness of the service, highlighting potential bottlenecks or performance degradations in order processing.

---

## Metric Exposure and Visualization

- **Metrics Endpoint:**  
  All custom metrics are exposed via the Spring Boot Actuator Prometheus endpoint at:
  ```
  http://<application-host>:8080/actuator/prometheus
  ```
- **Prometheus** is configured to scrape this endpoint regularly.
- **Grafana** dashboards visualize the collected metrics, enabling real-time monitoring of business KPIs and application health.

**Dashboard panels include:**
- Total orders created per product (`orders_per_product_total`)
- Average quantity per order (calculated: `order_quantity_avg_sum / order_quantity_avg_count`)
- Order processing latency (`order_processing_timer` with percentile breakdowns)
- Total orders created
- Total orders deleted
- HTTP request latency (p95, p99)
- JVM memory and CPU usage
- Logged events count (INFO, ERROR)
---

## Summary

This instrumentation approach ensures that both technical and business stakeholders have actionable visibility into application usage patterns and system performance.


--------------------------------------------------------------------------------------------------------------------------------------------------------------------


# 2. Monitoring Alerts

## High Order Creation Rate

*Threshold:*  
rate(orders_created_total[1m]) > 50

*Rationale:*  
A rate above 50 orders per minute may indicate an unusual spike in activity or potential abuse of the system. Monitoring this threshold is essential to prevent infrastructure overload. When the order count grows too rapidly, system resources (CPU, memory, database, network) risk becoming saturated, leading to slowdowns or even service interruptions.  
Setting an alert on this threshold enables prompt activation of automatic or manual scalability actions, such as increasing the number of application instances or enhancing the database. This allows the system to dynamically adapt to traffic peaks, ensuring stability and performance without interruptions for end users.

---

## HTTP 95th Percentile Latency (HTTP95Latency)

*Threshold:*  
histogram_quantile(0.95, sum(rate(http_server_requests_seconds_bucket[1m])) by (le)) > 1s for at least 2 minutes

*Rationale:*  
If the 95th percentile of latency exceeds 1 second for more than 2 minutes, it means a significant portion of requests are slow, potentially degrading the user experience and indicating application performance issues.

---

## JVM Memory Usage

*Threshold:*  
JVM memory usage > 90%

*Rationale:*  
Memory usage above 90% can indicate potential memory problems (e.g., memory leaks) that may cause slowdowns or application crashes. A timely alert enables intervention before serious failures occur.

---

## Error Logs Rate

*Threshold:*  
Logged errors > 10 per minute

*Rationale:*  
A rapid increase in logged errors is often the first sign of malfunctions or regressions. Receiving an alert when this threshold is exceeded allows you to investigate and resolve issues before they impact users.


--------------------------------------------------------------------------------------------------------------------------------------------------------------------


## 3. Tags to metrics

- Adding **tags to metrics** for richer filtering

To demonstrate knowledge and the ability to develop the optional point regarding tags, I have set up, on the custom metric named order_per_product_total, a tag related. to the range of quantities of products added to each order (ranges: 1, 2–5, 6–10, 10+). 

```java
public Order createOrder(Order order) {
        return orderProcessingTimer.record(() -> {
            Order savedOrder = repository.save(order);
            String quantityRange = getQuantityRange(order.getQuantity());

            meterRegistry.counter("orders_per_product", "product", order.getProductName(), "quantity_range", quantityRange).increment();

            quantitySummary.record(order.getQuantity());

            return savedOrder;
        });
    }
```

Then, I created a new Grafana panel 'Orders per Product (Quantity ≥ 10)' where, using this tag, I retrieve only the number of orders for each product when those orders contained more than 10 products. 

This can be useful to understand which products are often purchased in large quantities.


--------------------------------------------------------------------------------------------------------------------------------------------------------------------


# 4. Documentation: Improvement of Structured Logging for Order Flow Tracking

## Objective
Enhance the quality and structure of logs in the project to clearly and easily trace the order flow, replacing old simple textual logs (System.out.println) with structured logs in JSON format.

## What Has Been Done

### 1. Replacement of Old Textual Logs
Previous log messages, such as:


System.out.println("WebOrderController.getOrders() called");
System.out.println("Returning view name: order-list");

have been removed and replaced with structured logging calls using SLF4J.

### 2. Use of ObjectMapper for Data Serialization
To ensure that order data and other events are well formatted in the logs, a singleton ObjectMapper has been created in the controller class. With this, information (such as a map of details) is converted into JSON strings.

*Example:*
java
private final ObjectMapper mapper = new ObjectMapper();

logger.info("Order created: {}", mapper.writeValueAsString(Map.of(
    "event", "order_created",
    "order_id", order.getId(),
    "product_name", order.getProductName(),
    "quantity", order.getQuantity(),
    "status", "CREATED"
)));


### 3. Scope of Change
The modification, being a demonstration, was limited to places where logs were already present (in WebOrderController.getOrders) and to a method where logs were newly added (the order creation method).

## Advantages of Structured Logging

- *Readability:* JSON logs are easily readable by both humans and software.
- *Filterability:* Log management systems (Grafana) can filter, index, and search specific fields such as order_id or event.
- *Consistency:* Maintains a uniform log standard throughout the project.
- *Traceability:* Allows for precise tracking of every order's flow.

## Where Logs Are Visible

- *In the Docker Container:* Logs are written to the standard console (stdout).
- *Alternative (not implemented but possible):* Logs can be configured to be written to a separate file, allowing for persistence and rotation, or collected by centralized logging systems.


--------------------------------------------------------------------------------------------------------------------------------------------------------------------


## 5. Tools and Approaches for Profiling and Detecting Performance Bottlenecks

### 1. JVM Profiling Tools and Application Performance Monitoring (APM)

*VisualVM*  
- Description: Open source tool for JVM profiling, memory analysis, CPU sampling, thread/heap dumps.  
- Usage: Attach VisualVM to your application instance to identify slow methods, deadlocks, and memory leaks.

*Java Flight Recorder (JFR) & Mission Control*  
- Description: Low-overhead profiler included in OpenJDK 11+ and Oracle distributions.  
- Usage: Records runtime events, garbage collection, object allocations, and I/O. Mission Control provides visual analysis of bottlenecks.

*YourKit / JProfiler (Commercial)*  
- Description: Advanced Java profilers for deep dives into CPU, memory, threads, garbage collector, JDBC, network calls, etc.  
- Usage: Suitable for enterprise projects, offering sophisticated UIs and highly detailed analysis.

*Spring Boot Actuator + Micrometer* 
- Description: Beyond custom metrics, Actuator exports low-level metrics for HTTP, latency, JVM, thread pool, GC.  
- Usage: Identify slow endpoints, abnormal memory usage, thread pool problems directly from Prometheus/Grafana.

*Next-generation Application Performance Monitoring (APM)*  
- Examples:  [Dynatrace](https://www.dynatrace.com/)
- Usage: Provides distributed tracing, auto-detects bottlenecks, full-stack performance analysis with drill-down capabilities.

---

### 2. Data-Driven Approaches: Metrics, Alerting, and Tracing

*Latency & Throughput Metrics*  
- Constantly monitor p95/p99 HTTP latency, error counts, message queues, JVM heap.  
- Best practice: integrate alerts on key metrics (e.g., HTTP p95 > 1s for 2 min, JVM heap > 90%).

*Custom KPIs*  
- Business-specific metrics (e.g., "orders created per minute", "average quantity per order"): bottlenecks often surface as anomalies in these trends.

*Distributed Tracing (with tools like OpenTelemetry)*  
- Usage: Tracks each request end-to-end, pinpointing where time is spent in distributed/microservice architectures.

---

### 3. Operational Methodology

*Baseline and Comparison*
- Run load tests (e.g., JMeter, Gatling) and record key metrics, comparing trends over time and after each release.

*Drill-down*
- When an alert triggers, use Grafana/Prometheus to identify which endpoint, product, thread, or class causes the slowdown.

*Periodic Profiling (even in production)*
- Take regular JFR snapshots in production (low overhead) without impacting users.
--------------------------------------------------------------------------------------------------------------------------------------------------------------------

## Thank you for your time. I tried my best in this challenging project. Regards,Davide.