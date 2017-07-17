package hsaugsburg.zirbl001.Models.NavigationModels;

import java.util.ArrayList;

public class OwnStatisticsModel {
    private int tourID;
    private String tourName;
    private String groupName;
    private ArrayList<String> participants;
    private String participationDate;
    private int duration;
    private int score;
    private int rank;
    private int totalParticipations;

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

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<String> participants) {
        this.participants = participants;
    }

    public String getParticipationDate() {
        return participationDate;
    }

    public void setParticipationDate(String participationDate) {
        this.participationDate = participationDate;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public int getTotalParticipations() {
        return totalParticipations;
    }

    public void setTotalParticipations(int totalParticipations) {
        this.totalParticipations = totalParticipations;
    }
}
