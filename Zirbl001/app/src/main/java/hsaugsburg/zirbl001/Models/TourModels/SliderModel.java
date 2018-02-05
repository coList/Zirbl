package hsaugsburg.zirbl001.Models.TourModels;

public class SliderModel  extends TaskModel {
    private double rightNumber;
    private double minRange;
    private double maxRange;
    private boolean isInteger;
    private int toleranceRange;


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
