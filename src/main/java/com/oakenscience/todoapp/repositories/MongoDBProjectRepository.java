package com.oakenscience.todoapp.repositories;

import com.mongodb.ReadConcern;
import com.mongodb.ReadPreference;
import com.mongodb.TransactionOptions;
import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.oakenscience.todoapp.config.IAuthenticationFacade;
import com.oakenscience.todoapp.models.Project;
import com.oakenscience.todoapp.models.UserInfo;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.in;

@Repository
public class MongoDBProjectRepository implements ProjectRepository {
    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private IAuthenticationFacade authenticationFacade;

    private static final TransactionOptions txnOptions = TransactionOptions.builder()
                                                                           .readPreference(ReadPreference.primary())
                                                                           .readConcern(ReadConcern.MAJORITY)
                                                                           .writeConcern(WriteConcern.MAJORITY)
                                                                           .build();
    private final MongoClient client;
    private MongoCollection<Project> projectCollection;

    public MongoDBProjectRepository(MongoClient mongoClient) {
        this.client = mongoClient;
    }

    @PostConstruct
    void init() {
        projectCollection = client.getDatabase("todoapp").getCollection("projects", Project.class);
    }

    @Override
    public List<Project> findAll() {
        return projectCollection.find().into(new ArrayList<>());
    }

    @Override
    public List<Project> findAll(List<String> ids) {
        return projectCollection.find(in("_id", mapToObjectIds(ids))).into(new ArrayList<>());
    }

    @Override
    public Project findOne(String id) {
        return projectCollection.find(eq("_id", new ObjectId(id))).first();
    }

    private List<ObjectId> mapToObjectIds(List<String> ids) {
        return ids.stream().map(ObjectId::new).collect(Collectors.toList());
    }

    @Override
    public Project createNew(Project project) {
        Long projectId = configRepository.getNextProjectId();
        project.setId(projectId);
        Long userId = authenticationFacade.getCurrentUser().getId();
        if (userId == null) throw new RuntimeException(); // TODO
        project.setUserId(userId);
        projectCollection.insertOne(project);
        return projectCollection.find(eq("id", projectId)).first();
    }
}
