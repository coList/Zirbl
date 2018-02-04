package hsaugsburg.zirbl001.Models.TourModels.MapModels;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

public class StationModel {
    private int tourID;


    @SerializedName("tourContentfulID")
    private String tourContentfulID;

    @SerializedName("chronologyNumber")
    private int chronologyNumber;
    private int stationID;

    @SerializedName("contentfulID")
    private String contentfulID;

    @SerializedName("stationName")
    private String stationName;

    @SerializedName("mapInstruction")
    private String mapInstruction;

    @SerializedName("latitude")
    private Double latitude;

    @SerializedName("longitude")
    private Double longitude;

    @SerializedName("wayPoints")
    private JSONObject wayPoints;

    public int getTourID() {
        return tourID;
    }

    public void setTourID(int tourID) {
        this.tourID = tourID;
    }

    public String getTourContentfulID() {
        return tourContentfulID;
    }

    public void setTourContentfulID(String contentfulID) {
        this.tourContentfulID = contentfulID;
    }


    public int getChronologyNumber() {
        return chronologyNumber;
    }

    public void setChronologyNumber(int chronologyNumber) {
        this.chronologyNumber = chronologyNumber;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getMapInstruction() {
        return mapInstruction;
    }

    public void setMapInstruction(String mapInstruction) {
        this.mapInstruction = mapInstruction;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public int getStationID() {
        return stationID;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }

    public String getContentfulID() {
        return contentfulID;
    }

    public void setContentfulID(String contentfulID) {
        this.contentfulID = contentfulID;
    }

    public JSONObject getWayPoints() {
        return wayPoints;
    }

    public void setWayPoints(JSONObject wayPoints) {
        this.wayPoints = wayPoints;
    }
}
