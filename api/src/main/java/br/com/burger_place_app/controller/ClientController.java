package br.com.burger_place_app.controller;

import br.com.burger_place_app.domain.customer.CustomerService;
import br.com.burger_place_app.domain.customer.dto.CustomerRegistrationData;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("client")
public class ClientController {

    @Autowired
    CustomerService clientService;

    @PostMapping
    public void register(@RequestBody @Valid CustomerRegistrationData data){
        clientService.addCustomer(data);
    }
}
