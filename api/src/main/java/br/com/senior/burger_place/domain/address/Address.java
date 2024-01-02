package br.com.senior.burger_place.domain.address;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Embeddable
public class Address {
    private String streetAddress;
    private String neighborhood;
    private String city;
    private String state;
    private String postalCode;
    private String residentialNumber;
    private String complement;

    public Address(AdressDto adressDto) {
        this.streetAddress = adressDto.streetAddress();
        this.neighborhood = adressDto.neighborhood();
        this.city = adressDto.city();
        this.state = adressDto.state();
        this.postalCode = adressDto.postalCode();
        this.residentialNumber = adressDto.residentialNumber();
        this.complement = adressDto.complement();
    }

    public void updateInformationAdress(AdressDto adressDto) {
        if (adressDto.streetAddress() != null){
            this.streetAddress = adressDto.streetAddress();
        }
        if (adressDto.neighborhood() != null){
            this.neighborhood = adressDto.neighborhood();
        }
        if (adressDto.city() != null){
            this.city = adressDto.city();
        }
        if (adressDto.state() != null){
            this.state = adressDto.state();
        }
        if (adressDto.postalCode() != null){
            this.postalCode = adressDto.postalCode();
        }
        if (adressDto.residentialNumber() != null){
            this.residentialNumber = adressDto.residentialNumber();
        }
        if (adressDto.complement() != null){
            this.complement = adressDto.complement();
        }
    }
}
