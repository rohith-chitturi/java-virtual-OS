package com.rohith.javavirtualos.shell;

import com.rohith.javavirtualos.kernel.SystemContext;
import com.rohith.javavirtualos.command.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;

/**
 * The main CLI loop and input parser.
 */
public class Shell {

    private final SystemContext systemContext;
    private final ShellContext shellContext;
    private final CommandRegistry commandRegistry;
    private final List<String> history;

    public Shell(SystemContext systemContext) {
        this.systemContext = systemContext;
        this.shellContext = new ShellContext(systemContext, System.out, System.in);
        this.commandRegistry = new CommandRegistry();
        this.history = new ArrayList<>();
        registerBuiltInCommands();
    }

    private void registerBuiltInCommands() {
        commandRegistry.register(new ExitCommand());
        commandRegistry.register(new EchoCommand());
        commandRegistry.register(new PwdCommand());
        commandRegistry.register(new ClearCommand());
        commandRegistry.register(new DateCommand());
        commandRegistry.register(new TimeCommand());
        commandRegistry.register(new VersionCommand());
        commandRegistry.register(new HelpCommand(commandRegistry));
        commandRegistry.register(new HistoryCommand(history));
    }

    public void start() {
        Scanner scanner = new Scanner(shellContext.getIn());
        boolean running = true;

        while (running) {
            shellContext.getOut().print("JavaOS> ");
            if (!scanner.hasNextLine()) break;

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;

            history.add(input);

            String[] tokens = input.split("\\s+");
            String commandName = tokens[0];
            String[] args = new String[tokens.length - 1];
            System.arraycopy(tokens, 1, args, 0, tokens.length - 1);

            Command command = commandRegistry.getCommand(commandName);
            if (command != null) {
                try {
                    running = command.execute(args, shellContext);
                } catch (Exception e) {
                    shellContext.getOut().println("Error executing command: " + e.getMessage());
                }
            } else {
                shellContext.getOut().println(commandName + ": command not found");
            }
        }
    }
}
