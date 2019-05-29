package com.example.store_original_data;

import com.example.store_original_data.dao.TextRepository;
import com.example.store_original_data.entity.Text;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
public class StoreDataController {

    @Autowired
    private TextRepository textRepository;

    @Autowired
    private TextSaver textSaver;

    @RequestMapping("/hello")
    public String hello(){
        return "Hello World";
    }

    @PostMapping("/text")
    public Mono<Integer> addText(@RequestBody String text) {
        log.info("got text: " + text);
        return textSaver.saveTextToDbReactive(text);
    }

    @RequestMapping("/text/{id}")
    public Mono<Text> getText(@PathVariable Integer id){
        Mono<Text> response = textRepository.findById(id);
        return response;
    }

}
