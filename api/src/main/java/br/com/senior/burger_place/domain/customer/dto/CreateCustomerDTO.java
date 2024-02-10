package br.com.senior.burger_place.domain.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreateCustomerDTO {
    @NotBlank(message = "Customer name cannot be null or blank")
    private String name;
    @NotBlank(message = "Customer email cannot be null or blank")
    @Email(message = "Customer email malformed")
    private String email;
    @NotBlank(message = "Customer CPF cannot be null or blank")
    @Size(min = 11, max = 11, message = "Customer CPF malformed")
    private String cpf;
}
