package hsaugsburg.zirbl001;


import android.graphics.Bitmap;

public class TourSelectionModel implements JSONModel {
    private int categoryID;
    private String categoryName;
    private int tourID;
    private String tourName;
    private String difficultyName;
    private int duration;
    private int distance;
    private String mainpicture;

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
}
