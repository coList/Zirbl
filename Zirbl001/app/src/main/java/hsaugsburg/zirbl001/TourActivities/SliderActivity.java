package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.text.NumberFormat;
import android.os.Build;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadSlider;
import hsaugsburg.zirbl001.Models.TourModels.SliderModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.TopDarkActionbar;

@RequiresApi(api = Build.VERSION_CODES.N)
public class SliderActivity extends AppCompatActivity {
    private Context mContext = SliderActivity.this;

    private  SeekBar slider;
    private  TextView sliderCount;
    private static Double minValue;

    private int chronologyNumber;
    private int selectedTour;
    private String stationName;

    private boolean answerSelected;

    private Double range;
    private boolean isInteger;
    private String rightAnswer;
    private String answerCorrect;
    private String answerWrong;
    private int score;
    private int taskID;
    private int toleranceRange;
    private String answerPicture = "";

    public static final String GLOBAL_VALUES = "globalValuesFile";
    private String serverName;

    public static final String TOUR_VALUES = "tourValuesFile";
    private int currentScore;
    private long startTime;

    private TopDarkActionbar topDarkActionbar;

    private NumberFormat formatter = NumberFormat.getNumberInstance();

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slider);

        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));

        //get global tour values
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        int totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));
        startTime = Long.parseLong(tourValues.getString("startTime", null));
        currentScore = Integer.parseInt(tourValues.getString("currentScore", null));
        findViewById(R.id.slider).setPadding(40,0,40,0);

        stationName = getIntent().getStringExtra("stationName");
        String titleText;

        if (stationName != null && !stationName.isEmpty()) {
            titleText = stationName.toUpperCase();
        } else {
            titleText = "START";
        }
        topDarkActionbar = new TopDarkActionbar(this, titleText);

        slider = (SeekBar) findViewById(R.id.slider);

        TextView count = (TextView) findViewById(R.id.sliderCount);
        count.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);

        setDataView();
    }

    public void setDataView() {
        taskID = Integer.parseInt(getIntent().getStringExtra("taskid"));
        SliderModel result = new LoadSlider(this, selectedTour, taskID).readFile();

        if (!result.getAnswerPicture().equals("null") && !result.getAnswerPicture().isEmpty()) {
            answerPicture = result.getAnswerPicture();
        }

        TextView question = (TextView) findViewById(R.id.questionText);
        question.setText(fromHtml(result.getQuestion()));
        isInteger = result.getIsInteger();
        minValue = result.getMinRange();
        range = result.getMaxRange() - minValue;
        toleranceRange = result.getToleranceRange();

        TextView startCount = (TextView) findViewById(R.id.startCount);

        TextView endCount = (TextView) findViewById(R.id.endCount);


        sliderCount = (TextView) findViewById(R.id.sliderCount);

        if (!isInteger) {
            slider.setMax(getConvertedIntValue(result.getMaxRange() - minValue));
            sliderCount.setText(Double.toString(getConvertedDoubleValue(slider.getProgress() + getConvertedIntValue(minValue))));
            startCount.setText(Double.toString(getConvertedDoubleValue(getConvertedIntValue(minValue))));
            endCount.setText(Double.toString(getConvertedDoubleValue(getConvertedIntValue(result.getMaxRange()))));
        } else {
            Double value = result.getMaxRange() - minValue;
            slider.setMax(value.intValue());
            int sliderCountValue = slider.getProgress() + minValue.intValue();
            sliderCount.setText(String.format(Locale.GERMANY, "%d", sliderCountValue));

            startCount.setText(String.format(Locale.GERMANY, "%d", minValue.intValue()));

            Double maxValue = result.getMaxRange();
            endCount.setText(String.format(Locale.GERMANY, "%d", maxValue.intValue()));
        }

        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                answerSelected = true;
                if (!isInteger) {
                    sliderCount.setText(formatter.format(getConvertedDoubleValue(progress) + minValue));
                } else {
                    sliderCount.setText(String.format(Locale.GERMANY, "%d", progress + minValue.intValue()));
                }
            }

            public void onStartTrackingTouch(SeekBar seekBar) {}

            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        rightAnswer = Double.toString(result.getRightNumber());
        answerCorrect = result.getAnswerCorrect();
        answerWrong = result.getAnswerWrong();
        score = result.getScore();
    }

    public void continueToNextView(View view) {
        String userAnswer;
        if (!isInteger) {
            userAnswer = Double.toString(getConvertedDoubleValue(slider.getProgress() + getConvertedIntValue(minValue)));
        } else {
            userAnswer = Integer.toString(slider.getProgress() + minValue.intValue());
        }

        if (answerSelected)  {
            finish();
            Intent intent = new Intent(mContext, PointsActivity.class);
            intent.putExtra("isSlider", "true");
            intent.putExtra("range", Double.toString(range));
            intent.putExtra("userAnswer", userAnswer);
            intent.putExtra("solution", rightAnswer);
            intent.putExtra("answerCorrect", answerCorrect);
            intent.putExtra("answerWrong", answerWrong);
            intent.putExtra("score", Integer.toString(score));
            intent.putExtra("toleranceRange", Integer.toString(toleranceRange));
            intent.putExtra("chronologyNumber", Integer.toString(chronologyNumber));
            intent.putExtra("stationName", stationName);

            intent.putExtra("answerPicture", answerPicture);

            intent.putExtra("taskID", Integer.toString(taskID));
            startActivity(intent);
        } else {
            Animation shake = AnimationUtils.loadAnimation(SliderActivity.this, R.anim.shake);
            findViewById(R.id.continueButton).startAnimation(shake);

            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
        }
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
                EndTourDialog alertEnd = new EndTourDialog(mContext, selectedTour);
                alertEnd.showDialog((Activity) mContext);
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showEndTourDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public void showMenu(View view){
        topDarkActionbar.showMenu();
    }

    public void showStats(View view){
        topDarkActionbar.showStats(currentScore, startTime);
    }

    public void quitTour(View view){
        showEndTourDialog();
    }

}
