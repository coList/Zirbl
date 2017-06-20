package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Random;

import hsaugsburg.zirbl001.Datamanagement.JSONLetters;
import hsaugsburg.zirbl001.Models.LettersModel;
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

        StringBuilder stringBuilder = new StringBuilder(result.getSolution() + result.getOtherLetters());
        shuffleLetters(stringBuilder);
        String letters = stringBuilder.toString().toUpperCase();

        for (int i = 0; i < letters.length(); i++) {
            String name = "letter" + (i + 1);
            int id = getResources().getIdentifier(name, "id", getPackageName());
            TextView letter = (TextView) findViewById(id);
            letter.setText(String.valueOf(letters.charAt(i)));
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
}
