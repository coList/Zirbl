package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Field;

import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTourChronology;
import hsaugsburg.zirbl001.Datamanagement.TourChronologyTask;
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.R;

public class PointsActivity extends AppCompatActivity implements TourActivity{
    private Context mContext = PointsActivity.this;


    private int chronologyNumber;
    private int currentScore;
    private int selectedTour;
    private String stationName;

    public static final String TOUR_VALUES = "tourValuesFile";
    private int totalChronologyValue;

    private ChronologyModel nextChronologyItem = new ChronologyModel();

    private LoadTourChronology loadTourChronology;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);


        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));
        currentScore = Integer.parseInt(getIntent().getStringExtra("currentscore"));


        //get global tour values
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));

        stationName = getIntent().getStringExtra("stationName");

        String solution = getIntent().getStringExtra("solution");
        String userAnswer = getIntent().getStringExtra("userAnswer");
        String answerCorrect = getIntent().getStringExtra("answerCorrect");
        String answerWrong = getIntent().getStringExtra("answerWrong");
        int score = Integer.parseInt(getIntent().getStringExtra("score"));

        TextView answerText = (TextView)findViewById(R.id.answerText);
        ImageView answerImage = (ImageView)findViewById(R.id.pointsImage);
        TextView scoreText = (TextView) findViewById(R.id.points);

        String correct = "RICHTIG";
        String wrong = "FALSCH";
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);


        if (getIntent().getStringExtra("isSlider").equals("true")) {  //was the task a slider-Task?
            double userInput = Double.valueOf(userAnswer);
            double rightAnswer = Double.valueOf(solution);

            Double range = Double.parseDouble(getIntent().getStringExtra("range"));
            if (userInput >= rightAnswer - 0.05 * range &&
                    userInput <= rightAnswer + 0.05 * range) {
                answerText.setText(fromHtml(answerCorrect));
                answerImage.setImageResource(R.drawable.img_right);
                currentScore += score;
                scoreText.setText(Integer.toString(score));
                getSupportActionBar().setTitle(correct);
            } else {
                answerText.setText(fromHtml(answerWrong));
                answerImage.setImageResource(R.drawable.img_wrong);
                getSupportActionBar().setTitle(wrong);
            }
        } else { //if not:
            if (userAnswer.toUpperCase().equals(solution.toUpperCase())) {
                answerText.setText(fromHtml(answerCorrect));
                answerImage.setImageResource(R.drawable.img_right);
                currentScore += score;
                scoreText.setText(Integer.toString(score));
                getSupportActionBar().setTitle(correct);

            } else {
                answerText.setText(fromHtml(answerWrong));
                answerImage.setImageResource(R.drawable.img_wrong);
                getSupportActionBar().setTitle(wrong);
            }
        }

        TextView actionbarText = null;
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            actionbarText = (TextView) f.get(toolbar);
            actionbarText.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf"));
            actionbarText.setAllCaps(true);
            actionbarText.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            actionbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        } catch (NoSuchFieldException e) {
        }
        catch (IllegalAccessException e) {
        }


        loadTourChronology = new LoadTourChronology(this, this, nextChronologyItem, selectedTour, chronologyNumber, currentScore);

        loadTourChronology.readChronologyFile();

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

    }

    public void continueToNextView(View view) {
        loadTourChronology.continueToNextView();
    }

    public void backToNavigationActivity(View view) {
        Intent intent = new Intent(mContext, NavigationActivity.class);
        startActivity(intent);
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

    public String getStationName() {
        return stationName;
    }
}
