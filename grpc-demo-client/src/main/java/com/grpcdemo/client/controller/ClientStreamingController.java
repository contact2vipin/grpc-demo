package com.grpcdemo.client.controller;

import com.grpcdemo.client.dto.OrderSummaryDTO;
import com.grpcdemo.client.dto.StockOrderDTO;
import com.grpcdemo.client.service.StockClientServiceWithUI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class ClientStreamingController {

    @Autowired
    private StockClientServiceWithUI stockClientServiceWithUI;

    @PostMapping("/bulk-order")
    public OrderSummaryDTO placeBulkOrder(@RequestBody List<StockOrderDTO> orders) {
        return stockClientServiceWithUI.sendBulkOrders(orders);
    }
}
