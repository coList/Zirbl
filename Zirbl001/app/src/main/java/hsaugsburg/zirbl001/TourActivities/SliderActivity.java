package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import hsaugsburg.zirbl001.Datamanagement.JSONSlider;
import hsaugsburg.zirbl001.Models.SliderModel;
import hsaugsburg.zirbl001.R;

public class SliderActivity extends AppCompatActivity {

    private Context mContext = SliderActivity.this;

    private static SeekBar slider;
    private static TextView sliderCount;
    private static Double minValue;
    private static int sliderMax = 2000;

    private int chronologyNumber;

    private boolean answerSelected;


    private boolean isInteger;
    private String userAnswer;
    private String rightAnswer;
    private String answerCorrect;
    private String answerWrong;
    private int score;

    private int currentScore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));
        int taskID = Integer.parseInt(getIntent().getStringExtra("taskid"));
        currentScore = Integer.parseInt(getIntent().getStringExtra("currentscore"));
        setContentView(R.layout.activity_slider);
        slider = (SeekBar) findViewById(R.id.slider);


        new JSONSlider(this, taskID).execute("https://zirbl.multimedia.hs-augsburg.de/selectGuessTheNumberView.php");
    }

    public void continueToNextView(View view) {
        if (!isInteger) {
            userAnswer = Double.toString(getConvertedDoubleValue(slider.getProgress() + getConvertedIntValue(minValue)));
        } else {
            userAnswer = Integer.toString(slider.getProgress() + minValue.intValue());
        }

        Log.d("SliderActivity", Integer.toString(slider.getProgress() + minValue.intValue()));
        
        if (answerSelected)  {
            Intent intent = new Intent(mContext, PointsActivity.class);
            intent.putExtra("isSlider", "true");
            intent.putExtra("userAnswer", userAnswer);
            intent.putExtra("solution", rightAnswer);
            intent.putExtra("answerCorrect", answerCorrect);
            intent.putExtra("answerWrong", answerWrong);
            intent.putExtra("score", Integer.toString(score));
            intent.putExtra("chronologyNumber", Integer.toString(chronologyNumber));
            intent.putExtra("currentscore", Integer.toString(currentScore));
            startActivity(intent);
        } else {
            Animation shake = AnimationUtils.loadAnimation(SliderActivity.this, R.anim.shake);
            findViewById(R.id.continueButton).startAnimation(shake);
        }

    }


    //can't set minValue directly -> add minValue to valueDisplayed and substract minValue from maxValue
    public void processData(SliderModel result) {
        TextView question = (TextView) findViewById(R.id.questionText);
        question.setText(result.getQuestion());
        isInteger = result.getIsInteger();
        minValue = result.getMinRange();


        sliderCount = (TextView) findViewById(R.id.sliderCount);

        if (!isInteger) {
            slider.setMax(getConvertedIntValue(result.getMaxRange() - minValue));
            sliderCount.setText(Double.toString(getConvertedDoubleValue(slider.getProgress() + getConvertedIntValue(minValue))));
        } else {
            Double value = result.getMaxRange() - minValue;
            slider.setMax(value.intValue());
            sliderCount.setText(Integer.toString(slider.getProgress() + minValue.intValue()));
        }



        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                answerSelected = true;
                if (!isInteger) {
                    sliderCount.setText(Double.toString(getConvertedDoubleValue(progress) + minValue));
                } else {
                    sliderCount.setText(Integer.toString(progress + minValue.intValue()));
                }

            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        rightAnswer = Double.toString(result.getRightNumber());
        answerCorrect = result.getAnswerCorrect();
        answerWrong = result.getAnswerWrong();
        score = result.getScore();
    }


    //Convert double value to int by multiplying it (every value is 100 times its value!)
    private int getConvertedIntValue(double value) {
        return (int) Math.round(value * 100);
    }

    private double getConvertedDoubleValue(int value) {
        return value / 100.0;
    }

    private void showEndTourDialog(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                EndTourDialog alertEnd = new EndTourDialog(mContext);
                alertEnd.showDialog((Activity) mContext);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showEndTourDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
