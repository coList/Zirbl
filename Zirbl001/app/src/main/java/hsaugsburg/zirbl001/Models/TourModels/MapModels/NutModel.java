package hsaugsburg.zirbl001.Models.TourModels.MapModels;

public class NutModel {
    private int tourID;
    private String tourContentfulID;
    private int nutID;
    private String contentfulID;
    private Double latitude;
    private Double longitude;
    private boolean collected = false;
    private int score;
    private String foundText;

    public int getTourID() {
        return tourID;
    }

    public void setTourID(int tourID) {
        this.tourID = tourID;
    }

    public int getNutID() {
        return nutID;
    }

    public void setNutID(int nutID) {
        this.nutID = nutID;
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

    public boolean isCollected() {
        return collected;
    }

    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getFoundText() {
        return foundText;
    }

    public void setFoundText(String foundText) {
        this.foundText = foundText;
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