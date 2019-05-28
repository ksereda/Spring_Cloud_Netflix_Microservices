package com.example.service_indexer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TextController {

    @Autowired
    private TextSplitter textSplitter;

    @PostMapping("/text")
    public Integer processText(@RequestBody String body) {
        return textSplitter.process(body);
    }
}
