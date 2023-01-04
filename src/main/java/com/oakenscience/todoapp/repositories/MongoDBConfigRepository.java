package com.oakenscience.todoapp.repositories;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.oakenscience.todoapp.models.Config;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class MongoDBConfigRepository implements ConfigRepository{
    private final MongoClient client;
    private MongoCollection<Config> configCollection;

    public MongoDBConfigRepository(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    @PostConstruct
    void init() {
        configCollection = client.getDatabase("todoapp").getCollection("config", Config.class);
    }

    @Override
    public Long getNextItemId() {
        Config config = configCollection.find().first();
        Long nextItemId = config.getLastItemId() + 1;
        Bson update = Updates.set("last_item_id", nextItemId);
        configCollection.updateOne(new Document(), update);
        return nextItemId;
    }

    @Override
    public Long getNextProjectId() {
        return null;
    }
}
