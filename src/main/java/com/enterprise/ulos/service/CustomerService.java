package com.enterprise.ulos.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomerService {

    public Map<String, Object> loadCustomerContext(String customerId, String customerName) {
        return Map.of(
                "customerId", customerId,
                "customerName", customerName
        );
    }
}
