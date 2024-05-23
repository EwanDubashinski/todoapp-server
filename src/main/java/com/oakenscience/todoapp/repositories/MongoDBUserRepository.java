package com.oakenscience.todoapp.repositories;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.oakenscience.todoapp.models.DbUser;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class MongoDBUserRepository implements UserRepository {

    @Autowired
    private ConfigRepository configRepository;
    private static final TransactionOptions txnOptions = TransactionOptions.builder()
            .readPreference(ReadPreference.primary())
            .readConcern(ReadConcern.MAJORITY)
            .writeConcern(WriteConcern.MAJORITY)
            .build();
    private final MongoClient client;
    private MongoCollection<DbUser> userCollection;

    public MongoDBUserRepository(MongoClient mongoClient) {
        this.client = mongoClient;
    }
    @PostConstruct
    void init() {
        userCollection = client.getDatabase("todoapp").getCollection("users", DbUser.class);
    }
    @Override
    public DbUser createNew(DbUser dbUser) {
        dbUser.setId(configRepository.getNextUserId());
        userCollection.insertOne(dbUser);
        return dbUser;
    }

    @Override
    public DbUser findByEmail(String email) {
        return userCollection.find(eq("email", email.toLowerCase())).first();
    }

    @Override
    public DbUser updateUserToken(DbUser dbUser) {
        Bson update = Updates.set("token", dbUser.getToken());
        Bson query = eq("email", dbUser.getEmail());
        userCollection.findOneAndUpdate(query, update);
        return userCollection.find(query).first();
    }

    @Override
    public DbUser findByActivationCode(String code) {
        return userCollection.find(eq("token.token", code)).first();
    }

    @Override
    public DbUser enable(DbUser dbUser) {
        Bson update = Updates.set("activated", true);
        Bson query = eq("email", dbUser.getEmail());
        userCollection.findOneAndUpdate(query, update);
        return userCollection.find(query).first();
    }

    @Override
    public DbUser clearUserTokens(DbUser dbUser) {
        Bson update = Updates.set("token", null);
        Bson query = eq("email", dbUser.getEmail());
        userCollection.findOneAndUpdate(query, update);
        return userCollection.find(query).first();
    }
}
