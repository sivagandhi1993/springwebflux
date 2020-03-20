package com.nisum.springwebflux.controller.v1;

import com.nisum.springwebflux.document.Item;
import com.nisum.springwebflux.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.nisum.springwebflux.constants.ItemConstants.ITEM_END_POINT_V1;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    private ItemReactiveRepository itemReactiveRepository;

    private Logger logger = LoggerFactory.getLogger(ItemController.class);


    @GetMapping(ITEM_END_POINT_V1)
    public Flux<Item> getAllItems() {

        return itemReactiveRepository.findAll();

    }

    @GetMapping(ITEM_END_POINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id) {
        log.info("To get the one Item based on Id from Mongo DB");
        return itemReactiveRepository.findById(id)
                .map((item) -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @PostMapping(ITEM_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item) {
        log.info("To save the Item to Mongo DB" + item);
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ITEM_END_POINT_V1 + "/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {
        log.info("To delete the Item from Mongo DB based on ID and the item id deleted is " + id);
        return itemReactiveRepository.deleteById(id);
    }

    @GetMapping(ITEM_END_POINT_V1 + "/runtimeException")
    public Flux<Item> runtimeException() {
        return itemReactiveRepository.findAll()
                .concatWith(Mono.error(new RuntimeException("RuntimeException Occurred.")));
    }

    @PutMapping(ITEM_END_POINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id,
                                                 @RequestBody Item item) {
        log.info("To update the Item from Mongo DB based on ID and the item id updated is " + id);
        return itemReactiveRepository.findById(id)
                .flatMap(currentItem -> {
                    currentItem.setPrice(item.getPrice());
                    log.info("updated the price in Mongo DB of the item id and updated price is " + item.getPrice());
                    currentItem.setDescription(item.getDescription());
                    log.info("updated the description in Mongo DB of the item id and updated description is " + item.getDescription());
                    return itemReactiveRepository.save(currentItem);
                })
                .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND)).onErrorReturn(new ResponseEntity<>(HttpStatus.valueOf("Item is not updated due to technical glitch!!!")));
    }


}
