package com.getir.readingisgood.repository;

import com.getir.readingisgood.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    @Query(value = "Select b from Book b where b.name like %:name%")
    List<Book> findByName(@Param("name") String name);

    Optional<Book> findByIsbn(String isbn);

}
