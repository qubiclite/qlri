package main;

import api.API;
import iam.IAMWriter;
import oracle.OracleManager;
import oracle.OracleWriter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import qubic.QubicReader;
import qubic.QubicWriter;

import java.io.*;
import java.util.*;

public class Persistence {

    public static final String APP_DIR_PATH = API.QLWEB_PATH + "/qapps";
    private static final String PERSISTENCE_DIR_PATH = "persistence";
    private final String persistenceFileName;

    private HashMap<String, QubicWriter> qubicWriters = new HashMap<>();
    private HashMap<String, OracleWriter> oracleWriters = new HashMap<>();
    private HashMap<String, IAMWriter> iamPublishers = new HashMap<>();
    private HashMap<String, App> apps = new HashMap<>();

    public Persistence(boolean testnet) {
        persistenceFileName = (testnet ? "tn_" : "mn_")+Main.VERSION+".json";
        load();
    }

    public void addQubicWriter(QubicWriter qw) {
        qubicWriters.put(qw.getID(), qw);
        store();
    }

    public void addOracleWriter(OracleWriter ow) {
        if(ow.getManager() == null)
            new OracleManager(ow);
        oracleWriters.put(ow.getID(), ow);
        store();
    }

    public void addIAMWriter(IAMWriter ip) {
        iamPublishers.put(ip.getID(), ip);
        store();
    }

    public void deleteQubicWriter(QubicWriter qw) {
        qubicWriters.remove(qw.getID());
        store();
    }

    public void deleteOracleWriter(OracleWriter ow) {
        oracleWriters.remove(ow.getID());
        store();
    }

    public void deleteIAMPublisher(IAMWriter ip) {
        iamPublishers.remove(ip.getID());
        store();
    }

    /**
     * Finds all IAM streams starting with a specific handle.
     * @param handle the tryte sequence the IAM IDs starts with
     * @return the IAM streams found, NULL if multiple or none found
     * */
    public List<IAMWriter> findAllIAMStreamsWithHandle(String handle) {
        if(handle == null) handle = "";

        List<IAMWriter> candidates = new LinkedList<>();

        for(IAMWriter ip : iamPublishers.values()) {
            if(ip.getID().startsWith(handle))
                candidates.add(ip);
        }
        return candidates;
    }

    /**
     * Finds all oracles starting with a specific handle.
     * @param handle the tryte sequence the oracle IDs starts with
     * @return the oracles found, NULL if multiple or none found
     * */
    public List<OracleWriter> findAllOracleWritersWithHandle(String handle) {
        if(handle == null) handle = "";

        List<OracleWriter> candidates = new LinkedList<>();
        for(OracleWriter ow : oracleWriters.values()) {
            if(ow.getID().startsWith(handle))
                candidates.add(ow);
        }
        return candidates;
    }

    /**
     * Finds all qubics starting with a specific handle.
     * @param handle the tryte sequence the qubic IDs starts with
     * @return the qubics found, NULL if multiple or none found
     * */
    public List<QubicWriter> findAllQubicWritersWithHandle(String handle) {
        if(handle == null) handle = "";

        List<QubicWriter> candidates = new LinkedList<>();
        for(QubicWriter qw : qubicWriters.values()) {
            if(qw.getID().startsWith(handle))
                candidates.add(qw);
        }
        return candidates;
    }

    /**
     * Finds the single IAM stream starting with a specific handle.
     * @param handle the tryte sequence the IAM ID starts with
     * @return the IAM stream found, NULL if multiple or none found
     * */
    public IAMWriter findIAMStreamByHandle(String handle) {

        List<IAMWriter> ips = findAllIAMStreamsWithHandle(handle);

        if(ips.size() > 1) {
            Main.println("found " + ips.size() + " IAM streams, which one did you mean?");
            for(IAMWriter ip : ips)
                Main.println("   > " + ip.getID());
            return null;
        } else if(ips.size() == 0) {
            Main.println("no IAM streams with handle '"+handle+"' found");
            return null;
        } else {
            return ips.get(0);
        }
    }

