package com.learnreactivespring.router;

import com.learnreactivespring.constants.ItemConstants;
import com.learnreactivespring.handler.ItemsHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.learnreactivespring.constants.ItemConstants.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class ItemsRouter {

    @Bean
    public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler) {

        return RouterFunctions
                .route(GET(ITEM_FUNCTIONAL_ENDPOINT_V1)
                .and(accept(MediaType.APPLICATION_JSON)),itemsHandler::geAllItems)
                .andRoute(GET(ITEM_FUNCTIONAL_ENDPOINT_V1 + "/{id}")
                .and(accept(MediaType.APPLICATION_JSON)), itemsHandler::getOneItem);
    }
}