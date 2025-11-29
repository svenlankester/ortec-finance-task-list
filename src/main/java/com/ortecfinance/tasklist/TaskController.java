package com.ortecfinance.tasklist;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
public class TaskController {

    private final TaskManager taskManager = new TaskManager();

    @GetMapping
    public Map<String, List<Task>> getProjects() {
        return taskManager.getTasks();
    }

    @PostMapping
    public String addProject(@RequestBody String name) {
        taskManager.addProject(name);
        return "Project added: " + name;
    }

    @PostMapping("/{projectName}/tasks")
    public String getProject(@PathVariable String projectName, @RequestBody String taskName) {
        if (!taskManager.addTask(projectName, taskName)) {
            return "Project with name " + projectName + " not found.";
        }
        return "Task added: " + taskName;
    }

    @PutMapping("/{projectName}/tasks/{taskId}")
    public String updateTaskDeadline(@PathVariable String projectName, @PathVariable String taskId, @RequestParam String deadline) {
        if (!taskManager.addDeadLine(Integer.parseInt(taskId), deadline)) {
            return "Task with ID " + taskId +  " not found.";
        }
        return "Deadline updated for task " + taskId + " in project " + projectName;
    }

}
