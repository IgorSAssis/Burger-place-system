package br.com.senior.burger_place.infra;

import br.com.senior.burger_place.infra.dto.FieldError;
import br.com.senior.burger_place.infra.dto.ResponseWithFieldErrors;
import br.com.senior.burger_place.infra.dto.SimpleResponseError;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionHandlerError {

    @ExceptionHandler({
            EntityNotFoundException.class
    })
    public ResponseEntity<SimpleResponseError> handleNotFound(Exception exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new SimpleResponseError(exception.getMessage()));
    }

    @ExceptionHandler({
            DuplicateKeyException.class,
            NoSuchElementException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<SimpleResponseError> handleBadRequests(Exception exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new SimpleResponseError(exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWithFieldErrors> handleBadRequestsWithFieldErrors(MethodArgumentNotValidException exception) {
        List<FieldError> errors = exception.getFieldErrors().stream()
                .map(fieldError -> new FieldError(fieldError.getField(), fieldError.getDefaultMessage()))
                .sorted((Comparator.comparing(FieldError::getField)))
                .toList();
        ResponseWithFieldErrors response = new ResponseWithFieldErrors(errors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
