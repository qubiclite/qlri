package commands.app;

import resp.general.ResponseAbstract;
import resp.general.ResponseError;
import resp.general.ResponseSuccess;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import commands.param.validators.URLValidator;
import main.Persistence;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Map;

public class CommandAppInstall extends ComandAppAbstract {

    public static final CommandAppInstall instance = new CommandAppInstall();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{
        new URLValidator().setName("url").setDescription("download source of the app").setExampleValue("http://qame.org/tanglefarm")
    });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "app_install";
    }

    @Override
    public String getAlias() {
        return "ai";
    }

    @Override
    public String getDescription() {
        return "Installs an app from an external source.";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {
        println("app installed successfully");
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        // TODO check file types, validate that unzipped is single! directory! with alphanumeric name!

        try {
            URL url = new URL(parMap.get("url") + "/qapp.zip");
            ReadableByteChannel rbc = Channels.newChannel(url.openStream());
            FileOutputStream fos = new FileOutputStream("qapp.zip");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

            ZipFile zipFile = new ZipFile("qapp.zip");
            zipFile.extractAll(Persistence.APP_DIR_PATH );
            new File("qapp.zip").delete();


        } catch (IOException | ZipException e) {
            return new ResponseError(e);
        }

        persistence.loadApps();
        return new ResponseSuccess();
    }
}
