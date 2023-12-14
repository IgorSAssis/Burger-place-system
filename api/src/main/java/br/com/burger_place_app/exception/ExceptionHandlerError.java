package br.com.burger_place_app.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class ExceptionHandlerError {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity entityNotFoundException(EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseError(e));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity methodArgumentNotValidException(MethodArgumentNotValidException e){
        var error = e.getFieldErrors();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error.stream().map(DadosErroValidacao::new).toList());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity handleNotFoundException (NoSuchElementException e){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
    }

    private record DadosErroValidacao(String field, String message){
        public DadosErroValidacao(FieldError error){
            this(error.getField(), error.getDefaultMessage());
        }
    }
    private record ResponseError(String message){
        public ResponseError(RuntimeException exception){
            this(exception.getMessage());
        }
    }
}
