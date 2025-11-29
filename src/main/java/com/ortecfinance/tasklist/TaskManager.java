package com.ortecfinance.tasklist;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final Map<String, List<Task>> tasks = new LinkedHashMap<>();
    private long lastId = 0;
    
    private Task getTaskByID(long id) {
        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            for (Task task : project.getValue()) {
                if (task.getId() == id) {
                    return task;
                }
            }
        }
        return null;
    }

    public void addProject(String name) {
        tasks.put(name, new ArrayList<Task>());
    }

    public void addTask(String project, String description) {
        List<Task> projectTasks = tasks.get(project);
        if (projectTasks == null) {
            // TODO: add error messaging
            return;
        }
        projectTasks.add(new Task(nextId(), description, false));
    }

    public void addDeadLine(long id, String deadline) {
        
        Task task = getTaskByID(id);
        if (task != null){
            task.setDeadline(deadline);
            return;
        }
        // TODO: add error messaging
    }

    public void markTaskDone(long id, boolean done) {
        Task task = getTaskByID(id);
        if (task != null){
            task.setDone(done);
            return;
        }
        
    }

    // can improve in future by splitting this up to get task by project/deadline (introduces some complications)
    public Map<String, List<Task>> getTasks() {
        return tasks;
    }

    private long nextId() {
        return ++lastId;
    }
}
