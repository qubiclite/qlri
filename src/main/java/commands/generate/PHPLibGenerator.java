package commands.generate;

import commands.Command;
import commands.param.ParameterValidator;
import resp.general.ResponseSuccess;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.StringJoiner;


public enum PHPLibGenerator { ;

    public static void main(String[] args) throws FileNotFoundException {
        new File("../generated.txt").delete();
        PrintStream ps = new PrintStream(new FileOutputStream("../generated.txt", true));
        PHPLibGenerator.printPHPLibFunctions(ps);
    }

    /**
     * Generates and prints the command functions for the JS Library.
     * */
    public static void printPHPLibFunctions(PrintStream out) {
        for (Command command : Command.COMMANDS)
            if(command.isRemotelyAvailable())
                out.print(genPHPLibFunction(command));
        System.exit(0);
    }

    private static String genPHPLibFunction(Command command) {

        StringJoiner pars = new StringJoiner(", ");

        StringJoiner valMap = new StringJoiner(", ");
        valMap.add("'command' => '"+command.getName()+"'");

        StringJoiner documentation = new StringJoiner("\n * ");
        documentation.add(command.getDescription());

        StringJoiner validation = new StringJoiner("\n\t");

        for(ParameterValidator pv : command.getCallValidator().getParameterValidators()) {

            documentation.add(genPHPDocumentation(pv));

            String val = pv.genPHPValidation();
            if(val != null)
                validation.add(val.replace("_ParameterValidator", "ParameterValidator"));

            if(pv.isOptional()) {
                String defVal = pv.getDefaultValue().toString();
                if(pv.getDefaultValue() instanceof String)
                    defVal = "'" + defVal + "'";
                pars.add("$"+pv.getJSONKey() + " = " + defVal);
            } else
                pars.add("$"+pv.getJSONKey());

            valMap.add("'"+pv.getName()+"' => $" + pv.getJSONKey());
        }

        ResponseSuccess successExample = command.getSuccessResponseExample();
        if(successExample == null)
            successExample = new ResponseSuccess();
        documentation.add("@return array decoded from json, unparsed success example:");
        documentation.add("    " + successExample.toJSON().put("duration", "42").toString());

        StringJoiner sj = new StringJoiner("\n");
        sj.add("/**\n * "+documentation+"\n * */");
        sj.add("public function " + command.getName() + "(" + pars + ") {");
        if(validation.toString().length() > 0) sj.add("\t"+validation);
        sj.add("\t$request = array("+valMap.toString()+");");
        // ...
        sj.add("\treturn $this->send_request($request);");
        sj.add("}");

        return "\n" + sj.toString() + "\n";
    }

    public static String genPHPDocumentation(ParameterValidator pv) {
        String example = (pv.getExampleValue() != null ? ", e.g. " + (pv.getJSType().equals("string") ? "'" + pv.getExampleValue() + "'" : pv.getExampleValue()) : "");
        String parName = pv.isOptional() ? "$"+pv.getJSONKey()+" (optional)" : "$"+pv.getJSONKey();
        return "@param "+pv.getJSType()+" "+parName+" "+pv.getDescription()+example;
    }
}
