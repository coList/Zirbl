package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import java.lang.reflect.Field;
import java.util.Locale;

import hsaugsburg.zirbl001.Datamanagement.JSONSlider;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadSlider;
import hsaugsburg.zirbl001.Models.SliderModel;
import hsaugsburg.zirbl001.R;

public class SliderActivity extends AppCompatActivity {

    private Context mContext = SliderActivity.this;
    private static final String TAG = "SliderActivity";

    private  SeekBar slider;
    private  TextView sliderCount;
    private static Double minValue;
    private static int sliderMax = 2000;

    private int chronologyNumber;
    private int selectedTour;
    private String stationName;

    private boolean answerSelected;

    private Double range;
    private boolean isInteger;
    private String userAnswer;
    private String rightAnswer;
    private String answerCorrect;
    private String answerWrong;
    private int score;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;

    public static final String TOUR_VALUES = "tourValuesFile";
    private int totalChronologyValue;


    //dot menu
    private TextView title;
    private RelativeLayout dotMenuLayout;
    private boolean dotMenuOpen = false;


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_slider);
        //dot menu

        title = (TextView) findViewById(R.id.titleActionbar);
        dotMenuLayout=(RelativeLayout) this.findViewById(R.id.dotMenu);
        dotMenuLayout.setVisibility(RelativeLayout.GONE);

        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));

        //get global tour values
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));
        findViewById(R.id.slider).setPadding(40,0,40,0);

        stationName = getIntent().getStringExtra("stationName");


        if (stationName != null && !stationName.isEmpty()) {
            title.setText(stationName.toUpperCase());
        } else {
            title.setText("START");
        }

        slider = (SeekBar) findViewById(R.id.slider);

        TextView count = (TextView) findViewById(R.id.sliderCount);
        count.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);

        slider.getProgressDrawable().setColorFilter(
                ContextCompat.getColor(mContext, R.color.colorTurquoise), android.graphics.PorterDuff.Mode.SRC_IN);

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        //new JSONSlider(this, selectedTour, taskID).execute(serverName + "/api/selectGuessTheNumberView.php");

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

        setDataView();
    }

    public void setDataView() {
        int taskID = Integer.parseInt(getIntent().getStringExtra("taskid"));
        SliderModel result = new LoadSlider(this, selectedTour, taskID).readFile();

        TextView question = (TextView) findViewById(R.id.questionText);
        question.setText(fromHtml(result.getQuestion()));
        isInteger = result.getIsInteger();
        minValue = result.getMinRange();
        range = result.getMaxRange() - minValue;

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
            sliderCount.setText(Integer.toString(slider.getProgress() + minValue.intValue()));

            startCount.setText(Integer.toString(getConvertedIntValue(minValue)));

            endCount.setText(Integer.toString(getConvertedIntValue(result.getMaxRange())));
        }



        slider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                answerSelected = true;
                if (!isInteger) {
                    //Double value = getConvertedDoubleValue(progress) + minValue;
                    //sliderCount.setText(String.format(Locale.GERMAN, "%,d", value));
                    sliderCount.setText(Double.toString(getConvertedDoubleValue(progress) + minValue));
                } else {
                    //Integer value = progress + minValue.intValue();
                    //sliderCount.setText(String.format(Locale.GERMAN, "%,d", value));
                    sliderCount.setText(Integer.toString(progress + minValue.intValue()));

                }

                seekBar.getProgressDrawable().setColorFilter(
                        ContextCompat.getColor(mContext, R.color.colorTurquoise), android.graphics.PorterDuff.Mode.SRC_IN);

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

    public void continueToNextView(View view) {
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
            intent.putExtra("chronologyNumber", Integer.toString(chronologyNumber));
            intent.putExtra("stationName", stationName);
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

    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public void showMenu(View view){

        ImageView dotIcon = (ImageView) findViewById(R.id.dotIcon);
        TextView menuStats = (TextView) findViewById(R.id.menuStats);
        TextView menuQuit = (TextView) findViewById(R.id.menuQuit);

        if(dotMenuOpen){
            dotMenuLayout.setVisibility(RelativeLayout.GONE);
            dotMenuOpen = false;
            title.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            dotIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent));
        } else {
            dotMenuLayout.setVisibility(RelativeLayout.VISIBLE);
            dotMenuOpen = true;
            title.setTextColor(ContextCompat.getColor(mContext, R.color.colorTurquoise));
            dotIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorTurquoise));
            menuQuit.setTextSize(18);
            menuStats.setTextSize(18);
        }
    }
    public void showStats(View view){
        Log.d(TAG, "showStats: Stats");
    }
    public void quitTour(View view){
        showEndTourDialog();
    }






    //can't set minValue directly -> add minValue to valueDisplayed and substract minValue from maxValue
    public void processData(SliderModel result) {
        TextView question = (TextView) findViewById(R.id.questionText);
        question.setText(fromHtml(result.getQuestion()));
        isInteger = result.getIsInteger();
        minValue = result.getMinRange();
        range = result.getMaxRange() - minValue;


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
                    //Double value = getConvertedDoubleValue(progress) + minValue;
                    //sliderCount.setText(String.format(Locale.GERMAN, "%,d", value));
                    sliderCount.setText(Double.toString(getConvertedDoubleValue(progress) + minValue));
                } else {
                    //Integer value = progress + minValue.intValue();
                    //sliderCount.setText(String.format(Locale.GERMAN, "%,d", value));
                    sliderCount.setText(Integer.toString(progress + minValue.intValue()));

                }

                seekBar.getProgressDrawable().setColorFilter(
                        ContextCompat.getColor(mContext, R.color.colorTurquoise), android.graphics.PorterDuff.Mode.SRC_IN);

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

}
