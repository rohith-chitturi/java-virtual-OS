package com.rohith.javavirtualos.shell;

import com.rohith.javavirtualos.kernel.SystemContext;
import com.rohith.javavirtualos.kernel.UserManager;
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

    private final com.rohith.javavirtualos.services.FileSystemService fsService;
    private final com.rohith.javavirtualos.services.ProcessService processService;
    private final UserManager userManager;

    public Shell(SystemContext systemContext, com.rohith.javavirtualos.services.FileSystemService fsService, com.rohith.javavirtualos.services.ProcessService processService, UserManager userManager) {
        this.systemContext = systemContext;
        this.fsService = fsService;
        this.processService = processService;
        this.userManager = userManager;
        this.shellContext = new ShellContext(systemContext, userManager.getUser("root"), System.out, System.in);
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
        
        // User Commands
        commandRegistry.register(new WhoamiCommand());
        commandRegistry.register(new SuCommand(userManager));
        commandRegistry.register(new UseraddCommand(userManager));
        
        // Process Commands
        commandRegistry.register(new PsCommand(processService));
        commandRegistry.register(new KillCommand(processService));
        commandRegistry.register(new SleepCommand());
        
        // FS Commands
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.MkdirCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.RmdirCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.TouchCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.RmCommand(fsService), "delete", "del");
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.CdCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.LsCommand(fsService), "dir");
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.TreeCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.CatCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.WriteCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.AppendCommand(fsService));
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
                boolean isBackground = false;
                if (args.length > 0 && args[args.length - 1].equals("&")) {
                    isBackground = true;
                    String[] newArgs = new String[args.length - 1];
                    System.arraycopy(args, 0, newArgs, 0, args.length - 1);
                    args = newArgs;
                }
                
                final String[] finalArgs = args;
                if (isBackground) {
                    processService.executeAsProcess(input.replace(" &", ""), () -> {
                        command.execute(finalArgs, shellContext);
                    }, shellContext);
                } else {
                    try {
                        CommandResult result = command.execute(finalArgs, shellContext);
                        if (result.getMessage() != null && !result.getMessage().isEmpty()) {
                            shellContext.getOut().println(result.getMessage());
                        }
                        if (result.shouldTerminateShell()) {
                            running = false;
                        }
                    } catch (Exception e) {
                        shellContext.getOut().println("Error executing command: " + e.getMessage());
                    }
                }
            } else {
                shellContext.getOut().println(commandName + ": command not found");
            }
        }
    }
}
