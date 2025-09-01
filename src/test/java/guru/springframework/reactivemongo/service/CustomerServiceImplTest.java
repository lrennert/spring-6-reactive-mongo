package guru.springframework.reactivemongo.service;

import guru.springframework.reactivemongo.model.CustomerDTO;
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

    public static CustomerDTO getTestCustomerDTO() {
        return CustomerDTO.builder()
                .customerName("Clint Barton")
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
    }
}