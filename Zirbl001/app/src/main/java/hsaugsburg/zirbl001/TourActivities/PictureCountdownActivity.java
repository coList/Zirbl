package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Random;

import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadPictureCountdown;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadQuiz;
import hsaugsburg.zirbl001.Models.TourModels.PictureCountdownModel;
import hsaugsburg.zirbl001.Models.TourModels.QuizModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.TopDarkActionbar;
import hsaugsburg.zirbl001.Utils.UniversalImageLoader;

public class PictureCountdownActivity extends AppCompatActivity {

    private Context mContext = PictureCountdownActivity.this;

    private int amountOfAnswers;
    private int selectedAnswer = -1;

    private int chronologyNumber;
    private int selectedTour;
    private String stationName;
    private String rightAnswer;
    private String answerCorrect;
    private String answerWrong;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;

    public static final String TOUR_VALUES = "tourValuesFile";
    private int currentScore;
    private long startTime;

    private TopDarkActionbar topDarkActionbar;

    // Parameter Pixel Rätsel
    public int timeBetweenPixelChange = 500;
    private int linesOfPixel = 9;
    private int score = 0;

    TableRow[] rowPixels;
    LinearLayout[][] colorField;
    int[][] r;
    int[][] g;
    int[][] b;

    LinearLayout mContainerView;
    Bitmap originalBitmap;

