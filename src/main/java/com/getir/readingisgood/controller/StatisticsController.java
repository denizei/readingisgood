package com.getir.readingisgood.controller;

import static org.springframework.data.domain.Sort.Direction.DESC;
import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


import com.getir.readingisgood.controller.advice.ChangeLogSaver;
import com.getir.readingisgood.domain.BaseEntity;
import com.getir.readingisgood.domain.Order;
import com.getir.readingisgood.domain.OrderBook;
import com.getir.readingisgood.helper.ControllerHelper;
import com.getir.readingisgood.model.QueryObjectResponse;
import com.getir.readingisgood.model.StatisticsResponse;
import com.getir.readingisgood.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Validated
@CrossOrigin(origins = "http://localhost:8082")
@RestController
@RequestMapping("/api")
@Tag(name = "Statistics Operations", description = "Displaying Monthly Statistics")
public class StatisticsController {
    private BaseEntity logEntry = new BaseEntity() {
        @Override
        public String getId() {
            return "-1";
        }

        @Override
        public void setId(String id) {

        }
    };

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ChangeLogSaver changeLogSaver;

    @Autowired
    MongoTemplate mongoTemplate;

    @Operation(summary = "Displaying monthly statistics", description = "Users with ROLE_ADMIN role can perform this operation")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stats/monthly")
    public ResponseEntity<QueryObjectResponse<List<StatisticsResponse>>> queryOrder(
            @Valid @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM") Date startDate
            , @Valid @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM") Date endDate) {
        List<Order> stats = orderRepository.getStatsBetweenMonths(startDate, endDate);
        Map<Integer, Number[]> map = new TreeMap<>(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        });
        //find all the orders between selected dates and summarized its values
        stats.stream().forEach(order -> {
            int key = order.getOrderDate().getYear() * 100 + order.getOrderDate().getMonthValue();
            double price = 0;
            int quantity = 0;
            for (OrderBook ob : order.getBooks()) {
                price += ob.getPrice();
                quantity += ob.getQuantity();
            }
            //group orders by  year*100+month key
            map.merge(key, new Number[]{1, price, quantity}, (oldValue, newValue) -> {
                oldValue[0] = oldValue[0].intValue() + 1;
                oldValue[1] = oldValue[1].doubleValue() + newValue[1].doubleValue();
                oldValue[2] = oldValue[2].intValue() + newValue[2].doubleValue();
                return oldValue;
            });
        });
        List<StatisticsResponse> responseList = new ArrayList<>(map.size());
        for (Map.Entry<Integer, Number[]> en : map.entrySet()) {
            int monthAndYear = en.getKey();
            responseList.add(new StatisticsResponse(monthAndYear / 100, monthAndYear % 100, en.getValue()));
        }
        changeLogSaver.saveLog(logEntry, "Report fetched: " + startDate.getTime() + " to " + endDate.getTime());
        return ResponseEntity.ok(new QueryObjectResponse<>(responseList
                , ControllerHelper.getQueryMap("startDate", startDate, "endDate", endDate)));
    }
}