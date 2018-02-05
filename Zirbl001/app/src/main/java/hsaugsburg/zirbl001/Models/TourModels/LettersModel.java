package hsaugsburg.zirbl001.Models.TourModels;

import com.google.gson.annotations.SerializedName;

public class LettersModel  extends TaskModel {


    @SerializedName("solution")
    private String solution;


    @SerializedName("otherLetters")
    private String otherLetters;


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
