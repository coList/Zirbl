package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.lang.reflect.Field;

import hsaugsburg.zirbl001.Datamanagement.JSONLetters;
import hsaugsburg.zirbl001.Datamanagement.JSONTrueFalse;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTrueFalse;
import hsaugsburg.zirbl001.Models.TrueFalseModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.UniversalImageLoader;

public class TrueFalseActivity extends AppCompatActivity {

    private Context mContext = TrueFalseActivity.this;
    private static final String TAG = "TrueFalseActivity";

    private boolean answerSelected = false;
    private boolean trueSelected;
    private String rightAnswer;
    private String answerCorrect;
    private String answerWrong;
    private int score;
    private int chronologyNumber;
    private int selectedTour;
    private String stationName;

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
        setContentView(R.layout.activity_true_false);

        //dot menu

        title = (TextView) findViewById(R.id.titleActionbar);
        dotMenuLayout=(RelativeLayout) this.findViewById(R.id.dotMenu);
        dotMenuLayout.setVisibility(RelativeLayout.GONE);

        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));
        //selectedTour = Integer.parseInt(getIntent().getStringExtra("selectedTour"));

        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));

        stationName = getIntent().getStringExtra("stationName");

        if (stationName != null && !stationName.isEmpty()) {
            title.setText(stationName.toUpperCase());
        } else {
            title.setText("START");
        }

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        //new JSONTrueFalse(this, selectedTour, taskID).execute(serverName + "/api/selectTrueFalseView.php");

        //Selection
        Button buttonTruth = (Button) findViewById(R.id.truth);
        buttonTruth.setOnClickListener(answerTruth);
        Button buttonLie = (Button) findViewById(R.id.lie);
        buttonLie.setOnClickListener(answerLie);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);


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

        String imageURL = result.getPicturePath();
        ImageView questionPicture = (ImageView)findViewById(R.id.behindQuestionImage);

        File zirblImages = getDir("zirblImages", Context.MODE_PRIVATE);
        File f=new File(zirblImages , selectedTour + "taskid" + taskID + ".jpg");
        final String uri = Uri.fromFile(f).toString();
        ImageLoader.getInstance().displayImage(uri, questionPicture);

    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public void continueToNextView(View view) {
        if (answerSelected) {
            String userAnswer;
            if (trueSelected == true) {
                userAnswer = "true";
            } else {
                userAnswer = "false";
            }

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

    //Selection
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
    //


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




    public void processData(TrueFalseModel result) {
        TextView question = (TextView) findViewById(R.id.questionText);
        question.setText(fromHtml(result.getQuestion()));
        question.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));

        rightAnswer = String.valueOf(result.isTrue());
        answerCorrect = result.getAnswerCorrect();
        answerWrong = result.getAnswerWrong();
        score = result.getScore();

        String imageURL = result.getPicturePath();
        ImageView questionPicture = (ImageView)findViewById(R.id.behindQuestionImage);

        ImageLoader.getInstance().displayImage(serverName + imageURL, questionPicture);

    }
}
