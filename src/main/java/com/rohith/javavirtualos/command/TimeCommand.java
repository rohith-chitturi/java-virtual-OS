package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeCommand implements Command {
    @Override
    public boolean execute(String[] args, ShellContext context) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        context.getOut().println(LocalTime.now().format(formatter));
        return true;
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
