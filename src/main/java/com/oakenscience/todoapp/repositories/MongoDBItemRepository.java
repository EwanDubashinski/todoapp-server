package com.oakenscience.todoapp.repositories;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Updates;
import com.oakenscience.todoapp.config.IAuthenticationFacade;
import com.oakenscience.todoapp.models.Item;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Filters.*;

@Repository
public class MongoDBItemRepository implements ItemRepository {

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private IAuthenticationFacade auth;

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
        return itemCollection.find(auth.forCurrentUser()).into(new ArrayList<>());
    }

    @Override
    public Item findOne(Long id) {
        Bson filter = auth.forCurrentUser(eq("id", id));
        return itemCollection.find(filter).first();
    }

    @Override
    public List<Item> findByProjectID(Long projectId) {
        Bson filter = auth.forCurrentUser(eq("project_id", projectId));
        return itemCollection.find(filter).into(new ArrayList<>());
    }

    private List<Long> getChildrenIds(Long id) {
        Bson filter = auth.forCurrentUser(eq("id", id));
        Bson filterByParent = auth.forCurrentUser(eq("parent_id", id));

        List<Long> ids = new ArrayList<>();
        Item item = itemCollection.find(filter).first();
        if (item != null) {
            ids.add(id);
            itemCollection.find(filterByParent).forEach(i -> {
                ids.addAll(getChildrenIds(i.getId()));
            });
        }
        return ids;
    }


    @Override
    public void markAsDone(Long id) {
        Bson update = Updates.set("date_completed", LocalDateTime.now());
        Bson filter = auth.forCurrentUser(and(
                eq("date_completed", null),
                in("id", getChildrenIds(id))
        ));
        itemCollection.updateMany(filter, update);
    }
    @Override
    public void markAsNotDone(Long id) {
        Bson update = Updates.set("date_completed", null);
        Bson filter = auth.forCurrentUser(eq("id", id));

        Item item = itemCollection.find(filter).first();
        if (item != null) {
            LocalDateTime modifiedDate = item.getDateCompleted();
            Bson filterForUpdate = auth.forCurrentUser(and(
                    eq("date_completed", null),
                    in("id", getChildrenIds(id))
            ));
            itemCollection.updateMany(filterForUpdate, update);
        }
    }

    @Override
    public Item updateItemText(Item item) {
        Bson update = Updates.set("content", item.getContent());
        Bson filter = auth.forCurrentUser(eq("id", item.getId()));
        itemCollection.findOneAndUpdate(filter, update);
        return itemCollection.find(filter).first();
    }

    @Override
    public Item createNew(Item item) {
        Long userId = auth.getCurrentUserId();
        if (userId == null) throw new RuntimeException(); // TODO
        item.setUserId(userId);
        item.setId(configRepository.getNextItemId());
        itemCollection.insertOne(item);
        return item;
    }

    @Override
    public void delete(Item item) {
        Bson filter = auth.forCurrentUser(eq("id", item.getId()));
        itemCollection.deleteOne(filter);
    }

    @Override
    public void setChildOrder(Item item, Integer childOrder) {
//        Bson oldQuery = and(
//                eq("child_order", childOrder),
//                eq("project_id", item.getProjectId()),
//                eq("parent_id", item.getParentId())
//        );
//
//        Item oldItem = itemCollection.find(oldQuery).first();
//        if (oldItem == null) return;
//
//        Integer oldOrder = item.getChildOrder();
//
//        Bson updateNew = Updates.set("child_order", childOrder);
//        Bson updateOld = Updates.set("child_order", oldOrder);
//
//        oldQuery = eq("id", oldItem.getId());
//        Bson query = eq("id", item.getId());
//
//        itemCollection.findOneAndUpdate(query, updateNew);
//        itemCollection.findOneAndUpdate(oldQuery, updateOld);

        Bson listQuery = auth.forCurrentUser(and(
                eq("project_id", item.getProjectId()),
                eq("parent_id", item.getParentId())
        ));
        LinkedList<Item> items = itemCollection
                .find(listQuery)
                .sort(new BasicDBObject("child_order", 1))
                .into(new LinkedList<>());

        Item movingItem = items
                .stream()
                .filter(i -> Objects.equals(i.getId(), item.getId()))
                .findFirst()
                .orElse(null);
//        Item movingItem = items.remove((int)item.getChildOrder());
//        items.add(childOrder, item);
        int movingItemIndex = items.indexOf(movingItem);
        if (childOrder != movingItemIndex) {
            items.remove(movingItemIndex);
            items.add(childOrder, movingItem);
        }

        for (int i = 0; i < items.size(); i++) {
            Item listItem = items.get(i);
            if (listItem.getChildOrder() != i) {
                Bson query = auth.forCurrentUser(eq("id", listItem.getId()));
                Bson update = Updates.set("child_order", i);
                itemCollection.findOneAndUpdate(query, update);
            }
        }
    }

    @Override
    public void resetOrder(Long projectId) {
        List<Item> items = findByProjectID(projectId);

        Map<Long, List<Item>> itemsByParentId = new HashMap<>();

        for (Item item : items) {
            itemsByParentId.computeIfAbsent(item.getParentId(), k -> new ArrayList<>()).add(item);
        }

        itemsByParentId.forEach((parentId, itemsGroup) -> {
            itemsGroup.sort(Comparator.comparing(Item::getChildOrder));
            for (int i = 0; i < itemsGroup.size(); i++) {
                Item item = itemsGroup.get(i);
                Bson query = auth.forCurrentUser(eq("id", item.getId()));
                Bson update = Updates.set("child_order", i);
                itemCollection.findOneAndUpdate(query, update);
            }
        });
    }

    @Override
    public Item getItemAbove(Item item) {
        Bson itemAboveQuery = auth.forCurrentUser(and(
                lt("child_order", item.getChildOrder()),
                eq("project_id", item.getProjectId()),
                eq("parent_id", item.getParentId()),
                eq("date_completed", null)
        ));
        return itemCollection
                .find(itemAboveQuery)
                .sort(eq("child_order", -1))
                .first();
    }

    @Override
    public Item getItemBelow(Item item) {
        Bson itemAboveQuery = auth.forCurrentUser(and(
                gt("child_order", item.getChildOrder()),
                eq("project_id", item.getProjectId()),
                eq("parent_id", item.getParentId()),
                eq("date_completed", null)
        ));
        return itemCollection.find(itemAboveQuery).first();
    }

    @Override
    public Item setParent(Item item, Item parent) {
        Bson query = auth.forCurrentUser(eq("id", item.getId()));
        Long parentId = null;
        if (parent != null) parentId = parent.getId();
        Bson update = Updates.set("parent_id", parentId);
        itemCollection.findOneAndUpdate(query, update);
        return itemCollection.find(query).first();
    }

    @Override
    public Item getParent(Item item) {
        Item parent = null;
        if (item.getParentId() != null) {
            Bson filter = auth.forCurrentUser(eq("id", item.getParentId()));
            parent = itemCollection.find(filter).first();
        }
        return parent;
    }

    @Override
    public Integer getNextChildOrder(Long projectId, Long parentId) {
        Bson query = auth.forCurrentUser(and(
                eq("parent_id", parentId),
                eq("project_id", projectId)
        ));
        List<Item> items = itemCollection.find(query).into(new ArrayList<>()); // TODO: for debug reasons only
        return (int)itemCollection.countDocuments(query);
    }
}
