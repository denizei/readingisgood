package com.getir.readingisgood.repository;

import com.getir.readingisgood.domain.Customer;
import com.getir.readingisgood.domain.Order;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    @Query(value = "Select o from Order o where o.orderDate BETWEEN :startDate AND :endDate ORDER  BY o.orderDate desc")
    public List<Order> getAllBetweenDates(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, Pageable pageable);

    @Query(value = "Select EXTRACT(YEAR FROM o.order_date) as  yr,EXTRACT(MONTH FROM o.order_date) as mt" +
            ",count(distinct o.id),sum(ob.price)" +
            ",sum(ob.quantity) from CUSTOMER_ORDER o,order_book ob " +
            " where ob.order_id=o.id AND o.order_date BETWEEN :startDate AND :endDate " +
            " group by yr,mt order by yr desc,mt desc"
            , nativeQuery = true)
    public List<Object[]> getStatsBetweenMonths(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    public List<Order> findByCustomer(Customer customer, Pageable pageable);
}
