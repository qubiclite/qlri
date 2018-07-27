package commands.app;

import api.resp.app.ResponseAppList;
import api.resp.general.ResponseAbstract;
import commands.Command;
import commands.param.CallValidator;
import commands.param.ParameterValidator;
import main.App;
import main.Persistence;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Collection;
import java.util.Map;

public class CommandAppList extends Command {

    public static final CommandAppList instance = new CommandAppList();

    private static final CallValidator CV = new CallValidator(new ParameterValidator[]{ });

    @Override
    public CallValidator getCallValidator() {
        return CV;
    }

    @Override
    public String getName() {
        return "apps_list";
    }

    @Override
    public String getAlias() {
        return "al";
    }

    @Override
    public String getDescription() {
        return "prints the full list of all app installed";
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
}
