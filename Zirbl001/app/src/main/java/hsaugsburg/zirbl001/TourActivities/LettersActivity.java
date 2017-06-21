package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;

import hsaugsburg.zirbl001.Datamanagement.JSONLetters;
import hsaugsburg.zirbl001.Models.LettersModel;
import hsaugsburg.zirbl001.Models.TourSelectionModel;
import hsaugsburg.zirbl001.R;

public class LettersActivity extends AppCompatActivity {

    private int amountOfLetters = 14;

    private Context mContext = LettersActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new JSONLetters(this).execute("https://zirbl.multimedia.hs-augsburg.de/selectHangmanView.php");
        setContentView(R.layout.activity_letters);


    }

    public void continueToNextView(View view) {
        Intent intent = new Intent(mContext, NavigationActivity.class);
        startActivity(intent);
    }

    public void processData (LettersModel result) {
        TextView question = (TextView) findViewById(R.id.questionText);
        question.setText(result.getQuestion());

        TableRow tableRow = (TableRow)findViewById(R.id.inputArea);
        final int solutionLength = result.getSolution().length();
        final String solution = result.getSolution();
        final String answerCorrect = result.getAnswerCorrect();
        final String answerWrong = result.getAnswerWrong();

        StringBuilder stringBuilder = new StringBuilder(result.getSolution() + result.getOtherLetters());
        shuffleLetters(stringBuilder);
        final String letters = stringBuilder.toString().toUpperCase();

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

            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {  //delete set button and show the old letter

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
                            Button button = (Button)findViewById(i);
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

        //set continueButton
        ImageButton continueButton = (ImageButton)findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                boolean isAnswerCorrect;
                String userAnswer = "";
                for (int i = 0; i < solutionLength; i++) {
                    Button button = (Button)findViewById(i);
                    userAnswer += button.getText();
                }

                Intent intent1 = new Intent(mContext, PointsActivity.class);
                intent1.putExtra("userAnswer", userAnswer);
                intent1.putExtra("solution", solution.toUpperCase());
                intent1.putExtra("answerCorrect", answerCorrect);
                intent1.putExtra("answerWrong", answerWrong);
                startActivity(intent1);

            }
        });
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



}
