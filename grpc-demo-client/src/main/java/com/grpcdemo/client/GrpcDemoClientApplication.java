package com.grpcdemo.client;

import com.grpcdemo.client.service.StockClientService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GrpcDemoClientApplication implements CommandLineRunner {
    private final StockClientService stockClientService;

    public GrpcDemoClientApplication(StockClientService stockClientService) {
        this.stockClientService = stockClientService;
    }

    public static void main(String[] args) {
        SpringApplication.run(GrpcDemoClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        /*System.out.println("\n================gRPC client response: \n"+ stockClientService.getStockPrice("GOOGL"));

		System.out.println("\n============== Subscribe Stock Price ===========Server Streaming example====================\n");
		stockClientService.subscribeStockPrice("AMZN");

		System.out.println("\n============== place bulk order =========Client Streaming example======================\n");
		stockClientService.placeBulkOrder();*/

        System.out.println("\n============== live Trading =========Bi-directional Streaming example======================\n");
        stockClientService.startLiveTrading();
    }
}
