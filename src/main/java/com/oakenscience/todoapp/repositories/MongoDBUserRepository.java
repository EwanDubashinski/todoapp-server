package com.oakenscience.todoapp.repositories;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.oakenscience.todoapp.models.User;
import com.oakenscience.todoapp.models.VerificationToken;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

import static com.mongodb.client.model.Filters.eq;

@Repository
public class MongoDBUserRepository implements UserRepository {

    private static final TransactionOptions txnOptions = TransactionOptions.builder()
            .readPreference(ReadPreference.primary())
            .readConcern(ReadConcern.MAJORITY)
            .writeConcern(WriteConcern.MAJORITY)
            .build();
    private final MongoClient client;
    private MongoCollection<User> userCollection;

    public MongoDBUserRepository(MongoClient mongoClient) {
        this.client = mongoClient;
    }
    @PostConstruct
    void init() {
        userCollection = client.getDatabase("todoapp").getCollection("users", User.class);
    }
    @Override
    public User createNew(User user) {
        userCollection.insertOne(user);
        return user;
    }

    @Override
    public User findByEmail(String email) {
        return userCollection.find(eq("email", email.toLowerCase())).first();
    }

    @Override
    public User updateUserToken(User user) {
        Bson update = Updates.set("token", user.getToken());
        Bson query = eq("email", user.getEmail());
        userCollection.findOneAndUpdate(query, update);
        return userCollection.find(query).first();
    }
}
