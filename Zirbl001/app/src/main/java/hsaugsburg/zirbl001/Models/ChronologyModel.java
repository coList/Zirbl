package hsaugsburg.zirbl001.Models;



public class ChronologyModel {
    private int tourID;
    private Integer chronologyNumber;
    private Integer infoPopupID;
    private Integer stationID;
    private Integer taskID;
    private String taskClassName;

    public int getTourID() {
        return tourID;
    }

    public void setTourID(int tourID) {
        this.tourID = tourID;
    }

    public Integer getChronologyNumber() {
        return chronologyNumber;
    }

    public void setChronologyNumber(Integer chronologyNumber) {
        this.chronologyNumber = chronologyNumber;
    }

    public Integer getInfoPopupID() {
        return infoPopupID;
    }

    public void setInfoPopupID(Integer infoPopupID) {
        this.infoPopupID = infoPopupID;
    }

    public Integer getStationID() {
        return stationID;
    }

    public void setStationID(Integer stationID) {
        this.stationID = stationID;
    }

    public Integer getTaskID() {
        return taskID;
    }

    public void setTaskID(Integer taskID) {
        this.taskID = taskID;
    }

    public String getTaskClassName() {
        return taskClassName;
    }

    public void setTaskClassName(String taskClassName) {
        this.taskClassName = taskClassName;
    }
}
