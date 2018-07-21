package commands.param;

public abstract class ParameterValidator {

    private String name;
    private String description;
    private String exampleValue;
    private Object defValue;
    private boolean optional = false;

    public abstract String validate(String par);

    public ParameterValidator setName(String name) {
        this.name = name;
        return this;
    }

    public ParameterValidator setDescription(String description) {
        this.description = description;
        return this;
    }

    public ParameterValidator setExampleValue(String exampleValue) {
        this.exampleValue = exampleValue;
        return this;
    }

    public ParameterValidator makeOptional(Object defValue) {
        this.defValue = defValue;
        optional = true;
        return this;
    }

    public String getJSONKey() {
        return name.toLowerCase().replace(' ', '_');
    }

    public abstract Object convertParToObject(String par);

    public String getName() {
        return name + (optional ? " [OPTIONAL]" : "");
    }

    public String getDescription() {
        return description;
    }

    public String getExampleValue() {
        return exampleValue;
    }

    public Object getDefaultValue() {
        return defValue;
    }

    public boolean isOptional() {
        return optional;
    }
}
