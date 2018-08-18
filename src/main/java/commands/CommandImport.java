package commands;

import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.StringValidator;
import iam.IAMWriter;
import main.Persistence;
import oracle.OracleManager;
import oracle.OracleWriter;
import qubic.QubicReader;
import qubic.QubicWriter;
import resp.ResponseExport;
import resp.general.ResponseAbstract;
import resp.general.ResponseError;
import resp.general.ResponseSuccess;

import java.security.spec.InvalidKeySpecException;
import java.util.Map;

public class CommandImport extends Command {

    public static final CommandImport instance = new CommandImport();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new StringValidator().setName("encoded").setDescription("the code you received from command '"+CommandExport.instance.getName()+"' (starts with 'i_', 'o_' or 'q_')").setExampleValue("q_HZGULHJSZNDWPTOCXDYYKMKXCCKCHPORELEBZLBQRWHQNBMNAHBGWQYD9WRVHFKRQRXUXLXORJEPTN999_GUACEZHWFAEZEYGUACEZGQFEFFGOAGHSDAHCFCEZGUACEZGDFAABABEYEVJVIDABGBJLFQGNICDRHUBCGSEEDWDZEOFPCDICHGEHHOEYCPGCHJAACCIBGKIZHPINHKGGIBETIJHHANIIESCLCRENCGGUEOCXBBIFJCDJABHFAAGBGYJFEKIWIQCDJBAZIABLBKBFBFEAFCJRFOGGCOHZCHBPDJEWCDCSFZEQHFIHDZCSBOBMFTFNFCETADEODFCRGCCPFAGZIEFRIKFUARGWEOJLELBUGPIRDJGOEHEKGGFBFXBDDDHSEZCTFAFTEYAXIQIAAPFTGHFJCYBYASCFACBIEDAEFJEIIIGAENFAABABEYEPDTBGAFDIBBHHDQCXCIBRIMHACEIHCFJPAUBVCHESHEECACERIHHWFJHHFFACIXIBIJIHAOCGDGIJHZDYJHFFFOABAACAHTFUJHGHEAHWGMFUFRCDDBFHGWAMCUBMDTHGFUJQALIEJSANGMDSBJBUGCGPBZBMJLARJEBJJVFJESGFGZISEJETISJQEZGIHFCYBKEJCKBOIBAQAJBOADDRDTIKDXBFFEASALIWIOAAJRIFGJIUEZHWHFEWDBHTGOFCFVFAFTEYAHDRGKJTIPFGBHHIGIDNAABHFEEEGEAYJIIVFKDD")
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "import";
    }

    @Override
    public String getAlias() {
        return "im";
    }

    @Override
    public String getDescription() {
        return "imports a once exported entity (iam stream, qubic or oracle) encoded by a string";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("import successful");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {
        String encoded = (String)parMap.get("encoded");

        String[] splits = encoded.toUpperCase().split("_");

        if(splits.length != 3 && splits.length != 4)
            return new ResponseError("malformed encoding: should contain three or four underscore seperated parts but " + splits.length + " part(s) found");

        String prefix = splits[0];
        String id = splits[splits.length-2];
        String privateKey = splits[splits.length-1];

        IAMWriter writer;

        try {
            writer = new IAMWriter(id, privateKey);
        } catch (InvalidKeySpecException e) {
            return new ResponseError(e);
        }

        switch (prefix) {
            case "Q":
                if(persistence.findAllQubicWritersWithHandle(writer.getID()).size() > 0)
                    return new ResponseError("this qubic is already imported");
                persistence.addQubicWriter(new QubicWriter(writer));
                break;
            case "O":
                if(persistence.findAllOracleWritersWithHandle(writer.getID()).size() > 0)
                    return new ResponseError("this oracle is already imported");
                QubicReader qubicReader = new QubicReader(splits[1]);
                OracleWriter oracleWriter = new OracleWriter(qubicReader, writer);
                new OracleManager(oracleWriter);
                persistence.addOracleWriter(oracleWriter);
                break;
            case "I":
                if(persistence.findAllIAMStreamsWithHandle(writer.getID()).size() > 0)
                    return new ResponseError("this IAM stream is already imported");
                persistence.addIAMWriter(writer);
                break;
            default:
                return new ResponseError("malformed encoding: unknown prefix '"+prefix+"'");
        }

        return new ResponseSuccess();
    }
}
