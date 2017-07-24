package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Vibrator;
import android.text.Html;
import android.text.Spanned;

import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Random;

import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadLetters;
import hsaugsburg.zirbl001.Fonts.OpenSansBoldPrimaryButton;
import hsaugsburg.zirbl001.Models.TourModels.LettersModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.TopDarkActionbar;

public class LettersActivity extends AppCompatActivity {
    private Context mContext = LettersActivity.this;
    private static final String TAG = "LettersActivity";
    private int chronologyNumber;
    private int selectedTour;
    private String stationName;

    private String solution;
    private String answerCorrect;
    private String answerWrong;
    private int score;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;

    public static final String TOUR_VALUES = "tourValuesFile";

    //dot menu
    private TopDarkActionbar topDarkActionbar;
    private int currentScore;
    private long startTime;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letters);

        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));

        //get global tour values
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        int totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));
        startTime = Long.parseLong(tourValues.getString("startTime", null));
        currentScore = Integer.parseInt(tourValues.getString("currentScore", null));

        //dot menu
        stationName = getIntent().getStringExtra("stationName");
        String titleText;
        if (stationName != null && !stationName.isEmpty()) {
            titleText = stationName.toUpperCase();
        } else {
            titleText = "START";
        }

        topDarkActionbar = new TopDarkActionbar(this, titleText);

        TextView besideImg = (TextView) findViewById(R.id.besideImgQuestion);
        besideImg.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

        setDataView();
    }

    public void setDataView() {
        int taskID = Integer.parseInt(getIntent().getStringExtra("taskid"));
        LettersModel result = new LoadLetters(this, selectedTour, taskID).readFile();

        TextView question = (TextView) findViewById(R.id.questionText);
        question.setText(fromHtml(result.getQuestion()));

        TableRow tableRow = (TableRow) findViewById(R.id.inputArea);
        final int solutionLength = result.getSolution().length();
        solution = result.getSolution();
        answerCorrect = result.getAnswerCorrect();
        answerWrong = result.getAnswerWrong();
        score = result.getScore();

        StringBuilder stringBuilder = new StringBuilder(result.getSolution() + result.getOtherLetters());
        shuffleLetters(stringBuilder);
        final String letters = stringBuilder.toString().toUpperCase();

        //create "solution-buttons"
        for (int i = 0; i < solutionLength; i++) {
            final OpenSansBoldPrimaryButton button = new OpenSansBoldPrimaryButton(this);
            button.setId(i);
            float d = getResources().getDisplayMetrics().density;
            TableRow.LayoutParams params = new TableRow.LayoutParams((int)(30*d), TableRow.LayoutParams.WRAP_CONTENT);
            params.leftMargin = (int) (3 * d);
            params.rightMargin = (int) (3 * d);
            Context context = button.getContext();
            int drawableId = context.getResources().getIdentifier("img_line_below_letters", "drawable", context.getPackageName());
            button.setBackgroundResource(drawableId);

            int colorId = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
            button.setTextColor(colorId);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 26);

            //user wants to remove the old letter
            //empty the button text
            //set used letter visible again
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    boolean foundText = false;
                    for (int i = 0; i < letters.length(); i++) {
                        if (!foundText) {
                            String name = "letter" + (i + 1);
                            int id = getResources().getIdentifier(name, "id", getPackageName());
                            TextView letter = (TextView) findViewById(id);
                            if (letter.getText().toString().equals(button.getText()) && letter.getVisibility() == View.INVISIBLE) {
                                letter.setVisibility(View.VISIBLE);
                                button.setText("");
                                foundText = true;
                            }
                        }
                    }
                }
            });

            button.setText("");
            button.setLayoutParams(params);
            tableRow.addView(button);
        }

        //set letters
        for (int i = 0; i < letters.length(); i++) {
            String name = "letter" + (i + 1);
            int id = getResources().getIdentifier(name, "id", getPackageName());
            final TextView letter = (TextView) findViewById(id);
            letter.setText(String.valueOf(letters.charAt(i)));

            //Click on Letters: If empty space in solution, set invisible and set text of solutionLetter
            letter.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    String letterText = letter.getText().toString();
                    boolean foundButton = false;
                    for (int i = 0; i < solutionLength; i++) {
                        if (!foundButton) {
                            Button button = (Button) findViewById(i);
                            if (button.getText().equals("")) {
                                button.setText(letterText);
                                letter.setVisibility(View.INVISIBLE);
                                foundButton = true;
                            }
                        }
                    }
                }
            });
        }
    }

    public void continueToNextView(View view) {
        boolean answerSelected = true;
        String userAnswer = "";
        for (int i = 0; i < solution.length(); i++) {
            Button button = (Button) findViewById(i);
            userAnswer += button.getText();

            if (button.getText().equals("")) {
                answerSelected = false;
            }
        }

        if (answerSelected) {
            finish();
            Intent intent = new Intent(mContext, PointsActivity.class);
            intent.putExtra("isSlider", "false");
            intent.putExtra("userAnswer", userAnswer);
            intent.putExtra("solution", solution.toUpperCase());
            intent.putExtra("answerCorrect", answerCorrect);
            intent.putExtra("answerWrong", answerWrong);
            intent.putExtra("score", Integer.toString(score));
            intent.putExtra("chronologyNumber", Integer.toString(chronologyNumber));
            intent.putExtra("stationName", stationName);
            startActivity(intent);
        } else {
            Animation shake = AnimationUtils.loadAnimation(LettersActivity.this, R.anim.shake);
            findViewById(R.id.continueButton).startAnimation(shake);

            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
        }
    }

    private StringBuilder shuffleLetters(StringBuilder stringBuilder) {
        Random random = new Random();

        for (int i = stringBuilder.length() - 1; i > 1; i--) {
            int numberToSwapWith = random.nextInt(i);
            char tmp = stringBuilder.charAt(numberToSwapWith);
            stringBuilder.setCharAt(numberToSwapWith, stringBuilder.charAt(i));
            stringBuilder.setCharAt(i, tmp);
        }

        return stringBuilder;
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
        topDarkActionbar.showMenu();
    }

    public void showStats(View view){
        topDarkActionbar.showStats(currentScore, startTime);
    }

    public void quitTour(View view){
        showEndTourDialog();
    }
}
