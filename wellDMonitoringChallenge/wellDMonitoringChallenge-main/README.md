# Candidate Assessment Project

## Overview

You will work on a **Java Spring Boot web application** with a **Thymeleaf frontend** for managing orders.

The application includes:

- REST endpoints (`/api/orders`) and web endpoints (`/web/orders`)
- Order creation with `id`, `productName`, and `quantity`
- Viewing and deleting orders via forms
- Metrics exposed via Spring Boot Actuator at `/actuator/prometheus`
- Application logging configured for INFO, ERROR, and DEBUG levels

---

## Environment

The **pre-configured environment is provided** with:

- The **Java application,** **Prometheus** and **Grafana** already set up inside `docker-compose.yml`.
- Prometheus scraping configurations for the application's `/actuator/prometheus` endpoint ready.

You can start the environment (after minimal manipulation) with:

```bash
docker-compose up
```

N.B.
Grafana is to be configured to use Prometheus as its data source.

---

## Task

### Monitoring and Dashboard Creation

Your task is to **create a Grafana dashboard** to monitor this application using **Prometheus** as the data source.

The dashboard should include:

- Total orders created
- Total orders deleted
- HTTP request latency (p95, p99)
- JVM memory and CPU usage
- Logged events count (INFO, ERROR)
- Any additional metrics you consider relevant for application stability and debugging

### Custom Metrics

As part of this assessment, you are **required to create and expose custom metrics** in the application. Examples of custom metrics you could implement:

- Number of orders per product
- Average quantity per order
- Custom application counters or timers useful for business KPIs

These custom metrics should be:

- Exposed via Prometheus in the application 
- Documented in the deliverables
- Visualized in the Grafana dashboard

### Brief Observability Strategy

In addition to the dashboard:

- Describe which **alerts** you would configure, including thresholds and the rationale behind them.

---

## Optional Improvements

You are welcome to implement additional improvements, such as:

- Adding **tags to metrics** for richer filtering
- Improving **structured logging** to trace order flows
- Tools and approaches for **profiling and identifying performance bottlenecks**
---

## Deliverables
//fare
- Create a **Git repository**.
- Add the **Grafana dashboard JSON** ready to import under a `grafana/monitoring/` folder.
- Add your updated **Java application with custom metrics** exposed.
- Create a `README.md` describing:
  - What you did
  - How to use the dashboard
  - Which panels were created and their purpose
  - Proposed alerts and thresholds
  - The custom metrics you created and their purpose

---

## Requirements

- Java 17
- Maven
- Docker and Docker Compose

---

## Objective

This assessment aims to evaluate your ability to:

- Configure effective **monitoring** on a Java web application
- Select and create **meaningful metrics** for observability and debugging
- Implement **custom Prometheus metrics** for business and technical insights
- Communicate clearly through documentation
- Apply **DevOps practices** for production stability
- Find and correct anti-patterns in the configuration

---

## Contact

For technical questions, please contact:

[attilio.gualandi@welld.ch]

---

Thank you for your time and effort on this assessment.
