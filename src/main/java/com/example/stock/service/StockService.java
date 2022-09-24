package com.example.stock.service;

import com.example.stock.domain.Stock;
import com.example.stock.repository.StockRepository;
import org.hibernate.annotations.Synchronize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StockService {
    private final StockRepository stockRepository;

    public StockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public synchronized void decrease(Long id, Long quantity){
        //get stock
        Stock stock = stockRepository.findById(id).orElseThrow();
        //재고 감소
        stock.decrease(quantity);
        //저장
        stockRepository.saveAndFlush(stock);
    }


}
