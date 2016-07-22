package pt.ulisboa.tecnico.mybasaclient.model.firebase;

/**
 * Created by Sampaio on 21/07/2016.
 */
public class UserFirebase {

    private String name;
    private String id;

    public UserFirebase() {
    }

    public UserFirebase(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
