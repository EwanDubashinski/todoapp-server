package com.oakenscience.todoapp.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.bson.codecs.pojo.annotations.BsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Config {
    @BsonProperty("last_item_id")
    private Long lastItemId;

    @BsonProperty("last_project_id")
    private Long lastProjectId;
    @BsonProperty("last_user_id")
    private Long lastUserId;

    public Long getLastItemId() {
        return lastItemId;
    }

    public void setLastItemId(Long lastItemId) {
        this.lastItemId = lastItemId;
    }

    public Long getLastProjectId() {
        return lastProjectId;
    }

    public void setLastProjectId(Long lastProjectId) {
        this.lastProjectId = lastProjectId;
    }

    public Long getLastUserId() {
        return lastUserId;
    }

    public void setLastUserId(Long lastUserId) {
        this.lastUserId = lastUserId;
    }
}
