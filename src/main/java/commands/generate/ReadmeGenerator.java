package commands.generate;

import commands.Command;
import org.apache.commons.lang3.StringUtils;

import java.io.*;


public enum ReadmeGenerator { ;

    public static void main(String[] args) throws FileNotFoundException {
        new File("../generated.txt").delete();
        PrintStream ps = new PrintStream(new FileOutputStream("../generated.txt", true));
        printCommandTable(ps);
    }

    /**
     * Generates and prints the command table for the README.md file.
     * */
    public static void printCommandTable(PrintStream out) {
        for (Command command : Command.COMMANDS) {
            String name = StringUtils.rightPad('`'+command.getName()+'`', 25);
            String alias = StringUtils.rightPad('`'+command.getAlias()+'`', 5);
            String description = command.getDescription();
            out.println("| " + name + " | " + alias + " | " + description);
        }
    }
}
