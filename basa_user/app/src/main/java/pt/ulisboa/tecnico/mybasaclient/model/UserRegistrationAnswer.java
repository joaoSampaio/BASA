package pt.ulisboa.tecnico.mybasaclient.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sampaio on 28-06-2016.
 */
public class UserRegistrationAnswer {

    private List<String> uuids;
    private List<String> macAddress;

    public UserRegistrationAnswer() {
        this.uuids = new ArrayList<>();
        this.macAddress = new ArrayList<>();
    }

    public List<String> getUuids() {
        return uuids;
    }

    public void setUuids(List<String> uuids) {
        this.uuids = uuids;
    }

    public List<String> getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(List<String> macAddress) {
        this.macAddress = macAddress;
    }
}
