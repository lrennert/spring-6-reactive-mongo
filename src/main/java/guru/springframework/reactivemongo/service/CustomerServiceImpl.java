package guru.springframework.reactivemongo.service;

import guru.springframework.reactivemongo.model.CustomerDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Override
    public Mono<CustomerDTO> createCustomer(Mono<CustomerDTO> customerDTO) {
       return null;
    }

    @Override
    public Flux<CustomerDTO> listCustomers() {
        return null;
    }

    @Override
    public Mono<CustomerDTO> getCustomerById(String customerId) {
        return null;
    }

    @Override
    public Mono<CustomerDTO> updateCustomer(String customerId, CustomerDTO customerDTO) {
        return null;
    }

    @Override
    public Mono<Void> deleteCustomerById(String customerId) {
        return null;
    }
}
