package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeCommand implements Command {
    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        return CommandResult.success(LocalTime.now().format(formatter));
    }

    @Override
    public String getName() {
        return "time";
    }

    @Override
    public String getDescription() {
        return "Print current system time";
    }
}
