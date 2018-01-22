package hsaugsburg.zirbl001.Models.NavigationModels;


import android.graphics.Bitmap;

import hsaugsburg.zirbl001.Interfaces.JSONModel;

public class TourSelectionModel implements JSONModel {
    private int categoryID;
    private String categoryName;
    private int tourID;
    private String contentfulID;
    private String tourName;
    private String difficultyName;
    private int duration;
    private int distance;
    private String mainpicture;
    private Bitmap mainPictureBitmap;

    public String getContentfulID() {
        return contentfulID;
    }

    public void setContentfulID(String contentfulID) {
        this.contentfulID = contentfulID;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int categoryID) {
        this.categoryID = categoryID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
