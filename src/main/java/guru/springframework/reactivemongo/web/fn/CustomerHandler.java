package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.model.CustomerDTO;
import guru.springframework.reactivemongo.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

@Configuration
@RequiredArgsConstructor
public class CustomerHandler {

    private final CustomerService customerService;

    public Mono<ServerResponse> createCustomer(ServerRequest request) {
        return customerService.createCustomer(request.bodyToMono(CustomerDTO.class))
                .flatMap(customerDTO ->
                        ServerResponse
                                .created(UriComponentsBuilder
                                        .fromPath(CustomerRouterConfig.CUSTOMER_PATH_ID)
                                        .build(customerDTO.getId()))
                                .build());
    }

    public Mono<ServerResponse> listCustomers(ServerRequest request) {
        return ServerResponse.ok()
                .body(customerService.listCustomers(), CustomerDTO.class);
    }

    public Mono<ServerResponse> getCustomerById(ServerRequest request) {
        return ServerResponse.ok()
                .body(customerService
                                .getCustomerById(request.pathVariable("customerId"))
                                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND))),
                        CustomerDTO.class);
    }

    public Mono<ServerResponse> updateCustomer(ServerRequest request) {
        return request.bodyToMono(CustomerDTO.class)
                .flatMap(customerDTO -> customerService.updateCustomer(request.pathVariable("customerId"), customerDTO))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(dto -> ServerResponse.noContent().build());
    }

    public Mono<ServerResponse> deleteCustomerById(ServerRequest request) {
        return customerService.getCustomerById(request.pathVariable("customerId"))
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                .flatMap(customerDTO -> customerService
                        .deleteCustomerById(request.pathVariable("customerId"))
                        .then(ServerResponse.noContent().build()));
    }
}
