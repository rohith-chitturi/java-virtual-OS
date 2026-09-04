package com.rohith.javavirtualos.shell;

import com.rohith.javavirtualos.kernel.SystemContext;
import com.rohith.javavirtualos.kernel.UserManager;
import com.rohith.javavirtualos.command.*;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import com.rohith.javavirtualos.kernel.network.NetworkManager;
import com.rohith.javavirtualos.command.network.*;
import com.rohith.javavirtualos.kernel.device.DeviceManager;
import com.rohith.javavirtualos.command.device.*;

/**
 * The main CLI loop and input parser.
 */
public class Shell {

    private final ShellContext shellContext;
    private final CommandRegistry commandRegistry;
    private final List<String> history;

    private final com.rohith.javavirtualos.services.FileSystemService fsService;
    private final com.rohith.javavirtualos.services.ProcessService processService;
    private final UserManager userManager;
    private final NetworkManager networkManager;
    private final DeviceManager deviceManager;
    private final com.rohith.javavirtualos.kernel.process.runtime.RuntimeStatistics runtimeStats;

    public Shell(SystemContext systemContext, com.rohith.javavirtualos.services.FileSystemService fsService, com.rohith.javavirtualos.services.ProcessService processService, UserManager userManager, NetworkManager networkManager, DeviceManager deviceManager, com.rohith.javavirtualos.kernel.process.runtime.RuntimeStatistics runtimeStats) {
        this.fsService = fsService;
        this.processService = processService;
        this.userManager = userManager;
        this.networkManager = networkManager;
        this.deviceManager = deviceManager;
        this.runtimeStats = runtimeStats;
        this.shellContext = new ShellContext(systemContext, userManager.getUser("root"), System.out, System.in);
        this.commandRegistry = new CommandRegistry();
        this.history = new ArrayList<>();
        registerBuiltInCommands();
    }

    private void registerBuiltInCommands() {
        commandRegistry.register(new ExitCommand());
        commandRegistry.register(new EchoCommand());
        commandRegistry.register(new ExecCommand(processService, fsService, runtimeStats));
        commandRegistry.register(new RuntimeInfoCommand(runtimeStats));
        commandRegistry.register(new PwdCommand());
        commandRegistry.register(new ClearCommand());
        commandRegistry.register(new DateCommand());
        commandRegistry.register(new TimeCommand());
        commandRegistry.register(new VersionCommand());
        commandRegistry.register(new HelpCommand(commandRegistry));
        commandRegistry.register(new HistoryCommand(history));
        commandRegistry.register(new AliasCommand());
        
        // User Commands
        commandRegistry.register(new WhoamiCommand());
        commandRegistry.register(new SuCommand(userManager));
        commandRegistry.register(new UseraddCommand(userManager));
        
        // Process Commands
        commandRegistry.register(new PsCommand(fsService));
        commandRegistry.register(new PstreeCommand(processService));
        commandRegistry.register(new ThreadsCommand(processService));
        commandRegistry.register(new KillCommand(processService));
        commandRegistry.register(new SleepCommand());
        commandRegistry.register(new CpuInfoCommand(processService));
        commandRegistry.register(new SchedCommand(processService));
        commandRegistry.register(new SetSchedulerCommand(processService));
        commandRegistry.register(new AffinityCommand(processService));
        commandRegistry.register(new RunQueueCommand(processService));
        commandRegistry.register(new BenchmarkSchedulerCommand());
        commandRegistry.register(new VmMapCommand(processService));
        commandRegistry.register(new TopCommand(fsService));
        commandRegistry.register(new FreeCommand(fsService));
        commandRegistry.register(new VmstatCommand(fsService));
        commandRegistry.register(new IostatCommand(fsService));
        commandRegistry.register(new DmesgCommand(fsService));
        
        // FS Commands
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.MkdirCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.RmdirCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.TouchCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.RmCommand(fsService), "delete", "del");
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.CdCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.LsCommand(fsService), "dir");
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.TreeCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.text.GrepCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.text.WcCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.text.HeadCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.text.TailCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.CatCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.CpCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.MvCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.FindCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.WriteCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.AppendCommand(fsService));
        commandRegistry.register(new com.rohith.javavirtualos.command.fs.LnCommand(fsService));
        
        // Network Commands
        commandRegistry.register(new IfConfigCommand(networkManager));
        commandRegistry.register(new RouteCommand(networkManager));
        commandRegistry.register(new NetstatCommand(networkManager));
        commandRegistry.register(new PingCommand(networkManager));
        
        // Device Commands
        commandRegistry.register(new LsDevCommand(deviceManager));
        commandRegistry.register(new MountCommand(fsService));
        commandRegistry.register(new DevicesCommand(deviceManager));
        commandRegistry.register(new DevStatCommand(deviceManager));
    }

    @SuppressWarnings("resource")
    public void start() {
        Scanner scanner = new Scanner(shellContext.getIn());
        boolean running = true;

        while (running) {
            shellContext.getOut().print("JavaOS> ");
            if (!scanner.hasNextLine()) break;

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) continue;
            
            if (input.equals("exit")) {
                running = false;
                continue;
            }

            history.add(input);
            
            ShellParser parser = new ShellParser(commandRegistry, shellContext, fsService);
            parser.executeLine(input);
        }
    }
}
