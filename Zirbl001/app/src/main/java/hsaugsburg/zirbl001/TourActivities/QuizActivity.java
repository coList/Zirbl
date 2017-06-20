package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Random;

import hsaugsburg.zirbl001.Datamanagement.JSONQuiz;
import hsaugsburg.zirbl001.Models.QuizModel;
import hsaugsburg.zirbl001.R;

public class QuizActivity extends AppCompatActivity {

    private Context mContext = QuizActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new JSONQuiz(this).execute("https://zirbl.multimedia.hs-augsburg.de/selectSingleChoiceView.php");
        setContentView(R.layout.activity_quiz);
    }

    public void continueToNextView(View view) {
        Intent intent = new Intent(mContext, NavigationActivity.class);
        startActivity(intent);
    }

    public void processData (QuizModel result) {
        TextView question = (TextView) findViewById(R.id.questionText);
        question.setText(result.getQuestion());

        String[] answers = {result.getRightAnswer(), result.getOption2(), result.getOption3(), result.getOption4()};
        answers = shuffleArray(answers);

        for (int i = 0; i < answers.length; i++) {

            String name = "answer" + (i + 1);
            int id = getResources().getIdentifier(name, "id", getPackageName());
            TextView answer = (TextView) findViewById(id);
            answer.setText(answers[i]);
        }
    }

    public String[] shuffleArray(String[] array) {
        Random random = new Random();

        for (int i = array.length-1; i > 1; i--) {
            int numberToSwapWith = random.nextInt(i);
            String tmp = array[numberToSwapWith];
            array[numberToSwapWith] = array[i];
            array[i] = tmp;
        }

        return array;
    }
}

