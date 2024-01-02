package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.address.Address;
import br.com.senior.burger_place.domain.address.AdressDto;
import br.com.senior.burger_place.domain.customer.Customer;
import br.com.senior.burger_place.domain.customer.CustomerService;
import br.com.senior.burger_place.domain.customer.dto.CustomerRegistrationDTO;
import br.com.senior.burger_place.domain.customer.dto.CustomerUpdatedDTO;
import br.com.senior.burger_place.domain.customer.dto.ListingCustomersDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(controllers = {CustomerController.class})
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {

    @Autowired
    private CustomerController customerController;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomerService customerService;
    @Autowired
    private ObjectMapper objectMapper;


    @Test
    public void register_whenRegistrationDataNotIsValid_shouldReturnHttpStatus400() throws Exception {

        AdressDto adressDto = new AdressDto(null, null, null, null, null, null, null);
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO(null, null, null, adressDto);

        ResultActions result = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(dto))
                );

        result
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(jsonPath(
                        "$[0].field",
                        CoreMatchers.is("address.city"))
                )
                .andExpect(jsonPath(
                        "$[0].message",
                        CoreMatchers.is("must not be blank")
                ))
                .andExpect(jsonPath(
                        "$[1].field",
                        CoreMatchers.is("address.neighborhood"))
                )
                .andExpect(jsonPath(
                        "$[1].message",
                        CoreMatchers.is("must not be blank")
                ))
                .andExpect(jsonPath(
                        "$[2].field",
                        CoreMatchers.is("address.postalCode"))
                )
                .andExpect(jsonPath(
                        "$[2].message",
                        CoreMatchers.is("must not be blank")
                ))
                .andExpect(jsonPath(
                        "$[3].field",
                        CoreMatchers.is("address.state"))
                )
                .andExpect(jsonPath(
                        "$[3].message",
                        CoreMatchers.is("must not be blank")
                ))
                .andExpect(jsonPath(
                        "$[4].field",
                        CoreMatchers.is("address.streetAddress"))
                )
                .andExpect(jsonPath(
                        "$[4].message",
                        CoreMatchers.is("must not be blank")
                ))
                .andExpect(jsonPath(
                        "$[5].field",
                        CoreMatchers.is("cpf"))
                )
                .andExpect(jsonPath(
                        "$[5].message",
                        CoreMatchers.is("must not be blank")
                ))
                .andExpect(jsonPath(
                        "$[6].field",
                        CoreMatchers.is("email"))
                )
                .andExpect(jsonPath(
                        "$[6].message",
                        CoreMatchers.is("must not be blank")
                ))
                .andExpect(jsonPath(
                        "$[7].field",
                        CoreMatchers.is("name"))
                )
                .andExpect(jsonPath(
                        "$[7].message",
                        CoreMatchers.is("must not be blank")
                ))
                .andDo(MockMvcResultHandlers.print());
        verify(customerService, never()).addCustomer(dto);
    }

    @Test
    public void register_whenCustomerRegistrationDTOIsValid_shouldReturnStatus201() throws Exception {
        AdressDto adressDto = new AdressDto("Rua", "Bairro", "Cidade", "Estado", "89111111", "123", "complemento");
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO("Nome", "Email", "12345678900", adressDto);
        Customer customer = new Customer(dto);
        customer.setId(1l);


        when(customerService.addCustomer(dto)).thenReturn(customer);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(dto))
                );
        String expectedLocation = String.format("http://localhost/customers/%d", 1l);

        response
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(result -> {
                    Assertions.assertTrue(result.getResponse().containsHeader("Location"));
                })
                .andExpect(result -> {
                    Assertions.assertEquals(
                            expectedLocation,
                            result.getResponse().getHeader("Location")
                    );
                })
                .andExpect(result -> {
                    Assertions.assertEquals(
                            expectedLocation,
                            result.getResponse().getRedirectedUrl()
                    );
                })
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.name",
                        CoreMatchers.containsString(customer.getName())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.email",
                        CoreMatchers.containsString(customer.getEmail())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.address.streetAddress",
                        CoreMatchers.containsString(customer.getAddress().getStreetAddress())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.address.neighborhood",
                        CoreMatchers.containsString(customer.getAddress().getNeighborhood())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.address.city",
                        CoreMatchers.containsString(customer.getAddress().getCity())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.address.state",
                        CoreMatchers.containsString(customer.getAddress().getState())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.address.postalCode",
                        CoreMatchers.containsString(customer.getAddress().getPostalCode())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.address.residentialNumber",
                        CoreMatchers.containsString(customer.getAddress().getResidentialNumber())
                ))
                .andExpect(MockMvcResultMatchers.jsonPath(
                        "$.address.complement",
                        CoreMatchers.containsString(customer.getAddress().getComplement())
                ))
                .andDo(MockMvcResultHandlers.print());

        verify(customerService, times(1)).addCustomer(dto);
    }


    @Test
    public void listAllCustomer_whenExistCustomers_shouldReturnStatus200WithCustomers() throws Exception {
        Address address = new Address(new AdressDto("Rua", "Bairro", "Cidade", "Estado", "12345678", "12", "complemento"));
        Customer customer1 = new Customer(1l, "Nome1", "email1@email.com", "12345678900", true, address);
        Customer customer2 = new Customer(2l, "Nome2", "email2@email.com", "12345678911", true, address);

        PageImpl<ListingCustomersDTO> somePage = new PageImpl<>(
                Arrays.asList(
                        new ListingCustomersDTO(customer1),
                        new ListingCustomersDTO(customer2)
                ),
                Pageable.ofSize(10), 10);
        when(this.customerService.listCustomer(any(Pageable.class))).thenReturn(somePage);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content.size()", CoreMatchers.is(2)))
                .andDo(MockMvcResultHandlers.print());
        verify(customerService, times(1)).listCustomer(any(Pageable.class));
    }

    @Test
    public void listAllCustomer_whenCustomersDoNotExist_shouldReturnStatus200WithOutAnyCustomers() throws Exception {

        PageImpl<ListingCustomersDTO> somePage = new PageImpl<>(
                List.of(),
                Pageable.ofSize(5), 10);

        when(this.customerService.listCustomer(any(Pageable.class))).thenReturn(somePage);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/customers")
                                .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(jsonPath("$.content.size()", CoreMatchers.is(0)))
                .andDo(MockMvcResultHandlers.print());
        verify(customerService, times(1)).listCustomer(any(Pageable.class));
    }

    @Test
    public void listCustomerById_whenExistCustomer_shouldReturnStatus200WithCustomer() throws Exception {
        Address address = new Address(new AdressDto("Rua", "Bairro", "Cidade", "Estado", "12345678", "12", "complemento"));
        Customer someCustomer = new Customer(1l, "Nome", "email@email.com", "99999999999", true, address);
        when(customerService.listCustomerById(1l)).thenReturn(someCustomer);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/customers/{id}", 1l)
                                .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(result -> {
                    Customer responseResult = this.objectMapper.readValue(
                            result.getResponse().getContentAsString(),
                            Customer.class
                    );

                    Assertions.assertEquals(responseResult.getName(), someCustomer.getName());
                    Assertions.assertEquals(responseResult.getEmail(), someCustomer.getEmail());
                    Assertions.assertEquals(responseResult.getAddress().getStreetAddress(), someCustomer.getAddress().getStreetAddress());
                    Assertions.assertEquals(responseResult.getAddress().getNeighborhood(), someCustomer.getAddress().getNeighborhood());
                    Assertions.assertEquals(responseResult.getAddress().getCity(), someCustomer.getAddress().getCity());
                    Assertions.assertEquals(responseResult.getAddress().getState(), someCustomer.getAddress().getState());
                    Assertions.assertEquals(responseResult.getAddress().getPostalCode(), someCustomer.getAddress().getPostalCode());
                    Assertions.assertEquals(responseResult.getAddress().getResidentialNumber(), someCustomer.getAddress().getResidentialNumber());
                    Assertions.assertEquals(responseResult.getAddress().getComplement(), someCustomer.getAddress().getComplement());
                    Assertions.assertNull(responseResult.getCpf());
                })
                .andDo(MockMvcResultHandlers.print());
        verify(customerService, times(1)).listCustomerById(1l);
    }

    @Test
    public void listCustomerById_whenCustomerDoesNotExist_shouldReturnStatus404() throws Exception {

        when(this.customerService.listCustomerById(anyLong()))
                .thenThrow(new EntityNotFoundException("Cliente não existe ou está inativado"));

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/customers/{id}", 1l)
                                .contentType(MediaType.APPLICATION_JSON)
                );
        response
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.isEmptyOrNullString())))
                .andDo(MockMvcResultHandlers.print());
        verify(customerService, times(1)).listCustomerById(anyLong());
    }

    @Test
    public void updateCustomer_whenDtoIsValid_shouldReturnStatus200() throws Exception {
        AdressDto adressDto = new AdressDto("Rua", "Bairro", "Cidade", "Estado", "12345678", "12", "complemento");
        Customer customer = new Customer(1l, "nome", "Email", "12344456777", true, new Address(adressDto));

        CustomerUpdatedDTO updatedDto = new CustomerUpdatedDTO("novoNome", "novoEmail", null);
        when(customerService.listCustomerById(1l)).thenReturn(customer);

        ResultActions response = this.mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/customers/{id}", 1l)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(this.objectMapper.writeValueAsString(updatedDto))
                );
        response
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcResultHandlers.print());
        verify(customerService, times(1)).listCustomerById(1l);
        verify(customerService, times(1)).updateCustomer(1l, updatedDto);
    }

    @Test
    public void deleteCustomer_whenCustomerExistsAndIsActive_sholdReturnStatus2004() throws Exception {
        Customer customer = new Customer(1l, "nome", "email", "12333322234", true, mock(Address.class));
        when(customerService.listCustomerById(1l)).thenReturn(customer);
        ResultActions response = this.mockMvc
                .perform(MockMvcRequestBuilders.delete("/customers/{id}", 1l)
                        .contentType(MediaType.APPLICATION_JSON));
        response
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andDo(MockMvcResultHandlers.print());
        verify(customerService, times(1)).listCustomerById(1l);
        Assertions.assertFalse(customer.isActive());
    }

}