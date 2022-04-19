package com.microservices.revision.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

//The name annotation parameter for annotation FeignClient must match the
//application parameter spring.application.name of the target microservice.
@FeignClient(name = "inventory-service")
public interface InventoryClient {

    @GetMapping("/api/inventory/{skuCode}")
    Boolean inStock(@PathVariable String skuCode);
}
