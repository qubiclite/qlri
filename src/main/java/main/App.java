package main;

public class App {

    private final String id;
    private final String title;
    private final String description;
    private final String version;
    private final String url;
    private final String license;

    public App(String id, String title, String description, String version, String url, String license) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.version = version;
        this.url = url;
        this.license = license;
    }

    public String getID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getVersion() {
        return version;
    }

    public String getUrl() {
        return url;
    }

    public String getLicense() {
        return license;
    }
}
