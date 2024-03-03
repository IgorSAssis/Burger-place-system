package br.com.senior.burger_place.domain.occupation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class CustomerDTO {
    private UUID id;
    private String name;
}
