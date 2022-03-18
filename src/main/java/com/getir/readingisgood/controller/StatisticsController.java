package com.getir.readingisgood.controller;

import com.getir.readingisgood.controller.advice.ChangeLogSaver;
import com.getir.readingisgood.domain.BaseEntity;
import com.getir.readingisgood.helper.ControllerHelper;
import com.getir.readingisgood.model.QueryObjectResponse;
import com.getir.readingisgood.model.StatisticsResponse;
import com.getir.readingisgood.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Validated
@CrossOrigin(origins = "http://localhost:8082")
@RestController
@RequestMapping("/api")
@Tag(name = "Statistics Operations", description = "Displaying Monthly Statistics")
public class StatisticsController {
    private BaseEntity logEntry = new BaseEntity() {
        @Override
        public Long getId() {
            return -1L;
        }

        @Override
        public void setId(Long id) {

        }
    };

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ChangeLogSaver changeLogSaver;

    @Operation(summary = "Displaying monthly statistics", description = "Users with ROLE_ADMIN role can perform this operation")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/stats/monthly")
    public ResponseEntity<QueryObjectResponse<List<StatisticsResponse>>> queryOrder(
            @Valid @RequestParam("startDate") @DateTimeFormat(pattern = "yyyy-MM") Date startDate
            , @Valid @RequestParam("endDate") @DateTimeFormat(pattern = "yyyy-MM") Date endDate) {
        List<Object[]> stats = orderRepository.getStatsBetweenMonths(startDate, endDate);
        changeLogSaver.saveLog(logEntry, "Report fetched: " + startDate.getTime() + " to " + endDate.getTime());
        return ResponseEntity.ok(new QueryObjectResponse<>(stats.stream().map(StatisticsResponse::new)
                .collect(Collectors.toList()), ControllerHelper.getQueryMap("startDate", startDate, "endDate", endDate)));
    }
}