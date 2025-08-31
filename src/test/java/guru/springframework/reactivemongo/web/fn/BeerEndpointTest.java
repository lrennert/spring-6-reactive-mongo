package guru.springframework.reactivemongo.web.fn;

import guru.springframework.reactivemongo.model.BeerDTO;
import guru.springframework.reactivemongo.service.BeerServiceImplTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;

@Testcontainers
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
class BeerEndpointTest {

    @Container
    @ServiceConnection
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

    @Autowired
    WebTestClient webTestClient;

    @Test
    void testPatchIdNotFound() {
        webTestClient.patch()
                .uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testPatchIdFound() {
        BeerDTO testBeer = getSavedTestBeer();
        testBeer.setBeerName("New");

        webTestClient.patch()
                .uri(BeerRouterConfig.BEER_PATH_ID, testBeer.getId())
                .body(Mono.just(testBeer), BeerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testDeleteNotFound() {
        webTestClient.delete()
                .uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(999)
    void testDeleteBeer() {
        BeerDTO testBeer = getSavedTestBeer();

        webTestClient.delete()
                .uri(BeerRouterConfig.BEER_PATH_ID, testBeer.getId())
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    @Order(5)
    void testUpdateBeerBadRequest() {
        BeerDTO testBeer = getSavedTestBeer();
        testBeer.setBeerStyle("");

        webTestClient.put()
                .uri(BeerRouterConfig.BEER_PATH_ID, testBeer)
                .body(Mono.just(testBeer), BeerDTO.class)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testUpdateBeerNotFound() {
        webTestClient.put()
                .uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(4)
    void testUpdateBeer() {
        BeerDTO testBeer = getSavedTestBeer();
        testBeer.setBeerName("New");

        webTestClient.put()
                .uri(BeerRouterConfig.BEER_PATH_ID, testBeer.getId())
                .body(Mono.just(testBeer), BeerDTO.class)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void testCreateBeerBadData() {
        BeerDTO testBeer = BeerServiceImplTest.getTestBeerDto();
        testBeer.setBeerName("");

        webTestClient.post()
                .uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(testBeer), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void testCreateBeer() {
        BeerDTO testBeer = BeerServiceImplTest.getTestBeerDto();

        webTestClient.post()
                .uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(testBeer), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().exists("location");
    }

    @Test
    void testGetByIdNotFound() {
        webTestClient.get()
                .uri(BeerRouterConfig.BEER_PATH_ID, 999)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(1)
    void testGetById() {
        BeerDTO testBeer = getSavedTestBeer();

        webTestClient.get()
                .uri(BeerRouterConfig.BEER_PATH_ID, testBeer.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody(BeerDTO.class);
    }

    @Test
    @Order(2)
    void testListBeers() {
        webTestClient.get()
                .uri(BeerRouterConfig.BEER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody().jsonPath("$.length()").value(greaterThan(1));
    }

    @Test
    @Order(3)
    void testListBeersByStyle() {
        final String BEER_STYLE = "TEST";
        BeerDTO testBeer = getSavedTestBeer();
        testBeer.setBeerStyle(BEER_STYLE);

        // create test data
        webTestClient.post().uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(testBeer), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange();

        webTestClient.get().uri(UriComponentsBuilder
                        .fromPath(BeerRouterConfig.BEER_PATH)
                        .queryParam("beerStyle", BEER_STYLE)
                        .build().toUri())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody().jsonPath("$.length()").value(equalTo(1));
    }

    public BeerDTO getSavedTestBeer() {
        FluxExchangeResult<BeerDTO> beerDTOFluxExchangeResult = webTestClient.post()
                .uri(BeerRouterConfig.BEER_PATH)
                .body(Mono.just(BeerServiceImplTest.getTestBeer()), BeerDTO.class)
                .header("Content-Type", "application/json")
                .exchange()
                .returnResult(BeerDTO.class);

        List<String> location = beerDTOFluxExchangeResult.getResponseHeaders().get("Location");

        return webTestClient.get()
                .uri(BeerRouterConfig.BEER_PATH)
                .exchange().returnResult(BeerDTO.class).getResponseBody().blockFirst();
    }
}