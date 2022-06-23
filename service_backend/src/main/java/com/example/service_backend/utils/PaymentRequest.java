package com.example.service_backend.utils;

import lombok.Data;
import lombok.Generated;

@Data
@Generated
public class PaymentRequest {
 
    private final String name;
    private final String cardType;
    private final String cardNumber;
    private final String cvv;

}
