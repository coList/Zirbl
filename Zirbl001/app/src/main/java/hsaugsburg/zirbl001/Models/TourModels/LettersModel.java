package hsaugsburg.zirbl001.Models.TourModels;


import java.util.ArrayList;

public class LettersModel {
    private int taskID;
    private int stationID;
    private int tourID;
    private int score;
    private String question;
    private String answerCorrect;
    private String answerWrong;
    private String picturePath;
    private String solution;
    private String otherLetters;

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

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public String getOtherLetters() {
        return otherLetters;
    }

    public void setOtherLetters(String otherLetters) {
        this.otherLetters = otherLetters;
    }
}
