package guru.springframework.reactivemongo.service;

import guru.springframework.reactivemongo.domain.Beer;
import guru.springframework.reactivemongo.mapper.BeerMapper;
import guru.springframework.reactivemongo.model.BeerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@SpringBootTest
class BeerServiceImplTest {

    @Autowired
    BeerService beerService;

    @Autowired
    BeerMapper beerMapper;

    BeerDTO beerDto;

    @BeforeEach
    void setUp() {
        beerDto = beerMapper.beerToBeerDto(getTestBeer());
    }

    @Test
    void testSaveBeer() throws InterruptedException {
        Mono<BeerDTO> savedMono = beerService.saveBeer(Mono.just(beerDto));
        savedMono.subscribe(savedDto ->
                System.out.println(savedDto.getId())
        );

        Thread.sleep(1000L);
    }

    public static Beer getTestBeer() {
        return Beer.builder()
                .beerName("Space Dust")
                .beerStyle("IPA")
                .price(BigDecimal.TEN)
                .quantityOnHand(12)
                .upc("123213")
                .build();
    }
}