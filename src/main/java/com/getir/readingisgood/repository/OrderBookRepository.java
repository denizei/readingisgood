package com.getir.readingisgood.repository;

import com.getir.readingisgood.domain.Order;
import com.getir.readingisgood.domain.OrderBook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderBookRepository extends JpaRepository<OrderBook, Long> {
    List<OrderBook> findByOrder(Order order);
}
