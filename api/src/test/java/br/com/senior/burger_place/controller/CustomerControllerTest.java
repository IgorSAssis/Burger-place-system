package br.com.senior.burger_place.controller;

import br.com.senior.burger_place.domain.customer.Customer;
import br.com.senior.burger_place.domain.customer.CustomerConverter;
import br.com.senior.burger_place.domain.customer.CustomerService;
import br.com.senior.burger_place.domain.customer.dto.CreateCustomerDTO;
import br.com.senior.burger_place.domain.customer.dto.CustomerDTO;
import br.com.senior.burger_place.domain.customer.dto.UpdateCustomerDTO;
import br.com.senior.burger_place.infra.dto.ResponseWithFieldErrors;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static utils.CustomerCreator.*;

@WebMvcTest(controllers = {CustomerController.class})
@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerController integration tests")
class CustomerControllerTest {
    @Autowired
    private CustomerController customerController;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomerService customerServiceMocked;
    @MockBean
    private CustomerConverter customerConverterMocked;
    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    @DisplayName("list tests")
    class ListTest {
        @Test
        void list_whenExistCustomers_shouldReturnStatus200AndPageWithCustomerDTO() throws Exception {
            List<Customer> customers = List.of(createCustomer(), createCustomer());
            Page<Customer> customerPage = new PageImpl<>(customers, Pageable.ofSize(10), customers.size());

            mockListCustomers(customerPage);
            mockToCustomerDTO(customers.get(0), createCustomerDTO());
            mockToCustomerDTO(customers.get(1), createCustomerDTO());

            ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("name", "carlos")
                    .queryParam("email", "carlos@email.com")
                    .queryParam("active", "true")
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath(
                            "$.content.size()",
                            CoreMatchers.is(customers.size())
                    ))
                    .andDo(MockMvcResultHandlers.print());

        }

        @Test
        void list_whenDoNotExistCustomers_shouldReturnStatus200AndEmptyPage() throws Exception {
            Page<Customer> customerPage = new PageImpl<>(List.of(), Pageable.ofSize(10), 0);

            mockListCustomers(customerPage);

            ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/customers")
                    .contentType(MediaType.APPLICATION_JSON)
                    .queryParam("name", "carlos")
                    .queryParam("email", "carlos@email.com")
                    .queryParam("active", "true")
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(MockMvcResultMatchers.jsonPath(
                            "$.content.size()",
                            CoreMatchers.is(0)
                    ))
                    .andDo(MockMvcResultHandlers.print());

        }

