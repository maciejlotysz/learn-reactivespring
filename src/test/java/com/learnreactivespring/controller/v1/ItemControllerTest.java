package com.learnreactivespring.controller.v1;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    public List<Item> data() {

        return Arrays.asList(
                new Item(null, "Samsung QLED TV", 2599.99),
                new Item(null, "Philips OLED TV Ambilight", 4599.49),
                new Item(null, "LG OLED TV", 5399.99),
                new Item("ABC", "Sony Bravia 4K TV", 3199.49)
        );
    }

    @BeforeEach
    public void setUp() {

        itemReactiveRepository.deleteAll()
                 .thenMany(Flux.fromIterable(data()))
                 .flatMap(itemReactiveRepository::save)
                 .doOnNext(item -> System.out.println("Inserted value is: " + item))
                 .blockLast();

    }

    @Test
    public void getAllItem() {

        webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4);

    }

    @Test
    public void getAllItem_approach2() {

        webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(4)
                .consumeWith((response) -> {
                    List<Item> items = response.getResponseBody();
                    Objects.requireNonNull(items).forEach((item) -> Assertions.assertNotNull(item.getId()));
                });
    }

    @Test
    public void getAllItem_approach3() {

        Flux<Item> itemFlux = webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux.log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void getOneItem() {

        webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "ABC")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 3199.49);
    }

    @Test
    public void getOneItem_notFound() {

        webTestClient.get().uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "DEF")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItem() {

        Item item = new Item(null, "Panasonic OLED 4K", 5999.99);

        webTestClient.post().uri(ItemConstants.ITEM_ENDPOINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Panasonic OLED 4K")
                .jsonPath("$.price").isEqualTo(5999.99);

    }

    @Test
    public void deleteItem() {

        webTestClient.delete().uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

    }

    @Test
    public void updateItem() {

        double newPrice = 2899.49;
        Item item = new Item(null, "Sony Bravia 4K TV", newPrice);

        webTestClient.put().uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", newPrice);

    }

    @Test
    public void updateItem_notFound() {

        double newPrice = 2899.49;
        Item item = new Item(null, "Sony Bravia 4K TV", newPrice);

        webTestClient.put().uri(ItemConstants.ITEM_ENDPOINT_V1.concat("/{id}"), "DEF")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }



}
