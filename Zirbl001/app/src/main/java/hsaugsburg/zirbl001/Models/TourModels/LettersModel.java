package hsaugsburg.zirbl001.Models.TourModels;

public class LettersModel  extends TaskModel {
    private String solution;
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
