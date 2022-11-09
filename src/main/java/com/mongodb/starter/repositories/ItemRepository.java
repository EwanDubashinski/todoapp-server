package com.mongodb.starter.repositories;

import com.mongodb.starter.models.Item;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository {
    List<Item> findAll();

    List<Item> findByProjectID(Long projectId);

    Item findOne(Long id);

    void markAsDone(Long id);

    Item updateItemText(Item item);

    void markAsNotDone(Long id);

    Item createNew(Item item);

    void delete(Item item);
}
