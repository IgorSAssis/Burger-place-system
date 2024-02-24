package br.com.senior.burger_place.domain.customer.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CustomerDTO {
    private UUID id;
    private String name;
    private String email;
    private String cpf;
    private Boolean active;
}
