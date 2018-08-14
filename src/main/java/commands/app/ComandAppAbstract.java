package commands.app;

import commands.Command;

public abstract class ComandAppAbstract extends Command {
    @Override
    public String getGroup() {
        return "App";
    }
}
