package com.getir.readingisgood.repository;

import com.getir.readingisgood.domain.Customer;
import com.getir.readingisgood.domain.Order;
import org.springframework.data.domain.Pageable;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface OrderRepository extends MongoRepository<Order, String> {
    @Query("{'orderDate' : { $gte: ?0, $lte: ?1 } }")
    public List<Order> getAllBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query("{'orderDate' : { $gte: ?0, $lte: ?1 } }")
    public List<Order> getStatsBetweenMonths(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("{'customer.id':'?0'}")
    public List<Order> findByCustomer(String customerId, Pageable pageable);
}
