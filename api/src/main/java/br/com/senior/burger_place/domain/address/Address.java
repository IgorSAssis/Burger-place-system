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

    public Address(AdressData adressData) {
        this.streetAddress = adressData.streetAddress();
        this.neighborhood = adressData.neighborhood();
        this.city = adressData.city();
        this.state = adressData.state();
        this.postalCode = adressData.postalCode();
        this.residentialNumber = adressData.residentialNumber();
        this.complement = adressData.complement();
    }

    public void updateInformation(AdressData adressData) {
        if (adressData.streetAddress() != null){
            this.streetAddress = adressData.streetAddress();
        }
        if (adressData.neighborhood() != null){
            this.neighborhood = adressData.neighborhood();
        }
        if (adressData.city() != null){
            this.city = adressData.city();
        }
        if (adressData.state() != null){
            this.state = adressData.state();
        }
        if (adressData.postalCode() != null){
            this.postalCode = adressData.postalCode();
        }
        if (adressData.residentialNumber() != null){
            this.residentialNumber = adressData.residentialNumber();
        }
        if (adressData.complement() != null){
            this.complement = adressData.complement();
        }
    }
}
