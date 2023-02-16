package com.oakenscience.todoapp.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.codecs.pojo.annotations.BsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Project {

//    @JsonSerialize(using = ToStringSerializer.class)
//    private ObjectId id;
    @BsonProperty("id")
    private Long id;
    private String name;
    @BsonProperty("parent_id")
    private Long parentId;
    @BsonProperty("user_id")
    private Long userId;
    @BsonProperty("child_order")
    private Integer childOrder;
    @BsonProperty("is_archived")
    private Integer isArchived;

    private Integer collapsed;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getCollapsed() {
        return collapsed;
    }

    public void setCollapsed(Integer collapsed) {
        this.collapsed = collapsed;
    }

    public Integer getChildOrder() {
        return childOrder;
    }

    public void setChildOrder(Integer childOrder) {
        this.childOrder = childOrder;
    }

    public Integer getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Integer isArchived) {
        this.isArchived = isArchived;
    }
}