    // Timer Durchlauf
    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if(score<10){
                score = 10;
                timerHandler.removeCallbacks(timerRunnable);
                ((LinearLayout) findViewById(R.id.pixelMap)).removeAllViews();
            } else {
                TextView scoreText = (TextView) findViewById(R.id.fallingPoints);
                scoreText.setText(String.format(Locale.GERMANY, "%d", score)+" Punkte");
                int n = new Random().nextInt(rowPixels.length);
                int m = new Random().nextInt(colorField[n].length);
                if(colorField[n][m].getVisibility() != View.INVISIBLE){
                    colorField[n][m].startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fadeout));
                    colorField[n][m].setVisibility(View.INVISIBLE);
                    score--;
                    timerHandler.postDelayed(this, timeBetweenPixelChange);
                } else {
                    timerHandler.postDelayed(this, 0);
                }

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_picture_countdown);


        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));

        //get global tour values
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

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        //Selection
        Button buttonA = (Button) findViewById(R.id.answer1);
        buttonA.setOnClickListener(answerA);
        Button buttonB = (Button) findViewById(R.id.answer2);
        buttonB.setOnClickListener(answerB);
        Button buttonC = (Button) findViewById(R.id.answer3);
        buttonC.setOnClickListener(answerC);


        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

        initImageLoader();
        setDataView();

        final ImageButton startCountdown = (ImageButton) findViewById(R.id.startCountdown);
        startCountdown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timerHandler.postDelayed(timerRunnable, 0);
                int linearLayout = getResources().getIdentifier("pixelMap", "id", getPackageName());
                pixelatePicture(linesOfPixel, linearLayout);
                ImageView play = (ImageView) findViewById(R.id.greyplay);
                play.setVisibility(View.GONE);
                startCountdown.setVisibility(View.GONE);
            }
        });


    }

    public void pixelatePicture(int n ,int linearLayout) {
        if (((LinearLayout) findViewById(linearLayout)).getChildCount() > 0) {
            ((LinearLayout) findViewById(linearLayout)).removeAllViews();
        }
        ImageView image = (ImageView) findViewById(R.id.imgPixel);
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache(true);

        Bitmap bit = image.getDrawingCache();
        int heightLayout = bit.getHeight();
        int widthLayout = bit.getWidth();
        Log.d("C","height"+heightLayout+"width"+widthLayout);

        int m = widthLayout / (heightLayout / n);

        rowPixels = new TableRow[n];
        colorField = new LinearLayout[n][m];

        int[] xCoordinate = new int[m];
        int[] yCoordinate = new int[n];
        int[][] pixel = new int[n][m];
        r = new int[n][m];
        g = new int[n][m];
        b = new int[n][m];

        LinearLayout pixelMap = (LinearLayout) findViewById(linearLayout);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0);
        rowParams.weight = 1;
        TableRow.LayoutParams pixelParams = new TableRow.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT);
        pixelParams.weight = 1;

        for (int i = 0; i < n; i++) {
            rowPixels[i] = new TableRow((getApplicationContext()));
            pixelMap.addView(rowPixels[i], rowParams);
            for (int k = 0; k < m; k++) {
                colorField[i][k] = new LinearLayout(getApplicationContext());
                rowPixels[i].addView(colorField[i][k], pixelParams);
            }
        }

        int denom = 1;
        for (int i = 0; i < n; i++) {
            yCoordinate[i] = ((heightLayout / (n * 2)) * denom);
            denom += 2;
        }

        denom = 1;
        for (int i = 0; i < m; i++) {
            xCoordinate[i] = ((widthLayout / (m * 2)) * denom);
            denom += 2;
        }

        for (int i = 0; i < n; i++) {
            for (int k = 0; k < m; k++) {
                pixel[i][k] = bit.getPixel(xCoordinate[k], yCoordinate[i]);
                r[i][k] = Color.red((pixel[i][k]));
                g[i][k] = Color.green((pixel[i][k]));
                b[i][k] = Color.blue((pixel[i][k]));
                colorField[i][k].setBackgroundColor(Color.rgb(r[i][k], g[i][k], b[i][k]));
            }
        }

        score = (m * n)+10;

    }

    public void setDataView() {
        int taskID = Integer.parseInt(getIntent().getStringExtra("taskid"));
        PictureCountdownModel result = new LoadPictureCountdown(this, selectedTour, taskID).readFile();

        ArrayList<String> answers = new ArrayList<>();


        ImageView questionPicture = (ImageView) findViewById(R.id.imgPixel);
        File zirblImages = getDir("zirblImages", Context.MODE_PRIVATE);
        String[] parts = result.getPicturePath().split("\\.");
        String imgPath = selectedTour + "taskid" + taskID + "." + parts[parts.length - 1];
        File imgFile = new File(zirblImages, imgPath);
        String decodedImgUri = Uri.fromFile(imgFile).toString();
        ImageLoader.getInstance().displayImage(decodedImgUri, questionPicture);
        //int linearLayout = getResources().getIdentifier("pixelMap", "id", getPackageName());
        //pixelatePicture(linesOfPixel, linearLayout);


        TextView questionText = (TextView) findViewById(R.id.questionText);
        questionText.setText(fromHtml(result.getQuestion()));
        //questionText.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));


        //score = result.getScore();
        String points = " Punkte";
        TextView scoreText = (TextView) findViewById(R.id.fallingPoints);
        scoreText.setText(String.format(Locale.GERMANY, "%d", score)+points);

        answers.addAll(Arrays.asList(result.getRightAnswer(), result.getOption2(), result.getOption3()));

        amountOfAnswers = answers.size();
        answers = shuffleArray(answers);

        for (int i = 0; i < answers.size(); i++) {
            String name = "answer" + (i + 1);
            int id = getResources().getIdentifier(name, "id", getPackageName());
            TextView answer = (TextView) findViewById(id);
            answer.setText(answers.get(i));
        }

        rightAnswer = result.getRightAnswer();
        answerCorrect = result.getAnswerCorrect();
        answerWrong = result.getAnswerWrong();

    }

    private void initImageLoader() {
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public void continueToNextView(View view) {
        if (selectedAnswer >= 1) {
            String name = "answer" + selectedAnswer;
            int id = getResources().getIdentifier(name, "id", getPackageName());
            Button selectedButton = (Button) findViewById(id);
            String userAnswer = selectedButton.getText().toString();

            finish();
            Intent intent = new Intent(mContext, PointsActivity.class);
            intent.putExtra("isSlider", "false");
            intent.putExtra("userAnswer", userAnswer);
            intent.putExtra("solution", rightAnswer);
            intent.putExtra("answerCorrect", answerCorrect);
            intent.putExtra("answerWrong", answerWrong);
            intent.putExtra("score", Integer.toString(score));
            intent.putExtra("chronologyNumber", Integer.toString(chronologyNumber));
            intent.putExtra("stationName", stationName);
            startActivity(intent);
        } else {
            Animation shake = AnimationUtils.loadAnimation(PictureCountdownActivity.this, R.anim.shake);
            findViewById(R.id.continueButton).startAnimation(shake);

            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
        }
    }

    //Selection
    View.OnClickListener answerA = new View.OnClickListener() {
        public void onClick(View v) {
            selectedAnswer = 1;
            resetAnswers();

            RelativeLayout selected = (RelativeLayout) findViewById(R.id.area1);
            selected.setBackgroundResource(R.color.colorTurquoise);

            ImageView invertedImg = (ImageView) findViewById(R.id.imgLetter1);
            invertedImg.setImageResource(R.drawable.ic_1_active);
            Button btA = (Button) findViewById(R.id.answer1);
            btA.setTextColor(Color.WHITE);
        }
    };
    View.OnClickListener answerB = new View.OnClickListener() {
        public void onClick(View v) {
            selectedAnswer = 2;
            resetAnswers();

            RelativeLayout selected = (RelativeLayout) findViewById(R.id.area2);
            selected.setBackgroundResource(R.color.colorTurquoise);
            ImageView invertedImg = (ImageView) findViewById(R.id.imgLetter2);
            invertedImg.setImageResource(R.drawable.ic_2_active);
            Button btA = (Button) findViewById(R.id.answer2);
            btA.setTextColor(Color.WHITE);

        }
    };
    View.OnClickListener answerC = new View.OnClickListener() {
        public void onClick(View v) {
            selectedAnswer = 3;
            resetAnswers();

            RelativeLayout selected = (RelativeLayout) findViewById(R.id.area3);
            selected.setBackgroundResource(R.color.colorTurquoise);

            ImageView invertedImg = (ImageView) findViewById(R.id.imgLetter3);
            invertedImg.setImageResource(R.drawable.ic_3_active);
            Button btA = (Button) findViewById(R.id.answer3);
            btA.setTextColor(Color.WHITE);
        }
    };

    public ArrayList<String> shuffleArray(ArrayList<String> list) {
        Random random = new Random();

        for (int i = list.size() - 1; i > 1; i--) {
            int numberToSwapWith = random.nextInt(i);
            String tmp = list.get(numberToSwapWith);
            list.set(numberToSwapWith, list.get(i));
            list.set(i, tmp);
        }

        return list;
    }

    //unselect all answers
    private void resetAnswers() {
        for (int i = 0; i < amountOfAnswers; i++) {
            String nameRelativeLayout = "area" + (i + 1);
            int relativeLayoutID = getResources().getIdentifier(nameRelativeLayout, "id", getPackageName());
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(relativeLayoutID);
            relativeLayout.setBackgroundResource(0);

            String nameImageView = "imgLetter" + (i + 1);
            int imageViewID = getResources().getIdentifier(nameImageView, "id", getPackageName());
            ImageView imageView = (ImageView) findViewById(imageViewID);
            String nameDrawable = "ic_" + (i + 1) + "_normal";
            int imageDrawable = getResources().getIdentifier(nameDrawable, "drawable", getPackageName());
            imageView.setImageResource(imageDrawable);

            String nameButton = "answer" + (i + 1);
            int buttonID = getResources().getIdentifier(nameButton, "id", getPackageName());
            Button button = (Button) findViewById(buttonID);
            int colorId = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
            button.setTextColor(colorId);
        }
    }

    private void showEndTourDialog() {
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
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public void showMenu(View view) {
        topDarkActionbar.showMenu();
    }

    public void showStats(View view) {
        topDarkActionbar.showStats(currentScore, startTime);
    }

    public void quitTour(View view) {
        showEndTourDialog();
    }
}
