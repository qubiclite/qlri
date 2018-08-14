package commands.generate;

import commands.Command;
import commands.param.ParameterValidator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.StringJoiner;


public enum JSLibGenerator { ;

    public static void main(String[] args) throws FileNotFoundException {
        new File("../generated.txt").delete();
        PrintStream ps = new PrintStream(new FileOutputStream("../generated.txt", true));
        JSLibGenerator.printJSLibFunctions(ps);
    }

    /**
     * Generates and prints the command functions for the JS Library.
     * */
    public static void printJSLibFunctions(PrintStream out) {
        for (Command command : Command.COMMANDS)
            if(command.isRemotelyAvailable())
                out.print(genJSLibFunction(command));
        System.exit(0);
    }

    private static String genJSLibFunction(Command command) {


        StringJoiner pars = new StringJoiner(", ");
        pars.add("callback");

        StringJoiner valMap = new StringJoiner(", ");
        valMap.add("'command': '"+command.getName()+"'");

        StringJoiner documentation = new StringJoiner("\n * ");
        documentation.add(command.getDescription());
        documentation.add("@param {function} callback - function to call back, first parameter for return value (json object) in case of success, second parameter for error object in case of error");

        StringJoiner validation = new StringJoiner("\n\t");

        for(ParameterValidator pv : command.getCallValidator().getParameterValidators()) {

            documentation.add(genJSDocumentation(pv));

            String val = pv.genJSValidation();
            if(val != null)
                validation.add(val);

            if(pv.isOptional()) {
                String defVal = pv.getDefaultValue().toString();
                if(pv.getDefaultValue() instanceof String)
                    defVal = "'" + defVal + "'";
                pars.add(pv.getJSONKey() + " = " + defVal);
            } else
                pars.add(pv.getJSONKey());

            valMap.add("'"+pv.getName()+"': " + pv.getJSONKey());
        }

        StringJoiner sj = new StringJoiner("\n");
        sj.add("/**\n * "+documentation+"\n * */");
        sj.add(command.getName() + "(" + pars + ") {");
        if(validation.toString().length() > 0) sj.add("\t"+validation);
        sj.add("\tthis._send({"+valMap+"}, callback);");
        sj.add("}");

        return "\n" + sj.toString() + "\n";
    }

    public static String genJSDocumentation(ParameterValidator pv) {
        String example = (pv.getExampleValue() != null ? ", e.g. " + (pv.getJSType().equals("string") ? "'" + pv.getExampleValue() + "'" : pv.getExampleValue()) : "");
        String parName = pv.isOptional() ? "["+pv.getJSONKey()+"="
                + (pv.getDefaultValue() instanceof String ? "'" + pv.getDefaultValue() + "'" : pv.getDefaultValue())
                +"]" : pv.getJSONKey();
        return "@param {"+pv.getJSType()+"} "+parName+" - " + pv.getDescription() +  example;
    }
}
