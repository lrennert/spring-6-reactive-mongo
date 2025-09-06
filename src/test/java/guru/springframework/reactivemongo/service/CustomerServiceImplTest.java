package guru.springframework.reactivemongo.service;

import guru.springframework.reactivemongo.model.CustomerDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Testcontainers
@SpringBootTest
public class CustomerServiceImplTest {

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    CustomerService customerService;

    private void check() {
        AtomicReference<List<CustomerDTO>> atomicReference = new AtomicReference<>();

        customerService.listCustomers()
                .collectList()
                .subscribe(atomicReference::set);

        await().until(() -> atomicReference.get() != null);

        System.out.printf(">>> Customer Total: %d%n", atomicReference.get().size());
        atomicReference.get().forEach(System.out::println);
    }

    @Test
    @DisplayName("Test Create Customer Using Block")
    void testCreateCustomerBlocking() {
        CustomerDTO newCustomer = customerService.createCustomer(Mono.just(getTestCustomerDTO())).block();
        assertThat(newCustomer).isNotNull();
        assertThat(newCustomer.getId()).isNotNull();
    }

    @Test
    @DisplayName("Test Create Customer Using Subscriber")
    void testCreateCustomerNonBlocking() {
        AtomicReference<CustomerDTO> atomicReference = new AtomicReference<>();

        Mono<CustomerDTO> customerMono = customerService.createCustomer(Mono.just(getTestCustomerDTO()));
        customerMono.subscribe(atomicReference::set);

        await().until(() -> atomicReference.get() != null);

        CustomerDTO persistedDTO = atomicReference.get();
        assertThat(persistedDTO).isNotNull();
        assertThat(persistedDTO.getId()).isNotNull();
    }

    @Test
    void testListCustomers() {
        check();
        CustomerDTO customerDTO = getSavedCustomerDTO();

        AtomicReference<List<CustomerDTO>> atomicReference = new AtomicReference<>();

        customerService.listCustomers()
                .collectList()
                .subscribe(atomicReference::set);

        await().until(() -> atomicReference.get() != null);

        List<CustomerDTO> result = atomicReference.get();
        assertThat(result.size()).isGreaterThanOrEqualTo(1);
        assertThat(result).contains(customerDTO);
        check();
    }

    @Test
    void testGetCustomerById() {
        CustomerDTO customerDTO = getSavedCustomerDTO();

        AtomicReference<CustomerDTO> atomicReference = new AtomicReference<>();

        customerService.getCustomerById(customerDTO.getId())
                .subscribe(atomicReference::set);

        await().until(() -> atomicReference.get() != null);

        assertThat(atomicReference.get()).isNotNull();
    }

    private CustomerDTO getSavedCustomerDTO() {
        CustomerDTO customerDTO = customerService.createCustomer(Mono.just(getTestCustomerDTO())).block();
        Assertions.assertNotNull(customerDTO);
        customerDTO.setCreatedDate(customerDTO.getCreatedDate().truncatedTo(ChronoUnit.MILLIS));
        customerDTO.setLastModifiedDate(customerDTO.getLastModifiedDate().truncatedTo(ChronoUnit.MILLIS));
        return customerDTO;
    }

    public static CustomerDTO getTestCustomerDTO() {
        return CustomerDTO.builder()
                .customerName("Clint Barton")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }
}