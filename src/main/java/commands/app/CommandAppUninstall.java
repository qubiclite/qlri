package commands.app;

import resp.general.ResponseAbstract;
import resp.general.ResponseError;
import resp.general.ResponseSuccess;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.AlphaNumericValidator;
import commands.param.validators.StringValidator;
import commands.param.validators.URLValidator;
import main.Persistence;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;

public class CommandAppUninstall extends ComandAppAbstract {

    public static final CommandAppUninstall instance = new CommandAppUninstall();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new AlphaNumericValidator().setName("app").setDescription("app ID (directory name in '"+Persistence.APP_DIR_PATH+"')").setExampleValue("tanglefarm")
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "app_uninstall";
    }

    @Override
    public String getAlias() {
        return "au";
    }

    @Override
    public String getDescription() {
        return "Uninstalls an app.";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("app uninstalled successfully");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        File f = new File(Persistence.APP_DIR_PATH+"/"+parMap.get("app"));

        try {
            FileUtils.deleteDirectory(f);
        } catch (IOException e) {
            new ResponseError(e);
        }

        persistence.loadApps();
        return new ResponseSuccess();
    }
}
