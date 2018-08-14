package commands.oracle;

import commands.Command;

public abstract class CommandOracleAbstract extends Command {
    @Override
    public String getGroup() {
        return "Oracle";
    }
}
