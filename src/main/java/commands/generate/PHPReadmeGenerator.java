package commands.generate;

import commands.Command;
import commands.param.ParameterValidator;
import resp.general.ResponseAbstract;
import resp.general.ResponseSuccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.*;


public enum PHPReadmeGenerator { ;

    public static void main(String[] args) throws FileNotFoundException {
        new File("../generated.txt").delete();
        PrintStream ps = new PrintStream(new FileOutputStream("../generated.txt", true));
        printJSLibREADME(ps);
    }

    /**
     * Generates and prints the command documentation of the JS Library for the README.md file.
     * */
    public static void printJSLibREADME(PrintStream out) {
        out.println(printNav());
        out.println("## FUNCTIONS");
        for (Command command : Command.COMMANDS)
            if(command.isRemotelyAvailable())
                out.print(genJSLibREADME(command));
        System.exit(0);
    }

    private static String genJSLibREADME(Command command) {
        StringJoiner sj = new StringJoiner("\n");
        sj.add("\n### `" + command.getName() + "()`");
        sj.add(command.getDescription());
        genParameterTable(command, sj);
        genExampleCall(command, sj);
        genSuccessResponseExample(command, sj);
        sj.add("***");
        return sj.toString();
    }

    private static void genParameterTable(Command command, StringJoiner sj) {
        sj.add("#### parameters");
        if(command.getCallValidator().getParameterValidators().length == 0) {
            sj.add("no parameters");
            return;
        }
        sj.add("| name | type | description |");
        sj.add("| - | - | - |");
        for(ParameterValidator pv : command.getCallValidator().getParameterValidators()) {
            sj.add("| `$" + pv.getJSONKey() + "` "+(pv.isOptional() ? " (opt.)" : "")+" | `" + pv.getJSType() + " (" + pv.toString().toLowerCase() + ")` | " + pv.getDescription());
        }
    }

    private static void genExampleCall(Command command, StringJoiner sj) {
        sj.add("#### example call");

        StringJoiner parAssignment = new StringJoiner(", ");

        for(ParameterValidator pv : command.getCallValidator().getParameterValidators())
            parAssignment.add(pv.getJSType().equals("string") ? "'" + pv.getExampleValue()  + "'" : pv.getExampleValue());

        sj.add("```php\ntry {\n    $res = $qlite->"+command.getName()+"("+parAssignment+");\n    // process $res ...\n} catch (Exception $exc) {\n    echo $exc; // handle exception\n}\n```");
    }

    private static void genSuccessResponseExample(Command command, StringJoiner sj) {
        ResponseAbstract exampleResponse = command.getSuccessResponseExample();
        if(exampleResponse == null)
            exampleResponse = new ResponseSuccess();
        sj.add("#### example response (before being parsed into a php array)");
        sj.add("```json\n"+exampleResponse.toJSON().put("duration", 42).toString()+"\n```");
    }

    private static String printNav() {
        StringJoiner sj = new StringJoiner("\n");

        Map<String, Collection<Command>> grouped = new HashMap<>();

        for(Command c : Command.COMMANDS)
            if(c.isRemotelyAvailable()) {
                grouped.putIfAbsent(c.getGroup(), new LinkedList<>());
                grouped.get(c.getGroup()).add(c);
            }

        for(String group : grouped.keySet()) {
            Collection<Command> collection = grouped.get(group);
            sj.add("* " + group);
            for (Command c : collection)
                sj.add("    * [" + c.getName() + "](#"+c.getName()+")");
        }

        sj.add("***");
        return sj.toString();
    }
}
