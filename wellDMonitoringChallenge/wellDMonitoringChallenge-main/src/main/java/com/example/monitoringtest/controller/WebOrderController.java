package com.example.monitoringtest.controller;

import com.example.monitoringtest.model.Order;
import com.example.monitoringtest.service.OrderService;
import com.example.monitoringtest.metrics.OrderMetrics;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Controller
@RequestMapping("/web/orders")
public class WebOrderController {

    private final OrderService orderService;
    private final OrderMetrics orderMetrics;
    private static final Logger logger = LoggerFactory.getLogger(WebOrderController.class);
    private final ObjectMapper mapper = new ObjectMapper();


    public WebOrderController(OrderService orderService, OrderMetrics orderMetrics) {
        this.orderService = orderService;
        this.orderMetrics = orderMetrics;
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() {
        return "Test endpoint working!";
    }

    @GetMapping
    public String getOrders(Model model) {
        try {
            String getOrdersLog = mapper.writeValueAsString(Map.of(
            "event", "get_orders_called",
            "timestamp", Instant.now().toString()
        ));
        logger.info("getOrders called: {}", getOrdersLog);

        String returningViewLog = mapper.writeValueAsString(Map.of(
            "event", "view_returned",
            "view_name", "order-list"
        ));
        //System.out.println("WebOrderController.getOrders() called"); OLD LOG NOT STRUCTURED
        //System.out.println("Returning view name: order-list"); OLD LOG NOT STRUCTURED
        logger.info("Returning view: {}", returningViewLog);

        } catch (Exception e) {
            logger.error("Error serializing structured log data", e);
        }

        model.addAttribute("orders", orderService.getAllOrders());
        model.addAttribute("order", new Order());
        return "order-list";
}

    @PostMapping
    public String createOrder(@ModelAttribute Order order) {
        orderService.createOrder(order);
        orderMetrics.incrementOrderCount();
        // New structured log for order creation
        try {
            String orderDataJson = mapper.writeValueAsString(Map.of(
                "event", "order_created",
                "order_id", order.getId(),
                "product_name", order.getProductName(),
                "quantity", order.getQuantity(),
                "status", "CREATED"
            ));
            logger.info("Order created: {}", orderDataJson);
        } catch (Exception e) {
            logger.error("Error serializing order data for logging", e);
        }
        return "redirect:/web/orders";
    }

    @PostMapping("/{id}")
    public String deleteOrder(@PathVariable Long id, @RequestParam("_method") String method) {
        if ("delete".equalsIgnoreCase(method)) {
            orderService.deleteOrder(id);
            orderMetrics.incrementOrderDeletedCount();
        }
        return "redirect:/web/orders";
    }
}