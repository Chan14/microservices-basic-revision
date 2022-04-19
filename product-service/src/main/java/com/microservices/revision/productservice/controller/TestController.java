package com.microservices.revision.productservice.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RefreshScope
public class TestController {

    @Value("${test.name}")
    private String testName;

    @Value("${secret.message}")
    private String secretMessage;

    @GetMapping
    public String printTestName() {
        return testName;
    }

    @GetMapping("/message")
    public String printSecretMessage() {
        return secretMessage;
    }
}