    /**
     * Finds the single oracle starting with a specific handle.
     * @param handle the tryte sequence the oracle ID starts with
     * @return the oracle found, NULL if multiple or none found
     * */
    public OracleWriter findOracleWriterByHandle(String handle) {

        List<OracleWriter> ows = findAllOracleWritersWithHandle(handle);

        if(ows.size() > 1) {
            Main.println("found " + ows.size() + " oracles, which one did you mean?");
            for(OracleWriter ow : ows)
                Main.println("   > " + ow.getID());
            return null;
        } else if(ows.size() == 0) {
            Main.println("no oracles with handle '"+handle+"' found");
            return null;
        } else {
            return ows.get(0);
        }
    }

    /**
     * Finds the single qubic starting with a specific handle.
     * @param handle the tryte sequence the qubic ID starts with
     * @return the qubic found, NULL if multiple or none found
     * */
    public QubicWriter findQubicWriterByHandle(String handle) {

        List<QubicWriter> qws = findAllQubicWritersWithHandle(handle);

        if(qws.size() > 1) {
            Main.println("found " + qws.size() + " qubics, which one did you mean?");
            for(QubicWriter qw : qws)
                Main.println("   > " + qw.getID());
            return null;
        } else if(qws.size() == 0) {
            Main.println("no qubics with handle '"+handle+"' found");
            return null;
        } else {
            return qws.get(0);
        }
    }

