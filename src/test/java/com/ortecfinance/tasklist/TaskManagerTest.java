package com.ortecfinance.tasklist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = new TaskManager();
    }

    @Test
    void add_project() {
        taskManager.addProject("project");
        Map<String, List<Task>> tasks = taskManager.getTasks();

        assertTrue(tasks.containsKey("project"));
        assertEquals(0, tasks.get("project").size());
    }

    @Test
    void add_task() {
        taskManager.addProject("project");
        taskManager.addTask("project", "task");

        List<Task> tasks = taskManager.getTasks().get("project");
        assertEquals(1, tasks.size());

        Task task = tasks.get(0);
        assertEquals("task", task.getDescription());
        assertFalse(task.isDone());
        assertTrue(task.getId() > 0);
    }

    @Test
    void add_task_to_nonexistent_project_fails() {
        taskManager.addTask("nonexistent", "task");

        assertFalse(taskManager.getTasks().containsKey("nonexistent"));
    }

    @Test
    void mark_task_done() {
        taskManager.addProject("project");
        taskManager.addTask("project", "task");

        Task task = taskManager.getTasks().get("project").get(0);
        assertFalse(task.isDone());

        taskManager.markTaskDone(task.getId(), true);
        assertTrue(task.isDone());

        taskManager.markTaskDone(task.getId(), false);
        assertFalse(task.isDone());
    }

    @Test
    void add_deadline() {
        taskManager.addProject("project");
        taskManager.addTask("project", "task");

        Task task = taskManager.getTasks().get("project").get(0);
        assertEquals("", task.getDeadline());

        taskManager.addDeadLine(1, "31-12-2025");
        assertEquals("31-12-2025", task.getDeadline());
    }

    @Test
    void get_tasks() {
        taskManager.addProject("project");
        taskManager.addTask("project", "task1");
        taskManager.addTask("project", "task2");

        taskManager.addProject("project2");
        taskManager.addTask("project2", "task3");

        Map<String, List<Task>> tasks = taskManager.getTasks();

        assertEquals(2, tasks.size());
        assertEquals(2, tasks.get("project").size());
        assertEquals(1, tasks.get("project2").size());
    }

    @Test
    void unique_ids() {
        taskManager.addProject("project");
        taskManager.addTask("project", "task1");
        taskManager.addTask("project", "task2");

        List<Task> tasks = taskManager.getTasks().get("project");
        assertEquals(1, tasks.get(0).getId());
        assertEquals(2, tasks.get(1).getId());
    }
}
