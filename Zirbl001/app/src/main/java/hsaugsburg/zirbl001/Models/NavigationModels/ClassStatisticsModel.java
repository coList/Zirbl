package hsaugsburg.zirbl001.Models.NavigationModels;

import java.util.ArrayList;

public class ClassStatisticsModel {
    private int classID;
    private int teacherID;
    private String tourName;
    private String className;
    private String schoolName;
    private String participationDate;
    private int duration;
    private String groupName;
    private ArrayList<String> participants;
    private int score;
    private int classRanking;

    public int getClassID() {
        return classID;
    }

    public void setClassID(int classID) {
        this.classID = classID;
    }

    public int getTeacherID() {
        return teacherID;
    }

    public void setTeacherID(int teacherID) {
        this.teacherID = teacherID;
    }

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSchoolName() {
        return schoolName;
    }

    public void setSchoolName(String schoolName) {
        this.schoolName = schoolName;
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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getClassRanking() {
        return classRanking;
    }

    public void setClassRanking(int classRanking) {
        this.classRanking = classRanking;
    }
}
