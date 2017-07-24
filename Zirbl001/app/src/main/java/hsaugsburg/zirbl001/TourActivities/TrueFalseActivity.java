package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTrueFalse;
import hsaugsburg.zirbl001.Models.TourModels.TrueFalseModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.TopDarkActionbar;
import hsaugsburg.zirbl001.Utils.UniversalImageLoader;

public class TrueFalseActivity extends AppCompatActivity {
    private Context mContext = TrueFalseActivity.this;

    private boolean answerSelected = false;
    private boolean trueSelected;
    private String rightAnswer;
    private String answerCorrect;
    private String answerWrong;
    private int score;
    private int chronologyNumber;
    private int selectedTour;
    private String stationName;

    public static final String TOUR_VALUES = "tourValuesFile";
    private int currentScore;
    private long startTime;


    //dot menu
    private TopDarkActionbar topDarkActionbar;


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_true_false);

        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));

        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        int totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));
        startTime = Long.parseLong(tourValues.getString("startTime", null));
        currentScore = Integer.parseInt(tourValues.getString("currentScore", null));

        stationName = getIntent().getStringExtra("stationName");
        String titleText;
        if (stationName != null && !stationName.isEmpty()) {
            titleText = stationName.toUpperCase();
        } else {
            titleText = "START";
        }
        topDarkActionbar = new TopDarkActionbar(this, titleText);

        //Selection
        Button buttonTruth = (Button) findViewById(R.id.truth);
        buttonTruth.setOnClickListener(answerTruth);
        Button buttonLie = (Button) findViewById(R.id.lie);
        buttonLie.setOnClickListener(answerLie);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

        initImageLoader();

        setDataView();
    }

    public void setDataView() {
        int taskID = Integer.parseInt(getIntent().getStringExtra("taskid"));
        TrueFalseModel result = new LoadTrueFalse(this, selectedTour, taskID).readFile();


        TextView question = (TextView) findViewById(R.id.questionText);
        question.setText(fromHtml(result.getQuestion()));
        question.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));

        rightAnswer = String.valueOf(result.isTrue());
        answerCorrect = result.getAnswerCorrect();
        answerWrong = result.getAnswerWrong();
        score = result.getScore();

        ImageView questionPicture = (ImageView)findViewById(R.id.behindQuestionImage);

        File zirblImages = getDir("zirblImages", Context.MODE_PRIVATE);
        String[] parts = result.getPicturePath().split("\\.");
        String imgPath = selectedTour + "taskid" + taskID + "." + parts[parts.length - 1];
        File imgFile = new File(zirblImages , imgPath);
        String decodedImgUri = Uri.fromFile(imgFile).toString();
        ImageLoader.getInstance().displayImage(decodedImgUri, questionPicture);
    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public void continueToNextView(View view) {
        if (answerSelected) {
            String userAnswer;
            if (trueSelected) {
                userAnswer = "true";
            } else {
                userAnswer = "false";
            }

            finish();
            Intent intent = new Intent(mContext, PointsActivity.class);
            intent.putExtra("isSlider", "false");
            intent.putExtra("userAnswer", userAnswer);
            intent.putExtra("solution", rightAnswer);
            intent.putExtra("answerCorrect", answerCorrect);
            intent.putExtra("answerWrong", answerWrong);
            intent.putExtra("score", Integer.toString(score));
            intent.putExtra("chronologyNumber", Integer.toString(chronologyNumber));
            intent.putExtra("selectedTour", Integer.toString(selectedTour));
            intent.putExtra("stationName", stationName);
            startActivity(intent);
        } else {
            Animation shake = AnimationUtils.loadAnimation(TrueFalseActivity.this, R.anim.shake);
            findViewById(R.id.continueButton).startAnimation(shake);

            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
        }
    }

    View.OnClickListener answerTruth = new View.OnClickListener() {
        public void onClick(View v) {
            answerSelected = true;
            trueSelected = true;
            RelativeLayout selected = (RelativeLayout) findViewById(R.id.truthArea);
            selected.setBackgroundResource(R.color.colorTurquoise);

            RelativeLayout nonSelected = (RelativeLayout) findViewById(R.id.lieArea);
            nonSelected.setBackgroundResource(0);

            Button nonSelectedButton = (Button) findViewById(R.id.lie);

            int colorId = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
            nonSelectedButton.setTextColor(colorId);

            ImageView nonSelectedImageView = (ImageView) findViewById(R.id.iconLie);
            nonSelectedImageView.setImageResource(R.drawable.ic_lie_normal);

            ImageView invertedImg = (ImageView) findViewById(R.id.iconTruth);

            invertedImg.setImageResource(R.drawable.ic_truth_active);
            Button btA = (Button) findViewById(R.id.truth);
            btA.setTextColor(Color.WHITE);
        }
    };

    View.OnClickListener answerLie = new View.OnClickListener() {
        public void onClick(View v) {
            answerSelected = true;
            trueSelected = false;
            RelativeLayout selected = (RelativeLayout) findViewById(R.id.lieArea);
            selected.setBackgroundResource(R.color.colorRed);

            RelativeLayout nonSelected = (RelativeLayout) findViewById(R.id.truthArea);
            nonSelected.setBackgroundResource(0);

            Button nonSelectedButton = (Button) findViewById(R.id.truth);
            int colorId = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
            nonSelectedButton.setTextColor(colorId);

            ImageView nonSelectedImageView = (ImageView) findViewById(R.id.iconTruth);
            nonSelectedImageView.setImageResource(R.drawable.ic_truth_normal);

            ImageView invertedImg = (ImageView) findViewById(R.id.iconLie);

            invertedImg.setImageResource(R.drawable.ic_lie_active);
            Button btA = (Button) findViewById(R.id.lie);
            btA.setTextColor(Color.WHITE);
        }
    };

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
