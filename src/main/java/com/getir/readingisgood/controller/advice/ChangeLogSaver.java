package com.getir.readingisgood.controller.advice;

import com.getir.readingisgood.domain.*;
import com.getir.readingisgood.repository.ChangeLogRepository;
import com.getir.readingisgood.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class ChangeLogSaver {
    Logger logger = LoggerFactory.getLogger(ChangeLogSaver.class);

    @Autowired
    ChangeLogRepository changeLogRepository;

    @Autowired
    CustomerRepository customerRepository;

    public void saveLog(BaseEntity o, String newValue) {
        saveLog(o, null, null, newValue);
    }

    public void saveLog(BaseEntity o, String oldValue, String newValue) {
        saveLog(o, null, oldValue, newValue);
    }

    public void saveLog(BaseEntity o, Customer customer, String newValue) {
        saveLog(o, customer, null, newValue);
    }

    public void saveLog(BaseEntity o, Customer customer, String oldValue, String newValue) {
        try {
            if (o.getId() == null)
                return;
            ChangeLogActionType type = null;
            if (o instanceof Book)
                type = oldValue == null ? ChangeLogActionType.BOOK_INSERT : ChangeLogActionType.BOOK_UPDATE_BY_USER;
            else if (o instanceof Order)
                type = oldValue == null ? ChangeLogActionType.ORDER_INSERT : ChangeLogActionType.ORDER_UPDATE;
            else if (o instanceof Customer)
                type = ChangeLogActionType.CUSTOMER_CREATED;
            else if (o.getId() == -1)
                type = ChangeLogActionType.MONTHLY_REPORT_FETCHED;

            if (customer == null)
                customer = getValidCustomer().get();
            if (customer == null)
                return;
            ChangeLog log = new ChangeLog(customer, o.getId(), type, oldValue, newValue);
            changeLogRepository.save(log);
        } catch (Exception ex) {
            logger.error("Change Log exception", ex);
        }
    }

    public Optional<Customer> getValidCustomer() {
        UserDetails userDetails =
                (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return customerRepository.findByEmail(userDetails.getUsername());
    }

    public void saveLogs(List<ChangeLog> logs) {
        for (ChangeLog log : logs) {
            try {
                changeLogRepository.save(log);
            } catch (Exception ex) {
                logger.error("Change Log exception", ex);
            }
        }
    }
}
