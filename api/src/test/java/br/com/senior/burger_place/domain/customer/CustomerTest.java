package br.com.senior.burger_place.domain.customer;

import br.com.senior.burger_place.domain.address.Address;
import br.com.senior.burger_place.domain.address.AdressDto;
import br.com.senior.burger_place.domain.customer.dto.CustomerRegistrationDTO;
import br.com.senior.burger_place.domain.customer.dto.CustomerUpdatedDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class CustomerTest {

    @Mock
    private Address address;
    @InjectMocks
    private Customer customer;

    @Test
    public void updateInformation_whenNoNullValues_shouldUpdateData() {
        AdressDto oldAddress = new AdressDto("Rua A", "Bairro A", "Cidade A", "Estado A", "88888888", null, null);
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO("Nome Antigo", "emailAntigo@email.com", "99999999999", oldAddress);
        Customer oldCustomer = new Customer(dto);

        AdressDto adressDto = new AdressDto("Rua B", "Bairro B", "Cidade B", "Estado B", "999999999", null, null);
        CustomerUpdatedDTO customerUploadDTO = new CustomerUpdatedDTO("Novo nome", "novoEmail@email.com", adressDto);

        oldCustomer.updateInformation(customerUploadDTO);

        assertEquals("Novo nome", oldCustomer.getName());
        assertEquals("novoEmail@email.com", oldCustomer.getEmail());

    }

    @Test
    public void updateInformation_whenNullValues_shouldNotUpdateData() {
        AdressDto adressDto = mock(AdressDto.class);
        CustomerRegistrationDTO dto = new CustomerRegistrationDTO("Nome Antigo", "emailAntigo@email.com", "99999999999", adressDto);
        Customer oldCustomer = new Customer(dto);

        CustomerUpdatedDTO customerUploadDTO = new CustomerUpdatedDTO(null, null, null);

        assertThrows(IllegalArgumentException.class, () -> customer.updateInformation(customerUploadDTO));

        verify(address, never()).updateInformationAdress(any(AdressDto.class));
    }

    @Test
    public void updateInformation_whenAddressIsNotNull_shouldCallUpdateInformationAdressMethod() {
        AdressDto adressDto = new AdressDto("Rua B", "Bairro B", "Cidade B", "Estado B", "999999999", null, null);
        CustomerUpdatedDTO customerUploadDTO = new CustomerUpdatedDTO("Novo nome", "novoEmail@email.com", adressDto);

        customer.updateInformation(customerUploadDTO);

        verify(address, times(1)).updateInformationAdress(customerUploadDTO.adressDto());
    }

    @Test
    public void inactivate_whenInactivateIsCalled_activeAttributeShouldBeFalse() {

        Customer customer = new Customer(new CustomerRegistrationDTO("Ricardo Almeira", "Ricardo@email.com", "99999999900", mock(AdressDto.class)));

        assertTrue(customer.isActive());
        customer.inactivate();
        assertFalse(customer.isActive());
    }
}