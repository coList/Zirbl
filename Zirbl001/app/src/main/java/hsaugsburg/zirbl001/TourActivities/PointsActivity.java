package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTourChronology;
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.TourActivities.Navigation.NavigationActivity;

public class PointsActivity extends AppCompatActivity implements TourActivity{
    private Context mContext = PointsActivity.this;
    private static final String TAG = "PointsActivity";


    private int chronologyNumber;
    private int currentScore;
    private int selectedTour;
    private String stationName;

    public static final String TOUR_VALUES = "tourValuesFile";
    private int totalChronologyValue;

    private ChronologyModel nextChronologyItem = new ChronologyModel();

    private LoadTourChronology loadTourChronology;


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
        setContentView(R.layout.activity_points);

        //dot menu
        title = (TextView) findViewById(R.id.titleActionbar);
        dotMenuLayout=(RelativeLayout) this.findViewById(R.id.dotMenu);
        dotMenuLayout.setVisibility(RelativeLayout.GONE);

        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));

        //get global tour values
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        currentScore = Integer.parseInt(tourValues.getString("currentScore", null));
        totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));

        stationName = getIntent().getStringExtra("stationName");

        String solution = getIntent().getStringExtra("solution");
        String userAnswer = getIntent().getStringExtra("userAnswer");
        String answerCorrect = getIntent().getStringExtra("answerCorrect");
        String answerWrong = getIntent().getStringExtra("answerWrong");
        int stringLengthC = answerCorrect.length();
        int stringLengthW = answerWrong.length();
        int score = Integer.parseInt(getIntent().getStringExtra("score"));

        TextView answerText = (TextView)findViewById(R.id.answerText);
        ImageView answerImage = (ImageView)findViewById(R.id.pointsImage);
        TextView scoreText = (TextView) findViewById(R.id.points);
        ImageView gif = (ImageView) findViewById(R.id.gifConfetti);

        String correct = "RICHTIG";
        String wrong = "FALSCH";

        if (getIntent().getStringExtra("isSlider").equals("true")) {  //was the task a slider-Task?
            double userInput = Double.valueOf(userAnswer);
            double rightAnswer = Double.valueOf(solution);

            Double range = Double.parseDouble(getIntent().getStringExtra("range"));
            int toleranceRange = Integer.parseInt(getIntent().getStringExtra("toleranceRange"));
            Log.d("PointsActivity", Integer.toString(toleranceRange));
            Log.d("PointsActivityTolerance", Double.toString(toleranceRange/100.0));
            if (userInput >= rightAnswer - (toleranceRange/100.0) * range &&
                    userInput <= rightAnswer + (toleranceRange/100.0) * range) {
                answerText.setText(fromHtml(answerCorrect));
                answerImage.setImageResource(R.drawable.img_right_without_confetti);
                gif.setImageResource(R.drawable.confetti_right);
                currentScore += score;
                scoreText.setText(Integer.toString(score));
                title.setText(correct);
            } else {
                answerText.setText(fromHtml(answerWrong));
                answerImage.setImageResource(R.drawable.img_wrong_without_confetti);
                gif.setImageResource(R.drawable.confetti_wrong);
                title.setText(wrong);
            }
        } else { //if not:
            if (userAnswer.toUpperCase().equals(solution.toUpperCase())) {
                answerText.setText(fromHtml(answerCorrect));
                answerImage.setImageResource(R.drawable.img_right_without_confetti);
                gif.setImageResource(R.drawable.confetti_right);
                currentScore += score;
                scoreText.setText(Integer.toString(score));
                title.setText(correct);

            } else {
                answerText.setText(fromHtml(answerWrong));
                answerImage.setImageResource(R.drawable.img_wrong_without_confetti);
                gif.setImageResource(R.drawable.confetti_wrong);
                title.setText(wrong);
            }
        }

        //Scroll View State Change
        ConstraintLayout pointsArea = (ConstraintLayout) findViewById(R.id.pointsArea);
        LinearLayout continueArea = (LinearLayout) findViewById(R.id.continueArea);
        RelativeLayout.LayoutParams paramsContinue = (RelativeLayout.LayoutParams) continueArea.getLayoutParams();
        RelativeLayout.LayoutParams paramsPoints = (RelativeLayout.LayoutParams) pointsArea.getLayoutParams();
        RelativeLayout.LayoutParams paramsText = (RelativeLayout.LayoutParams) answerText.getLayoutParams();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        Log.d(TAG, "Höhe: " + height);
        Log.d(TAG, Double.toString(((double)stringLengthC)/height));
        Log.d(TAG, Double.toString(((double)stringLengthW)/height));
        if(((double)stringLengthC)/height <= 0.16 || ((double)stringLengthW)/height <= 0.16) {
            paramsContinue.addRule(RelativeLayout.BELOW, 0);
            paramsContinue.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);

            paramsText.addRule(RelativeLayout.BELOW, 0);
            paramsText.addRule(RelativeLayout.ABOVE, R.id.continueArea);

            paramsPoints.addRule(RelativeLayout.ABOVE, R.id.answerText);

            continueArea.setLayoutParams(paramsContinue);
            pointsArea.setLayoutParams(paramsPoints);
            answerText.setLayoutParams(paramsText);
        } else {
            paramsPoints.addRule(RelativeLayout.ABOVE, 0);

            paramsText.addRule(RelativeLayout.ABOVE, 0);
            paramsText.addRule(RelativeLayout.BELOW, R.id.pointsArea);

            paramsContinue.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            paramsContinue.addRule(RelativeLayout.BELOW, R.id.answerText);

            continueArea.setLayoutParams(paramsContinue);
            pointsArea.setLayoutParams(paramsPoints);
            answerText.setLayoutParams(paramsText);
        }
        //


        loadTourChronology = new LoadTourChronology(this, this, nextChronologyItem, selectedTour, chronologyNumber);

        loadTourChronology.readChronologyFile();

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

        SharedPreferences.Editor editor = tourValues.edit();
        editor.putString("currentScore", Integer.toString(currentScore));
        editor.commit();

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
}
