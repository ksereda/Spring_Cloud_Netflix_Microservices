package com.example.store_inverted_index;

import com.example.store_inverted_index.entity.ReverseIndexPair;
import com.example.store_inverted_index.entity.SimplePair;
import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import ru.java.mentor.storeinvertedindex.entity.SimplePair;
import ru.java.mentor.storeinvertedindex.entity.ReverseIndexPair;
import javax.annotation.PostConstruct;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@Component
public class MongoInteract {

    @Value("${mongodb.host}")
    private String mongodbHost;

    @Value("${mongodb.port}")
    private Integer mongodbPort;

    @Value("${mongodb.db}")
    private String mongodbDatabase;

    private MongoClient connection;
    private MongoOperations mongoOps;


    @PostConstruct
    public void init() {
        connection = new MongoClient(mongodbHost, mongodbPort);
        mongoOps = new MongoTemplate(new SimpleMongoDbFactory(connection, mongodbDatabase));
    }

    public Integer addReverseIndexMany(List<SimplePair> simplePairs) {
        return simplePairs
                .stream()
                .map(this::addReverseIndexElement)
                .collect(Collectors.toList()).size();
    }


    public Integer addReverseIndexElement(SimplePair simplePair) {
        if (isWordNew(simplePair.getWord())) {
            mongoOps.insert(new ReverseIndexPair(simplePair));
        } else {
            Update update = new Update().push("documentIds", simplePair.getDocumentId());
            mongoOps
                    .updateFirst(query(where("word").is(simplePair.getWord())),
                            update, ReverseIndexPair.class);
        }
        return simplePair.getDocumentId();
    }

    private boolean isWordNew(String word) {
        return mongoOps.find(query(where("word").is(word)), ReverseIndexPair.class).isEmpty();

    }

    public SimplePair findIndexElement(String word) {
        return mongoOps.findOne(query(where("word").is(word)), SimplePair.class);
    }

}
