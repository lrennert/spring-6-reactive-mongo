package guru.springframework.reactivemongo.service;

import guru.springframework.reactivemongo.model.CustomerDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerService {

    Mono<CustomerDTO> createCustomer(Mono<CustomerDTO> customerDTO);

    Flux<CustomerDTO> listCustomers();

    Mono<CustomerDTO> getCustomerById(String customerId);

    Mono<CustomerDTO> updateCustomer(String customerId, CustomerDTO customerDTO);

    Mono<Void> deleteCustomerById(String customerId);
}