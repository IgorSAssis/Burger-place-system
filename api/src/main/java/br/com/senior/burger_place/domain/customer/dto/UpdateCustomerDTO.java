package br.com.senior.burger_place.domain.customer.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateCustomerDTO {
    private String name;
    @Email(message = "Customer email malformed")
    private String email;
}
