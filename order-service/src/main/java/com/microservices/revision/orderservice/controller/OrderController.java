package com.microservices.revision.orderservice.controller;

import com.microservices.revision.orderservice.client.InventoryClient;
import com.microservices.revision.orderservice.dto.OrderDto;
import com.microservices.revision.orderservice.entity.Order;
import com.microservices.revision.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/orders")
@Slf4j
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;
    private final StreamBridge streamBridge;
    private final ExecutorService executorService;

    @PostMapping
    public String placeOrder(@RequestBody OrderDto orderDto) {
        circuitBreakerFactory.configureExecutorService(executorService);
        Resilience4JCircuitBreaker circuitBreaker = circuitBreakerFactory.create("inventory");
        Supplier<Boolean> allProductsInStockSupplier = () -> orderDto.getOrderLineItems().stream()
                .allMatch(orderLineItem -> {
                    log.info("Making call to Inventory service for the sku code {}", orderLineItem.getSkuCode());
                    return inventoryClient.inStock(orderLineItem.getSkuCode());
                });
        boolean allProductsInStock = circuitBreaker.run(allProductsInStockSupplier, throwable -> handleErrorCase(throwable));

        if (!allProductsInStock) return "Order cannot be placed";
        Order order = new Order();
        order.setOrderLineItems(orderDto.getOrderLineItems());
        order.setOrderNumber(UUID.randomUUID().toString());
        orderRepository.save(order);
        log.info("Order {} just got placed.", order);
        log.info("Sending Order Id {} for the just placed order to the notification service", order.getOrderId());
        streamBridge.send("orderPlacedEventSupplier-out-0", MessageBuilder.withPayload(order.getOrderId()).build());
        return "Order placed successfully";
    }

    private Boolean handleErrorCase(Throwable throwable) {
        log.info("Circuit break happened because of the error {}", throwable.getMessage());
        return false;
    }

    @GetMapping("/{orderId}")
    @ResponseStatus(HttpStatus.OK)
    public String getOrder(@PathVariable("orderId") String orderId) {
        return "Order id = " + orderId;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public String printOrderId() {
        return "The current date and time is " + LocalDate.now();
    }
}
