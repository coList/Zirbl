package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new JSONSlider(this).execute("https://zirbl.multimedia.hs-augsburg.de/selectGuessTheNumberView.php");
        setContentView(R.layout.activity_slider);
        slider = (SeekBar) findViewById(R.id.slider);
    }

    public void continueToNextView(View view) {
        Intent intent = new Intent(mContext, NavigationActivity.class);
        startActivity(intent);
    }


    //can't set minValue directly -> add minValue to valueDisplayed and substract minValue from maxValue
    public void processData(SliderModel result) {
        TextView question = (TextView) findViewById(R.id.questionText);
        question.setText(result.getQuestion());
        minValue = result.getMinRange();
        slider.setMax(getConvertedIntValue(result.getMaxRange() - minValue));
        sliderCount = (TextView) findViewById(R.id.sliderCount);
        sliderCount.setText(Double.toString(getConvertedDoubleValue(slider.getProgress() + getConvertedIntValue(minValue))));


        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                sliderCount.setText(Double.toString(getConvertedDoubleValue(progress) + minValue));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });


        final String rightAnswer = Double.toString(result.getRightNumber());
        final String answerCorrect = result.getAnswerCorrect();
        final String answerWrong = result.getAnswerWrong();

        ImageButton continueButton = (ImageButton) findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String userAnswer = Double.toString(getConvertedDoubleValue(slider.getProgress() + getConvertedIntValue(minValue)));

                Intent intent = new Intent(mContext, PointsActivity.class);
                intent.putExtra("isSlider", "true");
                intent.putExtra("userAnswer", userAnswer);
                intent.putExtra("solution", rightAnswer);
                intent.putExtra("answerCorrect", answerCorrect);
                intent.putExtra("answerWrong", answerWrong);
                startActivity(intent);

            }
        });

    }

    //Convert double value to int by multiplying it (every value is 100 times its value!)
    private int getConvertedIntValue(double value) {
        return (int) Math.round(value * 100);
    }

    private double getConvertedDoubleValue(int value) {
        return value / 100.0;
    }

    /*/Hat mal funktioniert...

    public void sliderSelection(){
        slider = (SeekBar)findViewById(R.id.slider);
        sliderCount = (TextView)findViewById(R.id.sliderCount);
        sliderCount.setText(" " + slider.getProgress());
        slider.setMax(sliderMax);

        slider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    int progress_value;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_value = progress;
                        sliderCount.setText(" " + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        sliderCount.setText(" " + progress_value);
                    }
                }
        );

    }
    /*/
}
