package com.ortecfinance.tasklist;

import org.junit.jupiter.api.*;

import java.io.*;
import java.util.Date;
import java.text.Format;
import java.text.SimpleDateFormat;


import static java.lang.System.lineSeparator;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public final class ApplicationTest {
    public static final String PROMPT = "> ";
    private final PipedOutputStream inStream = new PipedOutputStream();
    private final PrintWriter inWriter = new PrintWriter(inStream, true);

    private final PipedInputStream outStream = new PipedInputStream();
    private final BufferedReader outReader = new BufferedReader(new InputStreamReader(outStream));

    private Thread applicationThread;

    public ApplicationTest() throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(new PipedInputStream(inStream)));
        PrintWriter out = new PrintWriter(new PipedOutputStream(outStream), true);
        TaskList taskList = new TaskList(in, out);
        applicationThread = new Thread(taskList);
    }

    @BeforeEach
    public void start_the_application() throws IOException {
        applicationThread.start();
        readLines("Welcome to TaskList! Type 'help' for available commands.");
    }

    @AfterEach
    public void kill_the_application() throws IOException, InterruptedException {
        if (!stillRunning()) {
            return;
        }

        Thread.sleep(1000);
        if (!stillRunning()) {
            return;
        }

        applicationThread.interrupt();
        throw new IllegalStateException("The application is still running.");
    }

    @Test
    void it_works() throws IOException {
        execute("show");

        execute("add project secrets");
        execute("add task secrets Eat more donuts.");
        execute("add task secrets Destroy all humans.");

        execute("show");
        readLines(
            "secrets",
            "    [ ] 1: Eat more donuts.",
            "    [ ] 2: Destroy all humans.",
            ""
        );

        execute("add project training");
        execute("add task training Four Elements of Simple Design");
        execute("add task training SOLID");
        execute("add task training Coupling and Cohesion");
        execute("add task training Primitive Obsession");
        execute("add task training Outside-In TDD");
        execute("add task training Interaction-Driven Design");

        execute("check 1");
        execute("check 3");
        execute("check 5");
        execute("check 6");

        execute("show");
        readLines(
                "secrets",
                "    [x] 1: Eat more donuts.",
                "    [ ] 2: Destroy all humans.",
                "",
                "training",
                "    [x] 3: Four Elements of Simple Design",
                "    [ ] 4: SOLID",
                "    [x] 5: Coupling and Cohesion",
                "    [x] 6: Primitive Obsession",
                "    [ ] 7: Outside-In TDD",
                "    [ ] 8: Interaction-Driven Design",
                ""
        );

        execute("quit");
    }

    @Test
    void today_command() throws IOException {

        Format formatter = new SimpleDateFormat("dd-MM-yyyy");
        String date = formatter.format(new Date());

        execute("add project todo");
        execute("add task todo fix simple bug");
        execute("add task todo accidentally take down cloudflare");
        execute("add task todo push to prod on friday afternoon");
        
        // task 1 will have no deadline, 2 will have today as deadline and 3 will have unix time date
        String todayCommand = "deadline 2 " + date;
        execute(todayCommand);
        execute("deadline 3 1-1-1970");

        execute("today");
        readLines(
            "todo",
            "    [ ] 2: accidentally take down cloudflare",
            ""
        );

        execute("quit");
    }

    @Test
    void view_by_deadline_command() throws IOException {


        execute("add project todo");
        execute("add task todo fix simple bug");
        execute("add task todo accidentally take down cloudflare");
        execute("add task todo push to prod on friday afternoon");
        execute("add task todo get burnout from amount of tasks");
        
        // task 1 will have no deadline, 2 will have today as deadline and 3 will have unix time date
        execute("deadline 2 21-10-2025");
        execute("deadline 3 1-1-1970");
        execute("deadline 4 1-1-1970");

        execute("view-by-deadline");
        readLines(
            "1-1-1970:",
            "    [ ] 3: push to prod on friday afternoon",
            "    [ ] 4: get burnout from amount of tasks",
            "21-10-2025:",
            "    [ ] 2: accidentally take down cloudflare",
            "No deadline:",
            "    [ ] 1: fix simple bug",
            ""
        );

        execute("quit");
    }


    private void execute(String command) throws IOException {
        read(PROMPT);
        write(command);
    }

    private void read(String expectedOutput) throws IOException {
        int length = expectedOutput.length();
        char[] buffer = new char[length];
        outReader.read(buffer, 0, length);
        assertThat(String.valueOf(buffer), is(expectedOutput));
    }

    private void readLines(String... expectedOutput) throws IOException {
        for (String line : expectedOutput) {
            read(line + lineSeparator());
        }
    }

    private void write(String input) {
        inWriter.println(input);
    }

    private boolean stillRunning() {
        return applicationThread != null && applicationThread.isAlive();
    }
}
