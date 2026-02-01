package com.grpcdemo.client.service;

import com.grpcdemovk.*;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class StockClientService {

    @GrpcClient("stockServiceUnaryExample")
    private StockTradingServiceGrpc.StockTradingServiceBlockingStub stockServiceBlockingStub;

    @GrpcClient("stockServerStreamingServiceExample")
    private StockTradingServiceGrpc.StockTradingServiceStub stockTradingServiceStub;

    //StockResponse getStockPrice(StockRequest)
    public StockResponse getStockPrice(String stockSymbol) {
        StockRequest request = StockRequest.newBuilder().setStockSymbol(stockSymbol).build();
        return stockServiceBlockingStub.getStockPrice(request);
    }

    public void subscribeStockPrice(String symbol) {
        StockRequest request = StockRequest.newBuilder().setStockSymbol(symbol).build();

        stockTradingServiceStub.subscribeStockPrice(request, new StreamObserver<StockResponse>() {
            @Override
            public void onNext(StockResponse stockResponse) {
                System.out.println(
                        "Stock Price Update: " + stockResponse.getStockSymbol() +
                                "\nPrice: " + stockResponse.getPrice() +
                                "\nTime: " + stockResponse.getTimestamp()
                                + "\n");
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Error: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("============== Stock price stream live update completed ================");
            }
        });
    }

    public void placeBulkOrder() {
        StreamObserver<OrderSummary> responseObserver = new StreamObserver<OrderSummary>() {
            @Override
            public void onNext(OrderSummary orderSummary) {
                System.out.println("Order summary received from server: \n");
                System.out.println("Total orders: " + orderSummary.getTotalOrders());
                System.out.println("Successful orders: " + orderSummary.getSuccessCount());
                System.out.println("Total amount: $" + orderSummary.getTotalAmount());
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("Order summary received error from server: " + throwable.getMessage());
            }

            @Override
            public void onCompleted() {
                System.out.println("Stream completed, server is done sending summary!");
            }
        };
        StreamObserver<StockOrder> requestObserver = stockTradingServiceStub.bulkStockOrder(responseObserver);

        // Send multiple stream of stock order message/request
        try {
            requestObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("1")
                            .setStockSymbol("AAPL")
                            .setOrderType("BUY")
                            .setPrice(150.5)
                            .setQuantity(10)
                            .build()
            );
            requestObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("2")
                            .setStockSymbol("GOOGL")
                            .setOrderType("BUY")
                            .setPrice(151.5)
                            .setQuantity(5)
                            .build()
            );
            requestObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("3")
                            .setStockSymbol("AMZN")
                            .setOrderType("BUY")
                            .setPrice(152.5)
                            .setQuantity(7)
                            .build()
            );
            requestObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("4")
                            .setStockSymbol("AAPL")
                            .setOrderType("BUY")
                            .setPrice(153)
                            .setQuantity(12)
                            .build()
            );
            requestObserver.onNext(
                    StockOrder.newBuilder()
                            .setOrderId("5")
                            .setStockSymbol("AAPL")
                            .setOrderType("BUY")
                            .setPrice(140)
                            .setQuantity(5)
                            .build()
            );

            // Done sending orders
            requestObserver.onCompleted();
        } catch (Exception ex) {
            requestObserver.onError(ex);
        }
    }

    public void startLiveTrading() throws InterruptedException {
        StreamObserver<StockOrder> requestObserver = stockTradingServiceStub.liveTrading(
                new StreamObserver<>() {
                    @Override
                    public void onNext(TradeStatus tradeStatus) {
                        System.out.println("Server response: " + tradeStatus);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println("Error: " + throwable.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Stream completed!");
                    }
                }
        );

        // Sending multiple order request from client
        for (int i = 1; i <= 10; i++) {
            StockOrder stockOrder = StockOrder.newBuilder()
                    .setOrderId("Order-" + i)
                    .setStockSymbol("APPL")
                    .setQuantity(new Random().nextInt(10))
                    .setPrice(100 + i)
                    .setOrderType("BUY")
                    .build();
            requestObserver.onNext(stockOrder);
            Thread.sleep(500);
        }

        requestObserver.onCompleted();

    }
}
