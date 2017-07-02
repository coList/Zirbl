package hsaugsburg.zirbl001.Models.MapModels;

import org.json.JSONArray;

import hsaugsburg.zirbl001.Interfaces.JSONModel;

public class StationLocationModel implements JSONModel{

    private int tourID;
    private JSONArray stationModel;

    public int getTourID() {
        return tourID;
    }

    public void setTourID(int tourID) {
        this.tourID = tourID;
    }

    public JSONArray getStationModel() {
        return stationModel;
    }

    public void setStationModel(JSONArray stationModel) {
        this.stationModel = stationModel;
    }
}
