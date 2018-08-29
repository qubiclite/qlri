package api;

import main.Main;
import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

enum QLWebInstaller { ;

    private static final String QLWEB_DOWNLOAD_URL = "https://github.com/qubiclite/qlweb/archive/v"+Main.QLWEB_VERSION+".zip";
    private static final String QLWEB_ZIP_FILE_NAME = "qlweb.zip";

    static void installQLWeb() {

        Main.println("installing qlweb ...");
        try {
            downloadQLWeb();
            extractQLWeb();
            Main.println("qlweb installation complete");
        } catch (IOException | ZipException e) {
            Main.println("qlweb installation failed");
            e.printStackTrace();
        }
    }

    private static void downloadQLWeb() throws IOException {
        URL url = new URL(QLWEB_DOWNLOAD_URL);
        ReadableByteChannel rbc = Channels.newChannel(url.openStream());
        FileOutputStream fos = new FileOutputStream(QLWEB_ZIP_FILE_NAME);
        fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
    }

    private static void extractQLWeb() throws ZipException {
        ZipFile zipFile = new ZipFile(QLWEB_ZIP_FILE_NAME);
        zipFile.extractAll("./qlweb/" );
        new File("qlweb.zip").delete();
    }
}
