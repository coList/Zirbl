package hsaugsburg.zirbl001.Models.TourModels;

import com.google.gson.annotations.SerializedName;

public class DoUKnowModel {
    private int tourID;

    @SerializedName("tourContentfulID")
    private String tourContentfulID;
    private int infoPopupID;

    @SerializedName("contentfulID")
    private String contentfulID;

    @SerializedName("contentText")
    private String contentText;

    @SerializedName("latitude")
    private double latitude;

    @SerializedName("longitude")
    private double longitude;

    @SerializedName("picturePath")
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

    public String getTourContentfulID() {
        return tourContentfulID;
    }

    public void setTourContentfulID(String tourContentfulID) {
        this.tourContentfulID = tourContentfulID;
    }

    public String getContentfulID() {
        return contentfulID;
    }

    public void setContentfulID(String contentfulID) {
        this.contentfulID = contentfulID;
    }
}
