package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;

import hsaugsburg.zirbl001.Datamanagement.JSONLetters;
import hsaugsburg.zirbl001.Datamanagement.TourChronologyTask;
import hsaugsburg.zirbl001.Models.LettersModel;
import hsaugsburg.zirbl001.Models.TourSelectionModel;
import hsaugsburg.zirbl001.R;

public class LettersActivity extends AppCompatActivity {

    private int amountOfLetters = 14;

    private Context mContext = LettersActivity.this;
    private int chronologyNumber;

    private String solution;
    private String answerCorrect;
    private String answerWrong;
    private int score;

    private int currentScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_letters);

        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));

        int taskID = Integer.parseInt(getIntent().getStringExtra("taskid"));
        currentScore = Integer.parseInt(getIntent().getStringExtra("currentscore"));

        new JSONLetters(this, taskID).execute("https://zirbl.multimedia.hs-augsburg.de/selectHangmanView.php");


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
            Intent intent = new Intent(mContext, PointsActivity.class);
            intent.putExtra("isSlider", "false");
            intent.putExtra("userAnswer", userAnswer);
            intent.putExtra("solution", solution.toUpperCase());
            intent.putExtra("answerCorrect", answerCorrect);
            intent.putExtra("answerWrong", answerWrong);
            intent.putExtra("score", Integer.toString(score));
            intent.putExtra("chronologyNumber", Integer.toString(chronologyNumber));
            intent.putExtra("currentscore", Integer.toString(currentScore));
            startActivity(intent);
        } else {
            Animation shake = AnimationUtils.loadAnimation(LettersActivity.this, R.anim.shake);
            findViewById(R.id.continueButton).startAnimation(shake);
        }


    }

    public void processData(LettersModel result) {
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
            final Button button = new Button(this);
            button.setId(i);
            TableRow.LayoutParams params = new TableRow.LayoutParams(0, TableRow.LayoutParams.MATCH_PARENT);
            params.weight = 1;
            Context context = button.getContext();
            int drawableId = context.getResources().getIdentifier("button_underline", "drawable", context.getPackageName());
            button.setBackgroundResource(drawableId);

            int colorId = context.getResources().getIdentifier("colorPrimaryDark", "color", context.getPackageName());
            button.setTextColor(colorId);
            button.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            button.setText("");


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
                EndTourDialog alertEnd = new EndTourDialog(mContext);
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


}
