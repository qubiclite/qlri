package commands.param;

public abstract class ParameterValidator {

    private String name;
    private String description;
    private String exampleValue;
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

    public ParameterValidator makeOptional() {
        optional = true;
        return this;
    }

    public String getName() {
        return name + (optional ? " [OPTIONAL]" : "");
    }

    public String getDescription() {
        return description;
    }

    public String getExampleValue() {
        return exampleValue;
    }

    public boolean isOptional() {
        return optional;
    }
}
