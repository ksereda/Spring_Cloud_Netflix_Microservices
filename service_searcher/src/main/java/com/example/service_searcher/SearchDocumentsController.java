package com.example.service_searcher;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SearchDocumentsController {

    @Autowired
    private DocumentSearcher documentSearcher;

    @PostMapping("/search")
    public List<Integer> searchDocuments(@RequestBody String query) {
        return documentSearcher.getDocsBySentence(query);
    }

}
