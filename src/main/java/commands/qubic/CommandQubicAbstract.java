package commands.qubic;

import commands.Command;

public abstract class CommandQubicAbstract extends Command {
    @Override
    public String getGroup() {
        return "Qubic";
    }
}
