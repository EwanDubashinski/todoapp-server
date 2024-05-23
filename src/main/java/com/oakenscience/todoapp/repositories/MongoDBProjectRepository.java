package com.oakenscience.todoapp.repositories;

import com.mongodb.*;
import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.client.model.Updates;
import com.mongodb.client.model.WriteModel;
import com.oakenscience.todoapp.config.IAuthenticationFacade;
import com.oakenscience.todoapp.models.Project;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

import static com.mongodb.client.model.Aggregates.group;
import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Filters.gt;

@Repository
public class MongoDBProjectRepository implements ProjectRepository {
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
        return projectCollection.find(
                auth.forCurrentUser(ne("is_archived", 1))
        ).into(new ArrayList<>());
    }

    @Override
    public Project createNew(Project project) {
        Long projectId = configRepository.getNextProjectId();
        project.setId(projectId);
        project.setIsArchived(0);
        project.setChildOrder(getNextChildOrder(null));
        Long userId = auth.getCurrentUserId();
        if (userId == null) throw new RuntimeException(); // TODO
        project.setUserId(userId);
        projectCollection.insertOne(project);
        return projectCollection.find(eq("id", projectId)).first();
    }

    @Override
    public void setCollapsed(Project project) {
        Bson filter = auth.forCurrentUser(eq("id", project.getId()));
        Bson update = Updates.set("collapsed", project.getCollapsed());
        projectCollection.findOneAndUpdate(filter, update);
    }

    @Override
    public Project update(Project project) {
        Bson update = Updates.combine(
                Updates.set("name", project.getName()),
                Updates.set("child_order", project.getChildOrder())
        );
        Bson filter = auth.forCurrentUser(eq("id", project.getId()));
        projectCollection.findOneAndUpdate(filter, update);
        return projectCollection.find(filter).first();
    }
    @Override
    public List<Project> updateMany(List<Project> projects) {
        List<WriteModel<Project>> bulkWrites = new ArrayList<>();

        for (Project project : projects) {
            // Construct the filter to identify the document to be updated
            Bson filter = Filters.eq("id", project.getId());

            // Construct the update operation for each project
            Bson update = Updates.combine(
//                    Updates.set("name", project.getName()),
                    Updates.set("child_order", project.getChildOrder()),
                    Updates.set("parent_id", project.getParentId())
            );

            // Create an UpdateOneModel for the update operation
            UpdateOneModel<Project> updateModel = new UpdateOneModel<>(filter, update);

            // Add the update model to the list of bulk write operations
            bulkWrites.add(updateModel);
        }

        // Execute the bulk write operation
        BulkWriteResult bulkWriteResult = projectCollection.bulkWrite(bulkWrites);
        List<Long> ids = projects
                .stream()
                .map(Project::getId)
                .collect(Collectors.toList());
        Bson filter = Filters.in("id", ids);
        FindIterable<Project> updatedProjects = projectCollection.find(filter);
        List<Project> updatedProjectsList = new ArrayList<>();
        for (Project updatedProject: updatedProjects) {
            updatedProjectsList.add(updatedProject);
        }
//        updatedProjects.iterator().forEachRemaining(updatedProjectsList::add);
        // Return the list of updated projects
        return updatedProjectsList;
    }

    @Override
    public Long delete(Project project) {
        Long projectId = project.getId();
        Bson filter = auth.forCurrentUser(eq("id", projectId));
        projectCollection.deleteOne(filter);
        return projectId;
    }

    @Override
    public Project getProjectAbove(Project project) {
        Bson itemAboveQuery = auth.forCurrentUser(and(
                lt("child_order", project.getChildOrder()),
                eq("parent_id", project.getParentId()),
                eq("is_archived", 0)
        ));
        return projectCollection
                .find(itemAboveQuery)
                .sort(eq("child_order", -1))
                .first();
    }

    @Override
    public Project getProjectBelow(Project project) {
        Bson itemAboveQuery = auth.forCurrentUser(and(
                gt("child_order", project.getChildOrder()),
                eq("parent_id", project.getParentId()),
                eq("is_archived", 0)
        ));
        return projectCollection.find(itemAboveQuery).first();
    }

    @Override
    public void setChildOrder(Project project, Integer childOrder) {
        Bson listQuery = auth.forCurrentUser(eq("parent_id", project.getParentId()));
        LinkedList<Project> projects = projectCollection
                .find(listQuery)
                .sort(new BasicDBObject("child_order", 1))
                .into(new LinkedList<>());

        Project movingProject = projects
                .stream()
                .filter(p -> Objects.equals(p.getId(), project.getId()))
                .findFirst()
                .orElse(null);

        int movingProjectIndex = projects.indexOf(movingProject);
        if (childOrder != movingProjectIndex) {
            projects.remove(movingProjectIndex);
            projects.add(childOrder, movingProject);
        }

        for (int i = 0; i < projects.size(); i++) {
            Project listProject = projects.get(i);
            if (listProject.getChildOrder() != i) {
                Bson query = auth.forCurrentUser(eq("id", listProject.getId()));
                Bson update = Updates.set("child_order", i);
                projectCollection.findOneAndUpdate(query, update);
            }
        }
    }
    @Override
    public void resetOrder() {
        List<Project> projects = findAll();

        Map<Long, List<Project>> projectsByParentId = new HashMap<>();

        for (Project project : projects) {
            projectsByParentId.computeIfAbsent(project.getParentId(), k -> new ArrayList<>()).add(project);
        }

        projectsByParentId.forEach((parentId, projectsGroup) -> {
            projectsGroup.sort(Comparator.comparing(Project::getChildOrder, Comparator.nullsLast(Comparator.naturalOrder())));
            for (int i = 0; i < projectsGroup.size(); i++) {
                Project project = projectsGroup.get(i);
                Bson query = auth.forCurrentUser(eq("id", project.getId()));
                Bson update = Updates.set("child_order", i);
                projectCollection.findOneAndUpdate(query, update);
            }
        });
    }

    @Override
    public Integer getNextChildOrder(Long parentId) {
        Bson query = auth.forCurrentUser(eq("parent_id", parentId));
//        List<Project> projects = projectCollection.find(query).into(new ArrayList<>()); // TODO: for debug reasons only
        return (int)projectCollection.countDocuments(query) + 1;
    }
    @Override
    public Project setParent(Project project, Project parent) {
        Bson query = auth.forCurrentUser(eq("id", project.getId()));
        Long parentId = null;
        if (parent != null) parentId = parent.getId();
        Bson update = Updates.set("parent_id", parentId);
        projectCollection.findOneAndUpdate(query, update);
        return projectCollection.find(query).first();
    }

    @Override
    public Project getParent(Project project) {
        Project parent = null;
        if (project.getParentId() != null) {
            Bson filter = auth.forCurrentUser(eq("id", project.getParentId()));
            parent = projectCollection.find(filter).first();
        }
        return parent;
    }
}