        private void mockListCustomers(Page<Customer> expectedReturn) {
            Mockito.when(customerServiceMocked.listCustomers(
                    Mockito.any(Pageable.class),
                    Mockito.eq("carlos"),
                    Mockito.eq("carlos@email.com"),
                    Mockito.eq(true)
            )).thenReturn(expectedReturn);
        }
    }

    @Nested
    @DisplayName("show tests")
    class ShowTest {
        @Test
        void show_whenCustomerExists_shouldReturnStatus200WithCustomerDTO() throws Exception {
            Customer customer = createCustomer();
            CustomerDTO customerDTO = createCustomerDTO();
            customerDTO.setId(customer.getId());

            Mockito.when(customerServiceMocked.showCustomer(Mockito.any(UUID.class))).thenReturn(customer);
            mockToCustomerDTO(customer, customerDTO);

            ResultActions response = mockMvc.perform(MockMvcRequestBuilders.get("/customers/{id}", customer.getId())
                    .contentType(MediaType.APPLICATION_JSON)
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(result -> {
                        CustomerDTO output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                CustomerDTO.class
                        );

                        assertNotNull(output);
                        assertEquals(customerDTO, output);
                    })
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @Nested
    @DisplayName("create tests")
    class CreateTest {
        @ParameterizedTest
        @CsvSource({
                ",Customer name cannot be null or blank",
                "'',Customer name cannot be null or blank"
        })
        void create_whenDTONameIsNullOrBlank_shouldReturnStatus400WithError(String name, String expectedErrorMessage) throws Exception {
            String field = "name";
            CreateCustomerDTO dto = createCreateCustomerDTO();
            dto.setName(name);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(dto))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(1, output.getErrors().size()),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())
                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @ParameterizedTest
        @CsvSource({
                ",Customer email cannot be null or blank",
                "'',Customer email cannot be null or blank",
                "carlos,Customer email malformed",
                "carlos@,Customer email malformed"
        })
        void create_whenDTOEmailIsInvalid_shouldReturnStatus400WithError(String email, String expectedErrorMessage) throws Exception {
            String field = "email";
            CreateCustomerDTO dto = createCreateCustomerDTO();
            dto.setEmail(email);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(dto))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(1, output.getErrors().size()),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())
                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @ParameterizedTest
        @CsvSource({
                ",Customer CPF cannot be null or blank",
                "'',Customer CPF cannot be null or blank",
                "123123123,Customer CPF malformed",
                "1231231230000,Customer CPF malformed"
        })
        void create_whenDTOCpfIsInvalid_shouldReturnStatus400WithError(String cpf, String expectedErrorMessage) throws Exception {
            String field = "cpf";
            CreateCustomerDTO dto = createCreateCustomerDTO();
            dto.setCpf(cpf);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(dto))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())
                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        void create_whenDTOIsValid_shouldReturnStatus201WithCustomerDTO() throws Exception {
            CreateCustomerDTO createCustomerDTO = createCreateCustomerDTO();
            Customer customer = createCustomer();
            CustomerDTO customerDTO = createCustomerDTO();
            customerDTO.setId(customer.getId());

            String expectedCreatedResource = String.format("/customers/%s", customer.getId().toString());

            Mockito.when(
                    customerServiceMocked.createCustomer(Mockito.any(CreateCustomerDTO.class))
            ).thenReturn(customer);
            mockToCustomerDTO(customer, customerDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.post("/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(createCustomerDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isCreated())
                    .andExpect(result -> {
                        CustomerDTO output = objectMapper.readValue(result.getResponse().getContentAsString(), CustomerDTO.class);

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(customerDTO, output),
                                () -> assertNotNull(result.getResponse().getRedirectedUrl()),
                                () -> assertTrue(result.getResponse().getRedirectedUrl().contains(expectedCreatedResource))
                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @Nested
    @DisplayName("update tests")
    class UpdateTest {
        @ParameterizedTest
        @CsvSource({
                "carlos,Customer email malformed",
                "carlos@,Customer email malformed"
        })
        void update_whenDTOEmailIsInvalid_shouldReturnStatus400WithError(String email, String expectedErrorMessage) throws Exception {
            String field = "email";
            UpdateCustomerDTO updateCustomerDTO = createUpdateCustomerDTO();
            updateCustomerDTO.setEmail(email);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/customers/{id}", UUID.randomUUID())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateCustomerDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isBadRequest())
                    .andExpect(result -> {
                        ResponseWithFieldErrors output = objectMapper.readValue(
                                result.getResponse().getContentAsString(),
                                ResponseWithFieldErrors.class
                        );

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(1, output.getErrors().size()),
                                () -> assertEquals(field, output.getErrors().get(0).getField()),
                                () -> assertEquals(expectedErrorMessage, output.getErrors().get(0).getMessage())
                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }

        @Test
        void update_whenDTOIsValid_shouldReturnStatus200AndCustomerDTO() throws Exception {
            UpdateCustomerDTO updateCustomerDTO = createUpdateCustomerDTO();
            Customer customer = createCustomer();
            CustomerDTO customerDTO = createCustomerDTO();
            customerDTO.setId(customer.getId());

            Mockito.when(
                    customerServiceMocked.updateCustomer(Mockito.any(UUID.class), Mockito.any(UpdateCustomerDTO.class))
            ).thenReturn(customer);
            mockToCustomerDTO(customer, customerDTO);

            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.put("/customers/{id}", customer.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsBytes(updateCustomerDTO))
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isOk())
                    .andExpect(result -> {
                        CustomerDTO output = objectMapper.readValue(result.getResponse().getContentAsString(), CustomerDTO.class);

                        assertAll(
                                () -> assertNotNull(output),
                                () -> assertEquals(customerDTO, output)
                        );
                    })
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    @Nested
    @DisplayName("inactivate tests")
    class InactivateTest {
        @Test
        void inactivate_whenCustomerExists_shouldReturnStatus204() throws Exception {
            ResultActions response = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/customers/{id}", UUID.randomUUID().toString())
                            .contentType(MediaType.APPLICATION_JSON)
            );

            response
                    .andExpect(MockMvcResultMatchers.status().isNoContent())
                    .andDo(MockMvcResultHandlers.print());
        }
    }

    private void mockToCustomerDTO(Customer customer, CustomerDTO expectedReturn) {
        Mockito.when(customerConverterMocked.toCustomerDTO(customer)).thenReturn(expectedReturn);
    }
}