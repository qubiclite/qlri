package commands.app;

import resp.app.ResponseAppList;
import resp.general.ResponseAbstract;
import resp.general.ResponseSuccess;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import main.App;
import main.Persistence;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

public class CommandAppList extends ComandAppAbstract {

    public static final CommandAppList instance = new CommandAppList();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{ });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "app_list";
    }

    @Override
    public String getAlias() {
        return "al";
    }

    @Override
    public String getDescription() {
        return "Lists all apps installed.";
    }

    @Override
    public void terminalPostPerformAction(ResponseAbstract response, Persistence persistence, String[] par) {

        JSONArray apps = ((ResponseAppList)response).getApps();

        println("found " + apps.length() + " installed app(s):");
        for(int i = 0; i < apps.length(); i++)
            println("   > " + apps.getJSONObject(i).getString("title"));
    }

    @Override
    public ResponseAbstract perform(Persistence persistence, Map<String, Object> parMap) {

        Collection<App> apps = persistence.getApps();
        JSONArray arr = new JSONArray();
        for(App app : apps) {
            JSONObject json = new JSONObject();
            json.put("id", app.getID());
            json.put("title", app.getTitle());
            json.put("description", app.getDescription());
            json.put("version", app.getVersion());
            json.put("url", app.getUrl());
            json.put("license", app.getLicense());
            arr.put(json);
        }
        return new ResponseAppList(arr);
    }

    @Override
    public ResponseSuccess getSuccessResponseExample() {

        JSONArray arr = new JSONArray();

        JSONObject json = new JSONObject();
        json.put("id", "tanglefarm");
        json.put("title", "Tangle Farm");
        json.put("description", "Grow and harvest food on your own farm. The first qApp and decentralized IOTA game. The game state is entirely stored on the Tangle and validated by Qubic Lite.");
        json.put("version", "v0.1");
        json.put("url", "http://qame.org/tanglefarm");
        json.put("license", "&copy;2018 by microhash for qame.org");
        arr.put(json);

        return new ResponseAppList(arr);
    }
}
