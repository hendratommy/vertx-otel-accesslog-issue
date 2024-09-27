package org.acme;

import io.quarkus.logging.Log;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
public class Routes {
    @Inject
    Vertx vertx;
    @Inject
    WebClient client;

    public Handler<RoutingContext> handle(String msg) {
        return ctx -> {
            Log.info(msg);
            ctx.response().headers().set(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
            ctx.end(msg);
        };
    }

    public void sleep(RoutingContext ctx) {
        vertx.setTimer(1, v -> ctx.next());
    }

    public void init(@Observes Router router) {
        router.get("/message")
                .handler(handle("Message from Vertx"));

        router.get("/delayed-message")
                .handler(this::sleep)
                .handler(handle("Delayed message from Vertx"));

        router.get("/pokemon")
                .handler(this::sleep)
                .handler(ctx -> {
                    Log.info("Vertx Pokemon");
                    client.getAbs("https://pokeapi.co/api/v2/ability/1")
                            .send().onComplete(response -> {
                                ctx.response().headers().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
                                ctx.end(response.body());
                            }, ctx::fail);
                });
    }
}
