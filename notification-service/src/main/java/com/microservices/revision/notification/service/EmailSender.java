package com.microservices.revision.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailSender {

    public void sendEmail(String orderNumber) {
        log.info("An Order just got placed with order id {}",orderNumber);
        System.out.println("Order placed successfully. Order number is " + orderNumber);
        System.out.println("Email sent");
    }
}
