package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.Locale;

import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTourChronology;
import hsaugsburg.zirbl001.Fonts.QuicksandBoldPrimaryView;
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.TourModels.ChronologyModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.TopDarkActionbar;

public class PointsActivity extends AppCompatActivity implements TourActivity{
    private Context mContext = PointsActivity.this;

    private int chronologyNumber;
    private int currentScore;
    private int scoreBefore;
    private int score;
    private int selectedTour;
    private int totalChronologyValue;

    private String stationName;
    public static final String TOUR_VALUES = "tourValuesFile";

    private long startTime;

    private ChronologyModel nextChronologyItem = new ChronologyModel();
    private LoadTourChronology loadTourChronology;
    private TopDarkActionbar topDarkActionbar;
    private QuicksandBoldPrimaryView totalPoints;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            scoreBefore++;
            score--;
            totalPoints.setText(String.format(Locale.GERMANY, "%d", scoreBefore));
            if(score==0){
                timerHandler.removeCallbacks(timerRunnable);
            } else {
                timerHandler.postDelayed(this, 20);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_points);

        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));

        //Get Global Tourvalues
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        currentScore = Integer.parseInt(tourValues.getString("currentScore", null));
        scoreBefore = currentScore;
        totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));
        startTime = Long.parseLong(tourValues.getString("startTime", null));
        stationName = getIntent().getStringExtra("stationName");
        String solution = getIntent().getStringExtra("solution");
        String userAnswer = getIntent().getStringExtra("userAnswer");
        String answerCorrect = getIntent().getStringExtra("answerCorrect");
        String answerWrong = getIntent().getStringExtra("answerWrong");
        String answerPicture = getIntent().getStringExtra("answerPicture");
        String correct = "RICHTIG";
        String wrong = "FALSCH";
        String titleText;
        int taskID = Integer.parseInt(getIntent().getStringExtra("taskID"));
        int stringLengthC = answerCorrect.length();
        int stringLengthW = answerWrong.length();
        score = Integer.parseInt(getIntent().getStringExtra("score"));

        Log.d("PointsActivity", answerPicture);

        TextView answerText = (TextView)findViewById(R.id.answerText);
        totalPoints = (QuicksandBoldPrimaryView) findViewById(R.id.totalPoints);

        final VideoView answerVideo = (VideoView) findViewById(R.id.pointsVideo);
        answerVideo.setZOrderOnTop(true);
        answerVideo.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                answerVideo.start();
            }
        });

        boolean hasAnswerPicture = !answerPicture.equals("null") && !answerPicture.isEmpty() && !answerPicture.equals("");
        if (hasAnswerPicture) {
            File zirblImages = getDir("zirblImages", Context.MODE_PRIVATE);
            String[] parts = answerPicture.split("\\.");
            String imgPath = selectedTour + "taskid" + taskID + "answerpicture" + "." + parts[parts.length - 1];
            File imgFile = new File(zirblImages , imgPath);
            String decodedImgUri = Uri.fromFile(imgFile).toString();
            //ImageLoader.getInstance().displayImage(decodedImgUri, answerImage);
        }

        //Wenn SliderActivity, dann...
        if (getIntent().getStringExtra("isSlider").equals("true")) {
            double userInput = Double.valueOf(userAnswer);
            double rightAnswer = Double.valueOf(solution);
            Double range = Double.parseDouble(getIntent().getStringExtra("range"));
            int toleranceRange = Integer.parseInt(getIntent().getStringExtra("toleranceRange"));
            //Wenn im Toleranzbereich des Sliders, dann...
            if (userInput >= rightAnswer - (toleranceRange/100.0) * range && userInput <= rightAnswer + (toleranceRange/100.0) * range) {
                answerText.setText(fromHtml(answerCorrect));
                titleText = correct;
                answerVideo.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.right)); //Videoquelle autauschen
                answerVideo.start();
                totalPoints.setText(String.format(Locale.GERMANY, "%d", scoreBefore));
                timerHandler.postDelayed(timerRunnable, 1200);
                currentScore += score;
            //Wenn AUSSERHALB des Toleranzbereichs des Slider, dann...
            } else {
                answerText.setText(fromHtml(answerWrong));
                titleText = wrong;
                answerVideo.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.wrong)); //Videoquelle autauschen
                answerVideo.start();
                totalPoints.setText(String.format(Locale.GERMANY, "%d", scoreBefore));
            }
        //Wenn KEINE SliderActivity, dann...
        } else {
            //Wenn richtige Antwort, dann...
            if (userAnswer.toUpperCase().equals(solution.toUpperCase())) {
                answerText.setText(fromHtml(answerCorrect));
                titleText = correct;
                answerVideo.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.right)); //Videoquelle autauschen
                answerVideo.start();
                totalPoints.setText(String.format(Locale.GERMANY, "%d", scoreBefore));
                timerHandler.postDelayed(timerRunnable, 1200);
                currentScore += score;
            //Wenn faslche Antwort, dann...
            } else {
                answerText.setText(fromHtml(answerWrong));
                titleText = wrong;
                answerVideo.setVideoURI(Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.wrong)); //Videoquelle autauschen
                answerVideo.start();
                totalPoints.setText(String.format(Locale.GERMANY, "%d", scoreBefore));
            }
        }
        topDarkActionbar = new TopDarkActionbar(this, titleText);

        //Scroll View State Change
        RelativeLayout pointsArea = (RelativeLayout) findViewById(R.id.pointsArea);
        LinearLayout continueArea = (LinearLayout) findViewById(R.id.continueArea);

        RelativeLayout.LayoutParams paramsContinue = (RelativeLayout.LayoutParams) continueArea.getLayoutParams();
        RelativeLayout.LayoutParams paramsPoints = (RelativeLayout.LayoutParams) pointsArea.getLayoutParams();
        RelativeLayout.LayoutParams paramsText = (RelativeLayout.LayoutParams) answerText.getLayoutParams();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;

        /*if (((double)stringLengthC)/height <= 0.10 || ((double)stringLengthW)/height <= 0.10) {
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
        }*/

        loadTourChronology = new LoadTourChronology(this, this, nextChronologyItem, selectedTour, chronologyNumber);
        loadTourChronology.readChronologyFile();
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

        SharedPreferences.Editor editor = tourValues.edit();
        editor.putString("currentScore", Integer.toString(currentScore));
        editor.commit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            showEndTourDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void continueToNextView(View view) {
        loadTourChronology.continueToNextView();
    }

    private void showEndTourDialog(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                EndTourDialog alertEnd = new EndTourDialog(mContext, selectedTour);
                alertEnd.showDialog((Activity) mContext);
            }
        });
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
        topDarkActionbar.showMenu();
    }

    public void showStats(View view){
        topDarkActionbar.showStats(currentScore, startTime);
    }

    public void quitTour(View view){
        showEndTourDialog();
    }
}
