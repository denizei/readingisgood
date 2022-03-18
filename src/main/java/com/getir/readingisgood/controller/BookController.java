package com.getir.readingisgood.controller;

import com.getir.readingisgood.controller.advice.ChangeLogSaver;
import com.getir.readingisgood.domain.Book;
import com.getir.readingisgood.domain.Customer;
import com.getir.readingisgood.helper.ControllerHelper;
import com.getir.readingisgood.model.BookResponse;
import com.getir.readingisgood.model.ObjectResponse;
import com.getir.readingisgood.model.QueryObjectResponse;
import com.getir.readingisgood.model.exception.GeneralException;
import com.getir.readingisgood.model.request.BookRequest;
import com.getir.readingisgood.model.request.BookUpdateRequest;
import com.getir.readingisgood.repository.BookRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;

@Validated
@CrossOrigin(origins = "http://localhost:8082")
@RestController
@Tag(name = "Book Operations", description = "The operations on books")
@RequestMapping("/api")
public class BookController {
    Logger logger = LoggerFactory.getLogger(BookController.class);

    @Autowired
    BookRepository bookRepository;

    @Autowired
    ChangeLogSaver changeLogSaver;

    @GetMapping("/book")
    @Operation(summary = "Listing all the books or searching a book in the database", description = "No authorization needed")
    @ApiResponse(responseCode = "200", description = "Success")
    @Parameter(name = "name", description = "Book name to search", required = false)
    public ResponseEntity<ObjectResponse<List<BookResponse>>> getAllBooks(@RequestParam(required = false) String name) {
        List<BookResponse> books = new ArrayList<>();
        if (name == null)
            bookRepository.findAll().forEach(book -> books.add(new BookResponse(book)));
        else
            bookRepository.findByName(name).forEach(book -> books.add(new BookResponse(book)));
        return new ResponseEntity<>(new ObjectResponse<>(books), HttpStatus.OK);
    }

    @GetMapping("/book/{id}")
    @Operation(summary = "Searching a book by id", description = "No authorization needed")
    @Parameter(name = "id", description = "Book id", required = true)
    @ApiResponse(responseCode = "200", description = "Success")
    @ApiResponse(responseCode = "404", description = "Book not found")
    public ResponseEntity<QueryObjectResponse<Book>> getBookByKey(@PathVariable("id") long id) {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isPresent()) {
            return new ResponseEntity<>(new QueryObjectResponse<>(book.get(), ControllerHelper.getQueryMap("id", id)), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new QueryObjectResponse<>(
                    new GeneralException(GeneralException.ErrorCode.BOOK_NOT_FOUND, id + " is not found")
                    , ControllerHelper.getQueryMap("id", id))
                    , HttpStatus.NOT_FOUND);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PostMapping("/book")
    @Operation(summary = "Creates a new book in the database", description = "Users with ROLE_ADMIN can perform this operation")
    public ResponseEntity<ObjectResponse<BookResponse>> createBook(@Valid @RequestBody BookRequest bookRequest) {
        try {
            Optional<Book> foundBook = bookRepository.findByIsbn(bookRequest.getIsbn());
            if (foundBook.isPresent()) {
                throw new GeneralException(GeneralException.ErrorCode.DUPLICATE_BOOK
                        , " Same book with ISBN : " + foundBook.get().getIsbn() + " is found");
            }
            Book newBook = bookRepository
                    .save(new Book(bookRequest.getName(), bookRequest.getAuthor(),
                            bookRequest.getPrice(), bookRequest.getPublicationYear(),
                            bookRequest.getIsbn(), bookRequest.getStockCount()));
            logger.debug("Book created with id:" + newBook.getId());
            BookResponse bookResponse = new BookResponse(newBook);
            changeLogSaver.saveLog(newBook, null, null, bookRequest.getName());
            return new ResponseEntity<>(new ObjectResponse<>(bookResponse), HttpStatus.CREATED);
        } catch (GeneralException e) {
            logger.error("", e);
            return new ResponseEntity<>(new ObjectResponse<>(e), HttpStatus.BAD_REQUEST);
        }
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/book/{id}")
    @Operation(summary = "Updates a book's stock count or price in database", description = "Users with ROLE_ADMIN can perform this operation")
    public ResponseEntity<ObjectResponse<Book>> updateBook(@PathVariable("id") long id, @Valid @RequestBody BookUpdateRequest bookUpdateRequest) {
        Optional<Book> selectedBook = bookRepository.findById(id);
        Map<String, String> change = new HashMap<>();
        if (selectedBook.isPresent()) {
            Book updatedBook = selectedBook.get();
            if (bookUpdateRequest.getPrice() != null) {
                change.put("price: " + updatedBook.getPrice(), "price: " + bookUpdateRequest.getPrice());
                updatedBook.setPrice(bookUpdateRequest.getPrice());
            }
            if (bookUpdateRequest.getStockCount() != null) {
                change.put("stock: " + updatedBook.getStockCount(), "stock: " + bookUpdateRequest.getStockCount());
                updatedBook.setStockCount(bookUpdateRequest.getStockCount());
            }
            Book book = bookRepository.save(updatedBook);
            logger.debug("Book updated with id:" + book.getId() + " => " + change);

            Customer customer = changeLogSaver.getValidCustomer().get();
            change.forEach((oldValue, newValue) -> changeLogSaver.saveLog(book, customer, oldValue, newValue));

            return new ResponseEntity<>(new ObjectResponse<>(book), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(new ObjectResponse<>(
                    new GeneralException(GeneralException.ErrorCode.BOOK_NOT_FOUND, id + " is not found"))
                    , HttpStatus.NOT_FOUND);
        }
    }
}
