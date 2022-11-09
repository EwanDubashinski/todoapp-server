package com.mongodb.starter.repositories;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.starter.models.Item;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;

@Repository
public class MongoDBItemRepository implements ItemRepository {

    @Autowired
    private MongoDBConfigRepository configRepository;

    private static final TransactionOptions txnOptions = TransactionOptions.builder()
                                                                           .readPreference(ReadPreference.primary())
                                                                           .readConcern(ReadConcern.MAJORITY)
                                                                           .writeConcern(WriteConcern.MAJORITY)
                                                                           .build();
    private final MongoClient client;
    private MongoCollection<Item> itemCollection;

    public MongoDBItemRepository(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    @PostConstruct
    void init() {
        itemCollection = client.getDatabase("todoapp").getCollection("items_full", Item.class);
    }

    @Override
    public List<Item> findAll() {
        return itemCollection.find().into(new ArrayList<>());
    }

    @Override
    public Item findOne(Long id) {
//        BasicDBObject query = new BasicDBObject("id", id);
        return itemCollection.find(eq("id", id)).first();
    }

    @Override
    public List<Item> findByProjectID(Long projectId) {
        return itemCollection.find(eq("project_id", projectId)).into(new ArrayList<>());
    }

    private List<Long> getChildrenIds(Long id) {
        List<Long> ids = new ArrayList<>();
        Item item = itemCollection.find(eq("id", id)).first();
        if (item != null) {
            ids.add(id);
            itemCollection.find(eq("parent_id", id)).forEach(i -> {
//                ids.add(i.getId());
                ids.addAll(getChildrenIds(i.getId()));
            });
//            if (item.getParentId() != null)
        }
        return ids;
    }


    @Override
    public void markAsDone(Long id) {
        Bson update = Updates.set("date_completed", LocalDateTime.now());
//        itemCollection.findOneAndUpdate(eq("id", id), update);
//        Document query = new Document()
//                .append("parent_id",  id)
//                .append("date_completed", null);
        Bson filter = and(eq("date_completed", null), in("id", getChildrenIds(id)));
        itemCollection.updateMany(filter, update);
    }
    @Override
    public void markAsNotDone(Long id) {
        Bson update = Updates.set("date_completed", null);
        LocalDateTime modifiedDate = Objects.requireNonNull(
                itemCollection.find(eq("id", id)).first())
                .getDateCompleted();

//        itemCollection.findOneAndUpdate(eq("id", id), update);
//        Document query = new Document()
//                .append("parent_id",  id)
//                .append("date_completed", modifiedDate);
        Bson filter = and(eq("date_completed", modifiedDate), in("id", getChildrenIds(id)));
        itemCollection.updateMany(filter, update);
    }

    @Override
    public Item updateItemText(Item item) {
        Bson update = Updates.set("content", item.getContent());
        Bson query = eq("id", item.getId());
        itemCollection.findOneAndUpdate(query, update);
        return itemCollection.find(query).first();
    }

    @Override
    public Item createNew(Item item) {
//        Item item = new Item(configRepository.getNextItemId(), item);
        item.setId(configRepository.getNextItemId());
        itemCollection.insertOne(item);
        return item;
    }

    private List<ObjectId> mapToObjectIds(List<String> ids) {
        return ids.stream().map(ObjectId::new).collect(Collectors.toList());
    }

    @Override
    public void delete(Item item) {
        Bson query = eq("id", item.getId());
        itemCollection.deleteOne(query);
    }
}
