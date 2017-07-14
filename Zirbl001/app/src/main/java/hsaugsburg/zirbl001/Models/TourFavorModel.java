package hsaugsburg.zirbl001.Models;


import android.graphics.Bitmap;

import hsaugsburg.zirbl001.Interfaces.JSONModel;

public class TourFavorModel implements JSONModel {


    private int userID;
    private int tourID;
    private String tourName;
    private int duration;
    private int distance;
    private String difficultyName;
    private String mainpicture;
    private Bitmap mainPictureBitmap;


    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getTourID() {
        return tourID;
    }

    public void setTourID(int tourID) {
        this.tourID = tourID;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public String getDifficultyName() {
        return difficultyName;
    }

    public void setDifficultyName(String difficultyName) {
        this.difficultyName = difficultyName;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public String getMainpicture() {
        return mainpicture;
    }

    public void setMainpicture(String mainpicture) {
        this.mainpicture = mainpicture;
    }

    public Bitmap getMainPictureBitmap() {
        return mainPictureBitmap;
    }

    public void setMainPictureBitmap(Bitmap mainPictureBitmap) {
        this.mainPictureBitmap = mainPictureBitmap;
    }
}
