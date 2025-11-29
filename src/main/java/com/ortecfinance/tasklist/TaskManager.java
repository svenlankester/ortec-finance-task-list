package com.ortecfinance.tasklist;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TaskManager {
    private final Map<String, List<Task>> tasks = new LinkedHashMap<>();
    private int lastId = 0;
    
    private Task getTaskByID(String idString) {
        int id = Integer.parseInt(idString);
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

    public void addDeadLine(String commandLine) {
        String[] subcommandRest = commandLine.split(" ", 2);

        String id = subcommandRest[0];
        String deadline = subcommandRest[1];

        Task task = getTaskByID(id);
        if (task != null){
            task.setDeadline(deadline);
            return;
        }
        // TODO: add error messaging
    }

    public void markTaskDone(String idString, boolean done) {
        Task task = getTaskByID(idString);
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
