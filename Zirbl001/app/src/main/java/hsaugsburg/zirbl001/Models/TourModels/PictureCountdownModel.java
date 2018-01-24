package hsaugsburg.zirbl001.Models.TourModels;

public class PictureCountdownModel {
    private int taskID;
    private int stationID;
    private int tourID;
    private int score;
    private String question;
    private String answerCorrect;
    private String answerWrong;
    private String picturePath;
    private String answerPicture;
    private String rightAnswer;
    private String option2;
    private String option3;

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

    public void setTourID(int tourID) {
        this.tourID = tourID;
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

    public String getRightAnswer() {
        return rightAnswer;
    }

    public void setRightAnswer(String rightAnswer) {
        this.rightAnswer = rightAnswer;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

}
