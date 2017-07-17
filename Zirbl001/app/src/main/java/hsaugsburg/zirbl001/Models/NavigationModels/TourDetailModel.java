package hsaugsburg.zirbl001.Models.NavigationModels;

import android.graphics.Bitmap;
import java.util.List;
import hsaugsburg.zirbl001.Interfaces.JSONModel;

public class TourDetailModel implements JSONModel {
    private String tourName;
    private int tourID;
    private String categoryName;
    private String difficultyName;
    private int duration;
    private int distance;
    private String mainPicture;
    private String description;
    private String mapPicture;
    private List<String> picturesPath;
    private String videoPath;
    private String costs;
    private String warnings;
    private Bitmap mainPictureBitmap;
    private String shortDescription;

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    private String startLocation;
    private String endLocation;

    public String getTourName() {
        return tourName;
    }

    public void setTourName (String tourName) {
        this.tourName = tourName;
    }

    public int getTourID () {
        return tourID;
    }

    public void setTourID (int tourID) {
        this.tourID = tourID;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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

    public String getMainPicture() {return mainPicture; }

    public void setMainPicture(String mainPicture) {this.mainPicture = mainPicture;}

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMapPicture() {
        return mapPicture;
    }

    public void setMapPicture(String mapPicture) {
        this.mapPicture = mapPicture;
    }

    public List<String> getPicturesPath() {
        return picturesPath;
    }

    public void setPicturesPath(List<String> picturesPath) {
        this.picturesPath = picturesPath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getCosts() {
        return costs;
    }

    public void setCosts(String costs) {
        this.costs = costs;
    }

    public String getWarnings() {
        return warnings;
    }

    public void setWarnings(String warnings) {
        this.warnings = warnings;
    }

    public Bitmap getMainPictureBitmap() {
        return mainPictureBitmap;
    }

    public void setMainPictureBitmap(Bitmap mainPictureBitmap) {
        this.mainPictureBitmap = mainPictureBitmap;
    }

    public String getShortDescription() { return shortDescription; }

    public void setShortDescription(String shortDescription) { this.shortDescription = shortDescription; }
}
