package com.rohith.javavirtualos.command;

import com.rohith.javavirtualos.shell.ShellContext;
import java.time.LocalDate;

public class DateCommand implements Command {
    @Override
    public boolean execute(String[] args, ShellContext context) {
        context.getOut().println(LocalDate.now());
        return true;
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
