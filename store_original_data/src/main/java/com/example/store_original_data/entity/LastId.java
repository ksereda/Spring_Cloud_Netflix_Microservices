package com.example.store_original_data.entity;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "lastid")
public class LastId {

    private Integer lastid;

}
