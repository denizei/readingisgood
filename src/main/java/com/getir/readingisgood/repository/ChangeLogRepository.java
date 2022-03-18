package com.getir.readingisgood.repository;

import com.getir.readingisgood.domain.Book;
import com.getir.readingisgood.domain.ChangeLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

}
