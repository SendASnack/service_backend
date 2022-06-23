package com.example.service_backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.example.service_backend.model.OrderRequest;
import com.example.service_backend.model.OrderStatus;
import com.example.service_backend.services.OrderRequestService;
import com.example.service_backend.utils.MessageResponse;
import com.example.service_backend.utils.WebHookPostBody;

@RestController
@RequestMapping("api/webhooks")
public class WebHooksController {

    private final OrderRequestService orderRequestService;

    @Autowired
    public WebHooksController(OrderRequestService orderRequestService) {
        this.orderRequestService = orderRequestService;
    }

    @PostMapping("/order/status")
    public MessageResponse registerWebHook(@RequestBody WebHookPostBody webHookPostBody) {
        // OrderStatus
        // OrderId

        for (OrderRequest order :orderRequestService.getAllOrders()){
            if (order.getOrderRequestId()==webHookPostBody.getOrderRequestId()){
                order.setOrderStatus(OrderStatus.valueOf(webHookPostBody.getValue()));
                orderRequestService.updateOrderRequestId(order);
                break;
            }
        }

        return new MessageResponse("Status updated.");

    }

}
