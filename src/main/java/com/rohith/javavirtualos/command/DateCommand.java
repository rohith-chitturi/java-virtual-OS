package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;
import java.time.LocalDate;

public class DateCommand implements Command {
    @Override
    public CommandResult execute(String[] args, ShellContext context) {
        return CommandResult.success(LocalDate.now().toString());
    }

    @Override
    public String getName() {
        return "date";
    }

    @Override
    public String getDescription() {
        return "Print current system date";
    }
}
