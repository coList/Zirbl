package hsaugsburg.zirbl001.Models.TourModels;

public class SliderModel {
    private int taskID;
    private int stationID;
    private int tourID;
    private int score;
    private String question;
    private String answerCorrect;
    private String answerWrong;
    private String picturePath;
    private String answerPicture;
    private double rightNumber;
    private double minRange;
    private double maxRange;
    private boolean isInteger;
    private int toleranceRange;

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public int getStationID() {
        return stationID;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }

    public int getTourID() {
        return tourID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setTourID(int tourID) {
        this.tourID = tourID;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswerCorrect() {
        return answerCorrect;
    }

    public void setAnswerCorrect(String answerCorrect) {
        this.answerCorrect = answerCorrect;
    }

    public String getAnswerWrong() {
        return answerWrong;
    }

    public void setAnswerWrong(String answerWrong) {
        this.answerWrong = answerWrong;
    }

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getAnswerPicture() {
        return answerPicture;
    }

    public void setAnswerPicture(String answerPicture) {
        this.answerPicture = answerPicture;
    }

    public double getRightNumber() {
        return rightNumber;
    }

    public void setRightNumber(double rightNumber) {
        this.rightNumber = rightNumber;
    }

    public double getMinRange() {
        return minRange;
    }

    public void setMinRange(double minRange) {
        this.minRange = minRange;
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
    }

    public boolean getIsInteger() {
        return isInteger;
    }

    public void setIsInteger(boolean integer) {
        isInteger = integer;
    }

    public int getToleranceRange() {
        return toleranceRange;
    }

    public void setToleranceRange(int toleranceRange) {
        this.toleranceRange = toleranceRange;
    }
}
