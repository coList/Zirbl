package hsaugsburg.zirbl001;


import java.util.List;

public class TourDetailModel implements JSONModel {
    private String tourName;
    private int tourID;
    private String categoryName;
    private String difficultyName;
    private int duration;
    private int distance;
    private String description;
    private String mapPicture;
    private List<String> picturesPath;
    private String videoPath;
    private String costs;
    private String warnings;

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
}
