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

    private Config getOrCreate() {
        Config config = configCollection.find().first();
        if (config == null) {
            config = new Config();
            config.setLastItemId(0L);
            config.setLastProjectId(0L);
            config.setLastUserId(0L);
            configCollection.insertOne(config);
        };
        return config;
    }
    @Override
    public Long getNextItemId() {
        Config config = getOrCreate();
        Long nextItemId = config.getLastItemId() + 1;
        Bson update = Updates.set("last_item_id", nextItemId);
        configCollection.updateOne(new Document(), update);
        return nextItemId;
    }

    @Override
    public Long getNextProjectId() {
        Config config = getOrCreate();
        Long nextProjectId = config.getLastProjectId() + 1;
        Bson update = Updates.set("last_project_id", nextProjectId);
        configCollection.updateOne(new Document(), update);
        return nextProjectId;
    }

    @Override
    public Long getNextUserId() {
        Config config = getOrCreate();
        Long nextUserId = config.getLastUserId() + 1;
        Bson update = Updates.set("last_user_id", nextUserId);
        configCollection.updateOne(new Document(), update);
        return nextUserId;
    }
}
