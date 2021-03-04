package com.learnreactivespring.initialize;

import com.learnreactivespring.document.Item;
import com.learnreactivespring.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();

    }

    public List<Item> data() {

        return Arrays.asList(
                new Item(null, "Samsung QLED TV", 2599.99),
                new Item(null, "Philips OLED TV Ambilight", 4599.49),
                new Item(null, "LG OLED TV", 5399.99),
                new Item("ABC", "SOny Bravia 4K TV", 3199.49)
        );
    }

    private void initialDataSetup() {

        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> System.out.println("Item inserted from CommandLine Runner: " + item));

    }
}
