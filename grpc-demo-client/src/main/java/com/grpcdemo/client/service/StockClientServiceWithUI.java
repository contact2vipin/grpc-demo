package com.grpcdemo.client.service;

import com.grpcdemovk.OrderSummary;
import com.grpcdemovk.StockOrder;
import com.grpcdemovk.StockTradingServiceGrpc;
import com.grpcdemo.client.dto.OrderSummaryDTO;
import com.grpcdemo.client.dto.StockOrderDTO;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Service
public class StockClientServiceWithUI {

    @GrpcClient("stockServerStreamingServiceExample")
    private StockTradingServiceGrpc.StockTradingServiceStub stockTradingServiceStub;

    public OrderSummaryDTO sendBulkOrders(List<StockOrderDTO> ordersDTO) {
        CountDownLatch latch = new CountDownLatch(1);
        final OrderSummaryDTO resultHolder = new OrderSummaryDTO();

        StreamObserver<OrderSummary> responseObserver = new StreamObserver<>() {
            @Override
            public void onNext(OrderSummary summary) {
                // Convert Protobuf OrderSummary to DTO
                resultHolder.setTotalOrders(summary.getTotalOrders());
                resultHolder.setSuccessCount(summary.getSuccessCount());
                resultHolder.setTotalAmount(summary.getTotalAmount());
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
                latch.countDown();
            }

            @Override
            public void onCompleted() {
                latch.countDown();
            }
        };

        StreamObserver<StockOrder> requestObserver = stockTradingServiceStub.bulkStockOrder(responseObserver);

        // Convert the DTOs to Protobuf messages and send them
        for (StockOrderDTO orderDTO : ordersDTO) {
            StockOrder order = StockOrder.newBuilder()
                    .setOrderId(orderDTO.getOrderId())
                    .setStockSymbol(orderDTO.getStockSymbol())
                    .setOrderType(orderDTO.getOrderType())
                    .setPrice(orderDTO.getPrice())
                    .setQuantity(orderDTO.getQuantity())
                    .build();

            requestObserver.onNext(order);
        }

        requestObserver.onCompleted();

        try {
            latch.await(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return resultHolder; // Return DTO to the controller
    }
}