package main;

import exceptions.CorruptIAMStreamException;
import oracle.OracleManager;
import oracle.OracleWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import qubic.QubicReader;
import qubic.QubicWriter;
import tangle.IAMPublisher;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.StringJoiner;

public class Persistence {

    private final String filePath;

    private HashMap<String, QubicWriter> qubicWriters = new HashMap();
    private HashMap<String, OracleWriter> oracleWriters = new HashMap();
    private HashMap<String, IAMPublisher> iamPublishers = new HashMap();

    public Persistence(boolean testnet) {
        filePath = "qlite"+(testnet ? "_testnet" : "_mainnet")+".json";
        load();
    }

    public void addQubicWriter(QubicWriter qw) {
        qubicWriters.put(qw.getID(), qw);
        store();
    }

    public void addOracleWriter(OracleWriter ow) {
        oracleWriters.put(ow.getID(), ow);
        store();
    }

    public void addIAMPublisher(IAMPublisher ip) {
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

    public void deleteIAMPublisher(IAMPublisher ip) {
        iamPublishers.remove(ip.getID());
        store();
    }

    /**
     * Finds all IAM streams starting with a specific handle.
     * @param handle the tryte sequence the IAM IDs starts with
     * @return the IAM streams found, NULL if multiple or none found
     * */
    public ArrayList<IAMPublisher> findAllIAMStreamsWithHandle(String handle) {

        ArrayList<IAMPublisher> candidates = new ArrayList<>();
        for(IAMPublisher ip : iamPublishers.values()) {
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
    public ArrayList<OracleWriter> findAllOracleWritersWithHandle(String handle) {

        ArrayList<OracleWriter> candidates = new ArrayList<>();
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
    public ArrayList<QubicWriter> findAllQubicWritersWithHandle(String handle) {

        ArrayList<QubicWriter> candidates = new ArrayList<>();
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
    public IAMPublisher findIAMStreamByHandle(String handle) {

        ArrayList<IAMPublisher> ips = findAllIAMStreamsWithHandle(handle);

        if(ips.size() > 1) {
            Main.println("found " + ips.size() + " IAM streams, which one did you mean?");
            for(IAMPublisher ip : ips)
                Main.println("   > " + ip.getID());
            return null;
        } else if(ips.size() == 0) {
            Main.println("no IAM streams found");
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

        ArrayList<OracleWriter> ows = findAllOracleWritersWithHandle(handle);

        if(ows.size() > 1) {
            Main.println("found " + ows.size() + " oracles, which one did you mean?");
            for(OracleWriter ow : ows)
                Main.println("   > " + ow.getID());
            return null;
        } else if(ows.size() == 0) {
            Main.println("no oracles found");
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

        ArrayList<QubicWriter> qws = findAllQubicWritersWithHandle(handle);

        if(qws.size() > 1) {
            Main.println("found " + qws.size() + " qubics, which one did you mean?");
            for(QubicWriter qw : qws)
                Main.println("   > " + qw.getID());
            return null;
        } else if(qws.size() == 0) {
            Main.println("no qubics found");
            return null;
        } else {
            return qws.get(0);
        }
    }

    /**
     * Stores the current persistence state into the persistence file.
     * */
    protected void store() {
        JSONObject persistenceObject = buildPersistenceObject();
        String persistenceString = persistenceObject.toString();

        try {
            PrintWriter writer = new PrintWriter(filePath, "UTF-8");
            writer.println(persistenceString);
            writer.close();
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }
    }

    /**
     * Writes the persistent data from the running application into a JSON object.
     * That JSON object can than be converted to a string and written in a file.
     * */
    private JSONObject buildPersistenceObject() {

        JSONArray ipArr = new JSONArray();
        for(IAMPublisher ip : iamPublishers.values()) {
            JSONObject ipObj = new JSONObject();
            ipObj.put("id", ip.getID());
            ipObj.put("private_key", ip.getPrivateKeyTrytes());
            ipArr.put(ipObj);
        }

        JSONArray qwArr = new JSONArray();
        for(QubicWriter qw : qubicWriters.values()) {
            JSONObject qwObj = new JSONObject();
            qwObj.put("id", qw.getID());
            qwObj.put("private_key", qw.getPrivateKeyTrytes());
            qwArr.put(qwObj);
        }

        JSONArray owArr = new JSONArray();
        for(OracleWriter ow : oracleWriters.values()) {
            JSONObject owObj = new JSONObject();

            owObj.put("qubic", ow.getQubicReader().getID());
            owObj.put("paused", !ow.getManager().isRunning());
            owObj.put("hash_stream_id", ow.getHashStreamID());
            owObj.put("hash_private_key", ow.getHashPrivateKeyTrytes());
            owObj.put("result_stream_id", ow.getResultStreamID());
            owObj.put("result_private_key", ow.getResultPrivateKeyTrytes());

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
            String privKeyTrytes = ipObj.getString("private_key");

            try {
                IAMPublisher ip = new IAMPublisher(iamId, privKeyTrytes);
                iamPublishers.put(ip.getID(), ip);
            } catch (Throwable t) {
                Main.err("failed loading iam stream " + iamId + ": " + t.getMessage() + " ("+t.getClass().getName()+")");
                continue;
            }
        }

        JSONArray qwArr = persObj.getJSONArray("qubic_writers");
        Main.println("loading "+qwArr.length()+" qubic(s) ...");
        for(int i = 0; i < qwArr.length(); i++) {
            JSONObject qwObj = qwArr.getJSONObject(i);
            String qubicId = qwObj.getString("id");
            String privKeyTrytes = qwObj.getString("private_key");

            try {
                QubicWriter qw = new QubicWriter(qubicId, privKeyTrytes);
                qubicWriters.put(qw.getID(), qw);
            } catch (Throwable t) {
                Main.err("failed loading qubic " + qubicId + ": " + t.getMessage() + " ("+t.getClass().getName()+")");
                continue;
            }
        }

        JSONArray owArr = persObj.getJSONArray("oracle_writers");
        Main.println("loading "+owArr.length()+" oracle(s) ...");
        for(int i = 0; i < owArr.length(); i++) {
            JSONObject owObj = owArr.getJSONObject(i);

            String qubicId = owObj.getString("qubic");
            String hashStreamId = owObj.getString("hash_stream_id");
            String hashPrivKey = owObj.getString("hash_private_key");
            String resStreamId = owObj.getString("result_stream_id");
            String resPrivKey = owObj.getString("result_private_key");
            boolean paused = owObj.getBoolean("paused");

            if(!paused)
                Main.println("starting oracle: '"+resStreamId+"' ...");

            OracleWriter ow;

            try {
                ow = new OracleWriter(new QubicReader(qubicId), hashStreamId, hashPrivKey, resStreamId, resPrivKey);
            } catch (Throwable t) {
                Main.err("failed loading oracle " + resStreamId + ": " + t.getMessage() + " ("+t.getClass().getName()+")");
                continue;
            }

            oracleWriters.put(ow.getID(), ow);
            new OracleManager(ow);

            if(!paused)
                ow.getManager().start();
        }

        Main.println("persistence loaded");
    }

    /**
     * Reads the persistence JSON object from the persistence file.
     * @return JSON object containing the persistent data.
     * */
    private JSONObject readPersistenceObject() {

        String s = readFile(filePath);
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
}
