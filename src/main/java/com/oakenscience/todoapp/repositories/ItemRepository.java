package com.oakenscience.todoapp.repositories;

import com.oakenscience.todoapp.models.Item;
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

    void setChildOrder(Item item, Integer childOrder);

    void resetOrder(Long projectId);


    Item getItemBelow(Item item);
    Item getItemAbove(Item item);

    Item getParent(Item item);
    Item setParent(Item item, Item parent);
    Integer getNextChildOrder(Long projectId, Long parentId);
}
