package org.acme;

import java.time.Duration;
import java.util.function.Consumer;

import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
import io.vertx.mutiny.ext.web.Router;
import io.vertx.mutiny.ext.web.RoutingContext;
import io.vertx.mutiny.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;

@ApplicationScoped
public class MutinyRoutes {
    @Inject
    WebClient client;

    public Consumer<RoutingContext> handle(String msg) {
        return ctx -> {
            Log.info(msg);
            ctx.response().headers().set(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN);
            ctx.endAndForget(msg);
        };
    }

    public void sleep(RoutingContext ctx) {
        Uni.createFrom().voidItem()
                .onItem().delayIt().by(Duration.ofMillis(1))
                .subscribe().with(v -> ctx.next());
    }

    public void init(@Observes Router router) {
        router.get("/mutiny/message")
                .handler(handle("Message from Mutiny Vertx"));

        router.get("/mutiny/delayed-message").handler(this::sleep).handler(handle("Delayed message from Mutiny Vertx"));

        router.get("/mutiny/pokemon")
                .handler(this::sleep)
                .handler(ctx -> {
                    Log.info("Mutiny Vertx Pokemon");
                    client.getAbs("https://pokeapi.co/api/v2/ability/1")
                            .send()
                            .subscribe().with(response -> {
                                ctx.response().headers().set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON);
                                ctx.endAndForget(response.body());
                            }, ctx::fail);
                });
    }
}
