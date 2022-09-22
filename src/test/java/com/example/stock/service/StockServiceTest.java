package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class StockServiceTest {
    @Autowired
    private StockService service;

    @Autowired
    private StockRepository repository;

    @BeforeEach
    void before(){
        Stock stock = new Stock(1L, 100L);
        repository.saveAndFlush(stock);
    }

    @AfterEach
    void after(){
        repository.deleteAll();
    }

    @Test
    void stock_decrease() throws Exception  {
        service.decrease(1L, 1L);

        Stock stock = repository.findById(1L).orElseThrow();

        Assertions.assertThat(stock.getQuantity()).isEqualTo(99);
    }
    
    @Test
    @DisplayName("동시성 문제 테스트")
    void concurrency() throws Exception  {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        for(int i = 0; i < threadCount; i++){
            executorService.submit(()->{
                try {
                    service.decrease(1L, 1L);
                }finally {
                    latch.countDown();
                }
            });
        }
        latch.await();

        Stock stock = repository.findById(1L).orElseThrow();

        Assertions.assertThat(stock.getQuantity()).isEqualTo(0);

    }
}