package main;

import commands.Command;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Scanner;

public class Main {

    public static final DateFormat DF = new SimpleDateFormat("YYYY/MMM/dd HH:mm:ss");
    private static final String VERSION = "0.1";
    private static final Scanner s = new Scanner(System.in);

    private static  Persistence persistence;

    public static void main(String[] args) {

        println("");
        println("=== Welcome to QLRI v"+VERSION+" ===");
        println("");
        persistence = new Persistence();
        println("");
        println("Type 'help' for more information.");
        println("");

        while(true) {
            String input = nextLine();
            String[] par = input.split(" ");

            String commandString = par[0];
            println("");
            performCommand(commandString, par);
            println("");
        }
    }

    /**
     * Performs a command by validating the parameters and calling the respective command.
     * @param command the command to call the command
     * @param par     the parameters passed with the command call
     * */
    private static void performCommand(String command, String[] par) {

        Command a = Command.findCommand(command);

        if(a == null) {
            println("unknown command '"+command+"', maybe try 'help'");
            return;
        }

        try {

            String validation = a.getCallValidator().validate(par);
            if(validation == null)
                a.perform(persistence, par);
            else {
                println(validation);
                println("for more information try: 'help "+a.getAlias()+"'");
            }

        } catch (Throwable t) {
            System.err.println("exception thrown while trying to perform command '" + a.getName() + "':");
            t.printStackTrace();
        }

    }

    /**
     * Prints a string into the terminal into a seperate line.
     * @param s the string to print
     * */
    public static void println(String s) {
        s = s.replace("\n", "\n  ");
        System.out.println("  " + s);
    }

    /**
     * Waits for user input, giving user opportunity to execute any command.
     * @return user input
     * */
    private static String nextLine() {
        System.out.print("$ ");
        return s.nextLine();
    }

    /**
     * Forces user to give input before user can execute another command.
     * @return user input
     * */
    public static String input() {
        System.out.print("  > ");
        return s.nextLine();
    }
}