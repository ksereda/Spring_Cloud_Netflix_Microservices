package com.example.ReactiveWeb;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/react")
public class Controller {

    @RequestMapping(method = RequestMethod.GET, produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<List<Entity>> load() {
        return Flux.merge(Arrays.asList(producer1(), producer2(), producer3()));
    };

    private Mono<List<Entity>> producer1() {
        return Mono.<List<Entity>>fromCallable(() -> {
            Thread.sleep(5000);
            List<Entity> entityList = new LinkedList<>();
            Entity entity1 = new Entity();
            entity1.name = "name";
            entity1.lastName = "lastName";
            Entity entity2 = new Entity();
            entity1.name = "name1";
            entity1.lastName = "lastName1";
            Entity entity3 = new Entity();
            entity1.name = "name2";
            entity1.lastName = "lastName2";
            entityList.add(entity1);
            entityList.add(entity2);
            entityList.add(entity3);
            return entityList;
        }).subscribeOn(Schedulers.elastic());
    }

    private Mono<List<Entity>> producer2() {
        return Mono.<List<Entity>>fromCallable(() -> {
            Thread.sleep(8000);
            List<Entity> entityList = new LinkedList<>();
            Entity entity1 = new Entity();
            entity1.name = "name3";
            entity1.lastName = "lastName3";
            Entity entity2 = new Entity();
            entity1.name = "name4";
            entity1.lastName = "lastName4";
            Entity entity3 = new Entity();
            entity1.name = "name5";
            entity1.lastName = "lastName5";
            entityList.add(entity1);
            entityList.add(entity2);
            entityList.add(entity3);
            return entityList;
        }).subscribeOn(Schedulers.elastic());
    }

    private Mono<List<Entity>> producer3() {
        return Mono.<List<Entity>>fromCallable(() -> {
            Thread.sleep(13000);
            List<Entity> entityList = new LinkedList<>();
            Entity entity1 = new Entity();
            entity1.name = "name6";
            entity1.lastName = "lastName6";
            Entity entity2 = new Entity();
            entity1.name = "name7";
            entity1.lastName = "lastName7";
            Entity entity3 = new Entity();
            entity1.name = "name8";
            entity1.lastName = "lastName8";
            entityList.add(entity1);
            entityList.add(entity2);
            entityList.add(entity3);
            return entityList;
        }).subscribeOn(Schedulers.elastic());
    }

}