package br.com.senior.burger_place.infra.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWithFieldErrors {
    private List<FieldError> errors;
}
