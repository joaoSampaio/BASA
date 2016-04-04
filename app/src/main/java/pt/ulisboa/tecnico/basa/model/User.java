package pt.ulisboa.tecnico.basa.model;

/**
 * Created by joaosampaio on 08-03-2016.
 */
public class User {
    private String name;
    private String uuid;

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
