package org.mritunjd.sample.webfluxbackpressure;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.UUID;

@SpringBootApplication
public class DataPopulate {
    public static void main(String[] args) {
        new SpringApplicationBuilder(DataPopulate.class)
                .web(WebApplicationType.NONE)
                .build()
                .run(args);
    }

    @Bean
    public CommandLineRunner initData(MongoOperations mongo) {
        return (String... args) -> {

            mongo.dropCollection(Event.class);
            mongo.createCollection(Event.class, CollectionOptions.empty().size(1000000).capped());

            Flux.range(1, 100)
                    .map(i -> new Event(i.longValue(), UUID.randomUUID().toString()))
                    .map(mongo::save)
                    .blockLast(Duration.ofSeconds(5));
        };
    }


}
