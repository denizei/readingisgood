package com.getir.readingisgood.repository;

import com.getir.readingisgood.domain.Book;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BookRepository extends MongoRepository<Book, String> {
    @Query(value = "{'name': {$regex : ?0, $options: 'i'}}")
    List<Book> findByName(@Param("name") String name);

    @Query("{isbn:'?0'}")
    Optional<Book> findByIsbn(String isbn);

}
