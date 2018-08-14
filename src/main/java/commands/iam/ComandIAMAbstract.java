package commands.iam;

import commands.Command;

public abstract class ComandIAMAbstract extends Command {
    @Override
    public String getGroup() {
        return "IAM";
    }
}
