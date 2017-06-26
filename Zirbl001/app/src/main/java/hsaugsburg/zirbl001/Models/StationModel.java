package hsaugsburg.zirbl001.Models;

/**
 * Created by Mowie on 26.06.17.
 */

public class StationModel {
    private int tourID;
    private int chronologyNumber;
    private String stationName;
    private Double latitude;
    private Double longitude;



    public int getTourID() {
        return tourID;
    }

    public void setTourID(int tourID) {
        this.tourID = tourID;
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
}
