package com.example.ReactiveWeb;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;

/**
 *  When the main thread completes, our data collection will be stopped.
 *
 */

public class EntityTest {

    Users user1 = new Users("Chuck", "Norris", 40);
    Users user2 = new Users("Nikolas", "Cage", 40);
    Users user3 = new Users("Jessica", "Simpson", 40);


    @Test
    public void mono() {

        // Create object
        Mono<Users> monoName = Mono.just(user1);

        // Lock the thread until we get an object
        Users blockUser = monoName.block();

        // Check for correct results
        assertEquals(monoName, blockUser);

    }

    @Test
    public void blockMono() {

        Mono<Users> monoChuck = Mono.just(user1);

        // We block the stream until we receive and process the data.
        String name = monoChuck.map(Users::getName).block();

        assertEquals(name, "Chuck");

    }

    @Test
    public void flux() {

        // Create a stream to upload data
        Flux<Users> fluxUsers = Flux.just(user1, user2, user3);

        // We get all the data and process it as it is received.
        fluxUsers.subscribe(System.out::println);

    }

    @Test
    public void fluxFilter() {

        Flux<Users> fluxUsers = Flux.just(user1, user2, user3);

        // Filter and leave Chuck Noris alone
        fluxUsers.filter(user -> user.getName().equals("Chuck"))
                 .subscribe(user -> assertEquals(user, user1));

    }

    @Test
    public void fluxMap() {

        Flux<Users> fluxUsers = Flux.just(user1, user2, user3);

        // We convert the type Users to type String
        fluxUsers.map(Users::getName)
                 .subscribe(System.out::println);

    }

    // When the main thread completes, our data collection will be stopped.
    // This code will not output anything to the console.
    @Test
    public void fluxDelayElements() {

        Flux<Users> fluxUsers = Flux.just(user1, user2, user3);

        // We expect to receive data exactly 1 second and only after that we do event processing.
        fluxUsers.delayElements(Duration.ofSeconds(1))
                 .subscribe(System.out::println);

    }

    // To avoid this, use CountDownLatch.
    @Test
    public void fluxDelayElementsCountDownLatch() {

        // Create a counter and start it at 1
        CountDownLatch countDownLatch = new CountDownLatch(1);

        Flux<Users> fluxUsers = Flux.just(user1, user2, user3);

        // Run fluxUsers with a response after one second and set the counter to reset at the end
        fluxUsers.delayElements(Duration.ofSeconds(1))
                 .doOnComplete(countDownLatch::countDown)
                 .subscribe(System.out::println);  // output every second

        // Waiting for counter reset
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


    }

}
