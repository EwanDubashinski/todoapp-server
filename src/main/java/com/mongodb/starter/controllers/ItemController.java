package com.mongodb.starter.controllers;

import com.mongodb.starter.models.Item;
import com.mongodb.starter.repositories.ItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

import static java.util.Arrays.asList;
@CrossOrigin
@RestController
@RequestMapping("/api")
public class ItemController {

    private final static Logger LOGGER = LoggerFactory.getLogger(ItemController.class);
    private final ItemRepository itemRepository;

    public ItemController(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @GetMapping("items")
    public List<Item> getItems() {
        return itemRepository.findAll();
    }

    @GetMapping("item/{id}")
    public ResponseEntity<Item> getItem(@PathVariable Long id) {
        Item item = itemRepository.findOne(id);
        if (item == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        return ResponseEntity.ok(item);
    }

    @GetMapping("items/{projectId}")
    public List<Item> getItemsByProjectId(@PathVariable Long projectId) {
        itemRepository.resetOrder(projectId);
        List<Item> items = itemRepository.findByProjectID(projectId);
        items.sort(Comparator.comparing(Item::getChildOrder));
        return items;
    }

    @PostMapping("item/done")
    public void markAsDone(@RequestBody Item item) {
        itemRepository.markAsDone(item.getId());
    }

    @PostMapping("item/undone")
    public void markAsNotDone(@RequestBody Item item) {
        itemRepository.markAsNotDone(item.getId());
    }

    @PostMapping(value = "item/update")
    public Item updateText(@RequestBody Item item) {
        return itemRepository.updateItemText(item);
    }

    @PostMapping(value = "item/create")
    @ResponseStatus(HttpStatus.CREATED)
    public Item createNew(@RequestBody Item item) {
        return itemRepository.createNew(item);
    }

    @PostMapping(value = "item/delete")
    public void delete(@RequestBody Item item) {
        itemRepository.delete(item);
    }
    @PostMapping(value = "item/up")
    public void up(@RequestBody Item item) {
        itemRepository.setChildOrder(item, item.getChildOrder() - 1);
    }
    @PostMapping(value = "item/down")
    public void down(@RequestBody Item item) {
        itemRepository.setChildOrder(item, item.getChildOrder() + 1);
    }
    @PostMapping(value = "item/right")
    public void right(@RequestBody Item item) {
        Item itemAbove = itemRepository.getItemAbove(item);
        if (itemAbove != null) {
            Integer order = itemRepository.getNextChildOrder(item.getProjectId(), itemAbove.getId());
            item = itemRepository.setParent(item, itemAbove);
            itemRepository.setChildOrder(item, order);
        };
    }
    @PostMapping(value = "item/left")
    public void left(@RequestBody Item item) {
        if (item.getParentId() == null) return;

        Item parent = itemRepository.getParent(item);
        Item grandParent = itemRepository.getParent(parent);
        Long grandParentId = grandParent == null ? null : grandParent.getId();
//        Integer order = itemRepository.getNextChildOrder(item.getProjectId(), grandParentId);
        Integer order = parent.getChildOrder() + 1;
        item = itemRepository.setParent(item, grandParent);
        itemRepository.setChildOrder(item, order);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public final Exception handleAllExceptions(RuntimeException e) {
        LOGGER.error("Internal server error.", e);
        return e;
    }
}
