package com.ortecfinance.tasklist;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Date;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;

public final class TaskListCLI implements Runnable {
    private static final String QUIT = "quit";

    private final TaskManager taskManager = new TaskManager();
    private final BufferedReader in;
    private final PrintWriter out;

    public static void startConsole() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        PrintWriter out = new PrintWriter(System.out);
        new TaskListCLI(in, out).run();
    }

    public TaskListCLI(BufferedReader reader, PrintWriter writer) {
        this.in = reader;
        this.out = writer;
    }

    public void run() {
        out.println("Welcome to TaskList! Type 'help' for available commands.");
        while (true) {
            out.print("> ");
            out.flush();
            String command;
            try {
                command = in.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (command.equals(QUIT)) {
                break;
            }
            execute(command);
        }
    }

    private void execute(String commandLine) {
        String[] commandRest = commandLine.split(" ", 2);
        String command = commandRest[0];
        switch (command) {
            case "show":
                show(false);
                break;
            case "today":
                show(true);
                break;
            case "view-by-deadline":
                viewByDeadline();
                break;
            case "add":
                processAddCommand(commandRest[1]);
                break;
            case "check":
                taskManager.markTaskDone(Integer.parseInt(commandRest[1]), true);
                break;
            case "uncheck":
                taskManager.markTaskDone(Integer.parseInt(commandRest[1]), false);
                break;
            case "deadline":
                String[] subcommandRest = commandRest[1].split(" ", 2);
                taskManager.addDeadLine(Integer.parseInt(subcommandRest[0]), subcommandRest[1]);
                break;
            case "help":
                help();
                break;
            default:
                error(command);
                break;
        }
    }


    private void viewByDeadline() {
        // make a deadline -> tasklist map
        Map<String, List<Task>> deadlines = new LinkedHashMap<>();
        Map<String, List<Task>> tasks = taskManager.getTasks();

        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            for (Task task : project.getValue()) {
                String deadline = task.getDeadline();
                if (deadline.equals("")) {
                    deadline = "No deadline";
                }
                deadlines.computeIfAbsent(deadline, (k) -> new ArrayList<>()).add(task);
            }
        }

        // sort list of keys
        List<String> dates = new ArrayList<String>(deadlines.keySet());
        // temporarily remove no deadline to sort by date & simultaneously checking if it existed in the first place
        boolean hadNoDeadlineEntry = dates.remove("No deadline");
        DateTimeFormatter format = DateTimeFormatter.ofPattern("d-M-yyyy");
        dates.sort(Comparator.comparing(entry -> LocalDate.parse(entry, format)));
        if (hadNoDeadlineEntry) {
            dates.add("No deadline");
        }

        for (String deadline : dates) {
            out.println(deadline + ":");
            for (Task task : deadlines.get(deadline)) {
                out.printf("    [%c] %d: %s%n", (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription());
            }
        }
        out.println();
    }

    private void show(boolean showOnlyToday) {
        Map<String, List<Task>> tasks = taskManager.getTasks();

        // get current date
        Format formatter = new SimpleDateFormat("d-M-yyyy");
        String date = formatter.format(new Date());

        for (Map.Entry<String, List<Task>> project : tasks.entrySet()) {
            out.println(project.getKey());
            for (Task task : project.getValue()) {
                if (!showOnlyToday || task.getDeadline().equals(date))
                out.printf("    [%c] %d: %s%n", (task.isDone() ? 'x' : ' '), task.getId(), task.getDescription());
            }
            out.println();
        }
    }
    
    private void processAddCommand(String commandLine) {
        String[] subcommandRest = commandLine.split(" ", 2);
        String subcommand = subcommandRest[0];
        if (subcommand.equals("project")) {
            taskManager.addProject(subcommandRest[1]);
        } else if (subcommand.equals("task")) {
            String[] projectTask = subcommandRest[1].split(" ", 2);
            // TODO add input error checking to avoid program crashing over minor error
            taskManager.addTask(projectTask[0], projectTask[1]);
        }
    }
    
    private void help() {
        out.println("Commands:");
        out.println("  show");
        out.println("  add project <project name>");
        out.println("  add task <project name> <task description>");
        out.println("  check <task ID>");
        out.println("  uncheck <task ID>");
        out.println();
    }

    private void error(String command) {
        out.printf("I don't know what the command \"%s\" is.", command);
        out.println();
    }

}
