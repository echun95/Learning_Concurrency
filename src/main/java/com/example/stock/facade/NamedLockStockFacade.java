package com.example.stock.facade;

import com.example.stock.repository.LockRepository;
import com.example.stock.service.StockService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class NamedLockStockFacade {
    private final LockRepository lockRepository;
    private final StockService service;

    public NamedLockStockFacade(LockRepository lockRepository, StockService service) {
        this.lockRepository = lockRepository;
        this.service = service;
    }

    @Transactional
    public void decrease(Long id, Long quantity){
        try{
            lockRepository.getLock(id.toString());
            service.decrease(id, quantity); //얘만 롤백이 되야함
        }finally {
            lockRepository.releaseLock(id.toString());
        }
    }
}
