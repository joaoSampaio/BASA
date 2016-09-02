package pt.ulisboa.tecnico.basa.model;

import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import pt.ulisboa.tecnico.basa.app.AppController;
import pt.ulisboa.tecnico.basa.manager.UserManager;
import pt.ulisboa.tecnico.basa.model.event.EventLightSwitch;
import pt.ulisboa.tecnico.basa.model.event.EventUserLocation;
import pt.ulisboa.tecnico.basa.util.ModelCache;

/**
 * Created by Sampaio on 22/08/2016.
 */
public class AllStatisticalData {

    private List<StatisticalEvent> lights;
    private List<StatisticalEvent> temperature;
    private List<StatisticalEvent> occupantsOffice;
    private List<StatisticalEvent> occupantsBuilding;

    public AllStatisticalData() {
        lights = new ArrayList<>();
        temperature = new ArrayList<>();
        occupantsOffice = new ArrayList<>();
        occupantsBuilding = new ArrayList<>();
    }

    public List<StatisticalEvent> getLights() {
        return lights;
    }

    public List<StatisticalEvent> getTemperature() {
        return temperature;
    }

    public List<StatisticalEvent> getOccupantsOffice() {
        return occupantsOffice;
    }

    public List<StatisticalEvent> getOccupantsBuilding() {
        return occupantsBuilding;
    }

    public void addLightsEvent(EventLightSwitch event){
        StatisticalEvent statisticalEvent = null;
        int count = AppController.getInstance().getBasaManager().getLightingManager().lightsOn();
        if(!getLights().isEmpty()){
            long timeSinceLast = System.currentTimeMillis() - getLights().get(getLights().size()-1).getX();
            StatisticalEvent latest = getLights().get(getLights().size()-1);
            if(timeSinceLast <= 1000){
                statisticalEvent = latest;
                statisticalEvent.setY(count);
            }else if(latest.getY() == count) {
                //don't save if no change is present, result of app shutdown
                return;
            }
        }
        if(statisticalEvent == null) {
            statisticalEvent = new StatisticalEvent(System.currentTimeMillis(), count);
            getLights().add(statisticalEvent);
        }

        save(this);
    }

    public void addTemperatureEvent(StatisticalEvent event){
        getTemperature().add(event);
    }

    public void addOccupantEvent(EventUserLocation location){
        StatisticalEvent statisticalEvent = null;
        UserManager userManager = AppController.getInstance().getBasaManager().getUserManager();
        if (location.getLocation() == EventUserLocation.TYPE_BUILDING ) {
            statisticalEvent = new StatisticalEvent(System.currentTimeMillis(), userManager.numActiveUsersBuilding());
            getOccupantsBuilding().add(statisticalEvent);
        }else{
            statisticalEvent = new StatisticalEvent(System.currentTimeMillis(), userManager.numActiveUsersOffice());
            getOccupantsOffice().add(statisticalEvent);
        }


        save(this);

    }

    public static AllStatisticalData load(){
        AllStatisticalData stats = new ModelCache<AllStatisticalData>().loadModel(new TypeToken<AllStatisticalData>(){}.getType(), "stats_4");
        if(stats == null)
            stats = new AllStatisticalData();
        return stats;
    }

    public static void save(AllStatisticalData stats){
        new ModelCache<AllStatisticalData>().saveModel(stats, "stats_4");
    }


}