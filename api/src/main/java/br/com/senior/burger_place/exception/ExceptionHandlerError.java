package br.com.senior.burger_place.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionHandlerError {

    @ExceptionHandler({
            DuplicateKeyException.class,
            EntityNotFoundException.class
    })
    public ResponseEntity<SimpleResponseError> handleNotFound(Exception e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new SimpleResponseError(e));
    }

    @ExceptionHandler({
            NoSuchElementException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<SimpleResponseError> handleBadRequests(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponseError(exception));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ResponseErrorWithFieldErrors>> handleBadRequestsWithFieldErrors(MethodArgumentNotValidException exception) {
        List<FieldError> fieldErrors = exception.getFieldErrors();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body((fieldErrors.stream()
                        .map(ResponseErrorWithFieldErrors::new)
                        .sorted((Comparator.comparing(ResponseErrorWithFieldErrors::field)))
                        .toList()));
    }
}
