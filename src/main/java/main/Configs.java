package main;

import api.ApiAccount;
import api.PrivilegedApiAccount;
import commands.param.validators.NodeAddressValidator;
import tangle.NodeAddress;
import tangle.TangleAPI;

import java.security.InvalidParameterException;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;

public class Configs {

    private int port = 17733;
    private String host, nodeAddress = null;
    private TangleNet net = TangleNet.TEST_NET;
    private boolean apiEnabled = false, localPowEnabled = true;
    private final List<ApiAccount> accounts = new LinkedList<>();

    private static Configs instance = new Configs();

    public static Configs getInstance() {
        return instance;
    }

    public Iterable<ApiAccount> getAccounts() {
        return accounts;
    }

    protected void processArguments(String[] args) {
        for(int i = 0; i < args.length; i++) {
            if(args[i].charAt(0) == '-') {
                try {
                    processArgument(args[i], i+1 < args.length ? args[i+1] : null);
                } catch (Throwable t) {
                    Main.println("error while processing argument '"+args[i] + "': " + t.getMessage() + " ("+t.getClass().getName()+")");
                }
            }
        }

        if(getNodeAddress() != null) {
            String address = Configs.getInstance().getNodeAddress();
            String nodeProtocol = address.split(":")[0];
            String nodeHost = address.split("://")[1].split(":")[0];
            String nodePort = address.split(":")[2];
            TangleAPI.changeNode(new NodeAddress(nodeProtocol, nodeHost, nodePort), isTestnet() ? 9 : 14, localPowEnabled);
        }
    }

    private void processArgument(String argName, String argValue) {
        switch (argName) {
            case "-p":
            case "-port":
                int newPort = Integer.parseInt(argValue);
                if(newPort < 0 || newPort > 65535)
                    throw new IllegalArgumentException("port "+port+" is not allowed. please choose a value in the range from 0 to 65535.");
                port = newPort;
                break;
            case "-h":
            case "-host":
                host = argValue;
                break;
            case "-api":
                apiEnabled = true;
                break;
            case "-n":
            case "-node":
                String validate = new NodeAddressValidator().validate(argValue);
                if(validate != null)
                    throw new IllegalArgumentException(validate);
                nodeAddress = argValue;
                break;
            case "-mn":
            case "-mainnet":
                net = TangleNet.MAIN_NET;
                if(nodeAddress == null)
                    nodeAddress = "https://field.carriota.com:443";
                break;
            case "-tn":
            case "-testnet":
                net = TangleNet.TEST_NET;
                break;
            case "-rp":
            case "-remotepow":
                localPowEnabled = false;
                break;
            case "-u":
            case "-user":
                addAccount(argValue);
                break;
            default:
                Main.println("unknown parameter" + argName);
        }
    }

    private void addAccount(String argValue) {
        if(argValue == null)
            throw new NullPointerException();
        if(!argValue.contains(":"))
            throw new InvalidParameterException("could not add user: expected 'user:pass', actual '"+argValue+"'");
        String username = argValue.split(":", -1)[0];
        String password = argValue.split(":", -1)[1];
        accounts.add(new PrivilegedApiAccount(username, password));
    }

    public int getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public boolean isApiEnabled() {
        return apiEnabled;
    }

    public String getNodeAddress() {
        return nodeAddress;
    }

    public boolean isTestnet() {
        return net == TangleNet.TEST_NET;
    }

    public boolean isLocalPowEnabled() {
        return localPowEnabled;
    }
}

enum TangleNet {
    MAIN_NET, TEST_NET
}