package org.mritunjd.sample.webfluxbackpressure;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

interface EventRepository extends ReactiveMongoRepository<Event, Long> {

    @Tailable
    Flux<Event> findEventsBy();

}

@SpringBootApplication
public class WebfluxBackpressureApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebfluxBackpressureApplication.class, args);
    }

}

@RestController
@RequestMapping("/events")
class EventController {
    @Autowired
    private EventRepository eventRepository;

    @GetMapping("/all")
    public Flux<Event> getAll() {
        return eventRepository.findAll().log();
    }

    @GetMapping(value = "/stream", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<Event> getAllStream() {
        Flux<Event> flux = eventRepository.findEventsBy();
        return flux.log();
    }

    @PostMapping(value = "/", consumes = MediaType.APPLICATION_STREAM_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Void> addEvents(@RequestBody Flux<Event> events) {
        System.out.println("Idhar");
        return eventRepository.insert(events).then();
    }
}

@Data
@Document
@NoArgsConstructor
@AllArgsConstructor
class Event {
    private long id;
    private String content;
}