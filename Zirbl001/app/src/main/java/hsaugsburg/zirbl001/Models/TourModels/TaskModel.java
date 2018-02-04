package hsaugsburg.zirbl001.Models.TourModels;



public class TaskModel {
    private int taskID;
    private String contentfulID;
    private int stationID;
    private String stationContentfulID;
    private int tourID;
    private String tourContentfulID;
    private int score;
    private String question;
    private String answerCorrect;
    private String answerWrong;
    private String picturePath;
    private String answerPicture;

    public int getTaskID() {
        return taskID;
    }

    public void setTaskID(int taskID) {
        this.taskID = taskID;
    }

    public String getContentfulID() {
        return contentfulID;
    }

    public void setContentfulID(String contentfulID) {
        this.contentfulID = contentfulID;
    }

    public int getStationID() {
        return stationID;
    }

    public void setStationID(int stationID) {
        this.stationID = stationID;
    }

    public String getStationContentfulID() {
        return stationContentfulID;
    }

    public void setStationContentfulID(String stationContentfulID) {
        this.stationContentfulID = stationContentfulID;
    }

    public int getTourID() {
        return tourID;
    }

    public void setTourID(int tourID) {
        this.tourID = tourID;
    }

    public String getTourContentfulID() {
        return tourContentfulID;
    }

    public void setTourContentfulID(String tourContentfulID) {
        this.tourContentfulID = tourContentfulID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
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
}
