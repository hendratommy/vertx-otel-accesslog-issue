package org.acme;

import io.vertx.core.Vertx;
import io.vertx.ext.web.client.WebClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;

@ApplicationScoped
public class BeanProducers {
    @Produces
    @ApplicationScoped
    public WebClient webClient(Vertx vertx) {
        return WebClient.create(vertx);
    }

    @Produces
    @ApplicationScoped
    public io.vertx.mutiny.ext.web.client.WebClient mutinyWebClient(io.vertx.mutiny.core.Vertx vertx) {
        return io.vertx.mutiny.ext.web.client.WebClient.create(vertx);
    }
}
