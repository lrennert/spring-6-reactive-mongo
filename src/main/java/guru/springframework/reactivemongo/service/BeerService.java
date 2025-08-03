package guru.springframework.reactivemongo.service;

import guru.springframework.reactivemongo.model.BeerDTO;
import reactor.core.publisher.Mono;

public interface BeerService {

    Mono<BeerDTO> saveBeer(Mono<BeerDTO> beerDto);

    Mono<BeerDTO> getById(String id);
}
