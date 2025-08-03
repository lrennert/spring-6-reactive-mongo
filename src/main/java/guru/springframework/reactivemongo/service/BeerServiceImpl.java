package guru.springframework.reactivemongo.service;

import guru.springframework.reactivemongo.model.BeerDTO;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class BeerServiceImpl implements BeerService {

    @Override
    public Mono<BeerDTO> saveBeer(Mono<BeerDTO> beerDto) {
        return null;
    }

    @Override
    public Mono<BeerDTO> getById(String id) {
        return null;
    }
}
