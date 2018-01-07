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
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
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
import hsaugsburg.zirbl001.Models.TourModels.PictureCountdownModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.TopDarkActionbar;
import hsaugsburg.zirbl001.Utils.UniversalImageLoader;

public class PictureCountdownActivity extends AppCompatActivity {

    private Context mContext = PictureCountdownActivity.this;

    private int amountOfAnswers;
    private int selectedAnswer = -1;
    private int chronologyNumber;
    private int selectedTour;
    private int score;
    private int currentScore;

    private long startTime;

    private String stationName;
    private String rightAnswer;
    private String answerCorrect;
    private String answerWrong;
    String serverName;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    public static final String TOUR_VALUES = "tourValuesFile";

    private TopDarkActionbar topDarkActionbar;

    private ImageView[] fields = new ImageView[88];
    private int[] sequence = new int[]{
            81,12,53,33,87,19,67,4,10,68,84,16,32,47,43,77,23,54,56,64,72,0,14,74,39,79,20,69,7,1,31,76,44,6,13,45,80,73,42,8,82,
            75,18,2,86,70,55,21,3,11,65,51,24,78,49,62,83,17,36,63,15,27,58,34,85,57,66,50,25,71,37,60,5,48,22,30,9,52,46,40,29,
            61,35,28,59,38,41,26};
    private int n = 0;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if(n >= 88){
                timerHandler.removeCallbacks(timerRunnable);
                ((LinearLayout) findViewById(R.id.pixelMap)).removeAllViews();
            } else {
                TextView scoreText = (TextView) findViewById(R.id.fallingPoints);
                scoreText.setText(String.format(Locale.GERMANY, "%d", score)+" Punkte");
                fields[sequence[n]].startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.fadeout));
                fields[sequence[n]].setVisibility(View.INVISIBLE);
                n++;
                score--;
                timerHandler.postDelayed(this, 500);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        ImageButton playBtn = (ImageButton) findViewById(R.id.startCountdown);
        playBtn.setOnClickListener(playPixelQuiz);
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


        TextView questionText = (TextView) findViewById(R.id.questionText);
        questionText.setText(fromHtml(result.getQuestion()));


        score = result.getScore();
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

    public void pixelatePicture(ImageView image) {
        int rowsOfPixel = 8;
        int columnsOfPixel =11;
        //Image in Bitmap umwandeln
        image.setDrawingCacheEnabled(true);
        image.buildDrawingCache(true);
        Bitmap bit = image.getDrawingCache();
        int heightLayout = bit.getHeight();
        int widthLayout = bit.getWidth();

        //Initialisierung Arrays
        TableRow[] rowPixels = new TableRow[rowsOfPixel];
        ImageView[][] colorField = new ImageView[rowsOfPixel][columnsOfPixel];

        int[] yCoordinate = new int[rowsOfPixel];
        int[] xCoordinate = new int[columnsOfPixel];

        int[][] pixel = new int[rowsOfPixel][columnsOfPixel];
        int[][] r = new int[rowsOfPixel][columnsOfPixel];
        int[][] g = new int[rowsOfPixel][columnsOfPixel];
        int[][] b = new int[rowsOfPixel][columnsOfPixel];

        //Layout Params
        LinearLayout pixelMap = (LinearLayout) findViewById(R.id.pixelMap);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 0);
        rowParams.weight = 1;
        TableRow.LayoutParams pixelParams = new TableRow.LayoutParams(
                0, ViewGroup.LayoutParams.MATCH_PARENT);
        pixelParams.weight = 1;

        //Gitterlayout erstellen
        for (int i = 0; i < rowsOfPixel; i++) {
            rowPixels[i] = new TableRow(mContext);
            pixelMap.addView(rowPixels[i], rowParams);
            for (int k = 0; k < columnsOfPixel; k++) {
                colorField[i][k] = new ImageView(mContext);
                rowPixels[i].addView(colorField[i][k], pixelParams);
            }
        }

        //Pixel Koordinaten finden
        int denom = 1;
        for (int i = 0; i < rowsOfPixel; i++) {
            yCoordinate[i] = ((heightLayout / (rowsOfPixel * 2)) * denom);
            denom += 2;
        }

        denom = 1;
        for (int i = 0; i < columnsOfPixel; i++) {
            xCoordinate[i] = ((widthLayout / (columnsOfPixel * 2)) * denom);
            denom += 2;
        }

        //Farben zuordnen
        int n = 0;
        for (int i = 0; i < rowsOfPixel; i++) {
            for (int k = 0; k < columnsOfPixel; k++) {
                pixel[i][k] = bit.getPixel(xCoordinate[k], yCoordinate[i]);
                r[i][k] = Color.red((pixel[i][k]));
                g[i][k] = Color.green((pixel[i][k]));
                b[i][k] = Color.blue((pixel[i][k]));
                colorField[i][k].setBackgroundColor(Color.rgb(r[i][k], g[i][k], b[i][k]));
                fields[n] = colorField[i][k];
                n++;
            }
        }
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
    View.OnClickListener playPixelQuiz = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            timerHandler.postDelayed(timerRunnable, 0);
            ImageView play = (ImageView) findViewById(R.id.greyplay);
            ImageView image = (ImageView)findViewById(R.id.imgPixel);
            ImageButton startCountdown = (ImageButton) findViewById(R.id.startCountdown);
            pixelatePicture(image);
            play.setVisibility(View.GONE);
            startCountdown.setVisibility(View.GONE);
        }
    };
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