    /**
     * Stores the current persistence state into the persistence file.
     * */
    void store() {
        JSONObject persistenceObject = buildPersistenceObject();
        String persistenceString = persistenceObject.toString();

        File dir = new File(PERSISTENCE_DIR_PATH);
        if(!dir.exists()) dir.mkdir();

        try {
            PrintWriter writer = new PrintWriter(PERSISTENCE_DIR_PATH + "/" + persistenceFileName, "UTF-8");
            writer.println(persistenceString);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the persistent data from the running application into a JSON object.
     * That JSON object can than be converted to a string and written in a file.
     * */
    private JSONObject buildPersistenceObject() {

        JSONArray ipArr = new JSONArray();
        for(IAMWriter ip : iamPublishers.values()) {
            JSONObject ipObj = new JSONObject();
            ipObj.put("id", ip.getID());
            ipObj.put("private_key", ip.getPrivateKeyTrytes());
            ipArr.put(ipObj);
        }

        JSONArray qwArr = new JSONArray();
        for(QubicWriter qw : qubicWriters.values()) {
            JSONObject qwObj = new JSONObject();
            qwObj.put("id", qw.getID());
            qwObj.put("private_key", qw.getIAMWriter().getPrivateKeyTrytes());
            qwArr.put(qwObj);
        }

        JSONArray owArr = new JSONArray();
        for(OracleWriter ow : oracleWriters.values()) {
            JSONObject owObj = new JSONObject();

            owObj.put("qubic", ow.getQubicReader().getID());
            owObj.put("paused", !ow.getManager().isRunning());
            owObj.put("id", ow.getID());
            owObj.put("private_key", ow.getIAMWriter().getPrivateKeyTrytes());

            owArr.put(owObj);
        }

        JSONObject persObj = new JSONObject();
        persObj.put("iam_streams", ipArr);
        persObj.put("qubic_writers", qwArr);
        persObj.put("oracle_writers", owArr);

        return persObj;
    }

    /**
     * Loads the persistence data from the persistence file into the running application.
     * */
    private void load() {

        Main.println("loading persistence ...");

        JSONObject persObj = readPersistenceObject();
        if(persObj == null) {
            Main.println("no persistence found, created new one");
            return;
        }

        JSONArray ipArr = persObj.getJSONArray("iam_streams");
        Main.println("loading "+ipArr.length()+" iam stream(s) ...");
        for(int i = 0; i < ipArr.length(); i++) {
            JSONObject ipObj = ipArr.getJSONObject(i);
            String iamId = ipObj.getString("id");
            String privateKeyTrytes = ipObj.getString("private_key");

            try {
                IAMWriter iw = new IAMWriter(iamId, privateKeyTrytes);
                iamPublishers.put(iw.getID(), iw);
            } catch (Throwable t) {
                Main.err("failed loading iam stream " + iamId + ": " + t.getMessage() + " ("+t.getClass().getName()+")");
            }
        }

        JSONArray qwArr = persObj.getJSONArray("qubic_writers");
        Main.println("loading "+qwArr.length()+" qubic(s) ...");
        for(int i = 0; i < qwArr.length(); i++) {
            JSONObject qwObj = qwArr.getJSONObject(i);
            String qubicId = qwObj.getString("id");
            String privateKeyTrytes = qwObj.getString("private_key");

            try {
                IAMWriter iw = new IAMWriter(qubicId, privateKeyTrytes);
                QubicWriter qw = new QubicWriter(iw);
                qubicWriters.put(qw.getID(), qw);
            } catch (Throwable t) {
                Main.err("failed loading qubic " + qubicId + ": " + t.getMessage() + " ("+t.getClass().getName()+")");
            }
        }

        JSONArray owArr = persObj.getJSONArray("oracle_writers");
        Main.println("loading "+owArr.length()+" oracle(s) ...");
        for(int i = 0; i < owArr.length(); i++) {
            JSONObject owObj = owArr.getJSONObject(i);

            String qubicID = owObj.getString("qubic");
            String oracleID = owObj.getString("id");
            String privateKeyTrytes = owObj.getString("private_key");
            boolean paused = owObj.getBoolean("paused");

            if(!paused)
                Main.println("starting oracle: '"+oracleID+"' ...");

            OracleWriter ow;

            try {
                ow = new OracleWriter(new QubicReader(qubicID), new IAMWriter(oracleID, privateKeyTrytes));
            } catch (Throwable t) {
                Main.err("failed loading oracle " + oracleID + ": " + t.getMessage() + " ("+t.getClass().getName()+")");
                continue;
            }

            oracleWriters.put(ow.getID(), ow);
            new OracleManager(ow);

            if(!paused)
                ow.getManager().start();
        }

        Main.println("loading app(s) ...");
        loadApps();

        Main.println("persistence loaded");
    }

    public void loadApps() {

        HashMap<String, App> newApps = new HashMap<>();

        File appDir = new File(APP_DIR_PATH);
        File[] children = appDir.listFiles();

        if (children != null) {
            for (File child : children) {
                addApp(newApps, child.getName());
            }
        }

        apps = newApps;
    }

    private void addApp(HashMap<String, App> appMap, String appDirName) {

        String metaString = readFile(APP_DIR_PATH + "/" + appDirName + "/meta.json");
        JSONObject meta;

        try {
            meta = new JSONObject(metaString);

            String title       = meta.getString("title");
            String description = meta.getString("description");
            String version     = meta.getString("version");
            String url         = meta.getString("url");
            String license     = meta.getString("license");

            App app = new App(appDirName, title, description, version, url, license);
            appMap.put(appDirName, app);

        } catch (JSONException e) {
            Main.err("failed loading app '"+appDirName+"': invalid meta file");
            e.printStackTrace();
        }
    }

    /**
     * Reads the persistence JSON object from the persistence file.
     * @return JSON object containing the persistent data.
     * */
    private JSONObject readPersistenceObject() {
        String s = readFile(PERSISTENCE_DIR_PATH + "/" + persistenceFileName);
        return s == null ? null : new JSONObject(s);
    }

    /**
     * Reads the content of a file.
     * @param path path to the file
     * @return content of the file, NULL if file not found
     * */
    public String readFile(String path) {

        Scanner scanner;
        File file = new File(path);

        if(file.isDirectory()) {
            Main.println("could not read: '" + path + "' is a directory, not a file");
            return null;
        }

        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            Main.println("could not read file: '" + path + "' not found");
            return null;
        }

        StringJoiner sj = new StringJoiner("\n");
        while(scanner.hasNext()) {
            sj.add(scanner.nextLine());
        }
        scanner.close();

        return sj.toString();
    }

    public Collection<App> getApps() {
        return apps.values();
    }
}
