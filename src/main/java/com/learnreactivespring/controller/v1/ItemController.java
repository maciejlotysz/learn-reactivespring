package com.learnreactivespring.controller.v1;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.learnreactivespring.constants.ItemConstants.ITEM_ENDPOINT_V1;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ITEM_ENDPOINT_V1)
    public Flux<Item> getAllItems() {

        return itemReactiveRepository.findAll();
    }

    @GetMapping(ITEM_ENDPOINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id) {

        return itemReactiveRepository.findById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ITEM_ENDPOINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item) {

        return itemReactiveRepository.save(item);

    }

    @DeleteMapping(ITEM_ENDPOINT_V1 + "/{id}")
    public Mono<Void> deleteItem(@PathVariable String id) {

        return itemReactiveRepository.deleteById(id);

    }

    // UPDATE OPERATION
    // 1.id and item to be updated in teh requested path variable and request body
    // 2.using the id get the item from database
    // 3.update the item retrieved with the value from the requested body
    // 4.save the item
    // 5.return the saved item

    @PutMapping(ITEM_ENDPOINT_V1 + "/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item) { // 1

        return itemReactiveRepository.findById(id) // 2
                .flatMap(currentItem -> {                               // 3
                    currentItem.setPrice(item.getPrice());              // 3
                    currentItem.setDescription(item.getDescription());  // 3
                    return itemReactiveRepository.save(currentItem);    // 4

                })
                .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK)) //5
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
