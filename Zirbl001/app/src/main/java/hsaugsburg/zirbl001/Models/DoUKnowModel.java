package hsaugsburg.zirbl001.Models;

public class DoUKnowModel {
    private int tourID;
    private int infoPopupID;
    private String contentText;
    private double latitude;
    private double longitude;
    private String picturePath;

    public int getTourID() {
        return tourID;
    }

    public void setTourID(int tourID) {
        this.tourID = tourID;
    }

    public int getInfoPopupID() {
        return infoPopupID;
    }

    public void setInfoPopupID(int infoPopupID) {
        this.infoPopupID = infoPopupID;
    }

    public String getContentText() {
        return contentText;
    }

    public void setContentText(String contentText) {
        this.contentText = contentText;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }
}
