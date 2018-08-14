package commands.generate;

import api.resp.general.ResponseSuccess;
import commands.Command;
import main.Main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.StringJoiner;


public enum APIDocGenerator { ;

    public static void main(String[] args) throws FileNotFoundException {
        new File("../generated.txt").delete();
        PrintStream ps = new PrintStream(new FileOutputStream("../generated.txt", true));
        printAPIDocumentation(ps);
    }

    /**
     * Generates and prints the command table for the README.md file.
     * */
    private static void printAPIDocumentation(PrintStream out) {
        for (Command command : Command.COMMANDS)
            if(command.isRemotelyAvailable())
                out.print(genAPIDoc(command));
        System.exit(0);
    }

    private static void genDocHead(Command command, StringJoiner sj) {
        sj.add("@api {post} / " + command.getName().replace('_', ' '));
        sj.add("@apiName " + command.getName());
        sj.add("@apiGroup " + command.getGroup());
        sj.add("@apiVersion " + Main.VERSION);
        sj.add("@apiDescription " + command.getDescription());
    }

    private static String genAPIDoc(Command command) {
        StringJoiner sj = new StringJoiner("\n * ");
        genDocHead(command, sj);
        sj.add(command.getCallValidator().genDoc());
        genAPIDocExample(command, sj);
        genAPIDocSuccessExample(command, sj);
        return "/**\n * " + sj.toString() + "\n */\n\n";
    }

    private static void genAPIDocSuccessExample(Command command, StringJoiner sj) {
        ResponseSuccess successResponseExample = command.getSuccessResponseExample();

        if(successResponseExample != null) {
            sj.add("");
            sj.add("@apiSuccessExample Success-Response:");
            sj.add("    " + successResponseExample.toJSON().put("duration", "42"));
        }
    }

    private static void genAPIDocExample(Command command, StringJoiner sj) {
        String exampleJSONRequest = command.getCallValidator().buildExampleJSONRequest().toString();
        String curl = "{\"command\":\""+command.getAlias()+"\"" + (exampleJSONRequest.length() > 2 ? ',' + exampleJSONRequest.substring(1) : "}");
        curl = curl.replace("'", "\\'");
        sj.add("@apiExample {curl} Example usage:");
        sj.add("    curl -X POST 'http://localhost:17733'");
        sj.add("         -H 'X-QLITE-API-Version: ql-"+Main.VERSION+"'");
        sj.add("         -d '"+curl+"'");
    }
}
