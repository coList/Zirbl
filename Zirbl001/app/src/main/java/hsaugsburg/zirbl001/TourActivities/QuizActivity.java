package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import hsaugsburg.zirbl001.Datamanagement.JSONQuiz;
import hsaugsburg.zirbl001.Models.QuizModel;
import hsaugsburg.zirbl001.R;

public class QuizActivity extends AppCompatActivity {

    private Context mContext = QuizActivity.this;

    boolean btnAClicked = false;
    boolean btnBClicked = false;
    boolean btnCClicked = false;
    boolean btnDClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new JSONQuiz(this).execute("https://zirbl.multimedia.hs-augsburg.de/selectSingleChoiceView.php");
        setContentView(R.layout.activity_quiz);

        //Selection
        Button buttonA = (Button) findViewById(R.id.answer1);
        buttonA.setOnClickListener(answerA);
        Button buttonB = (Button) findViewById(R.id.answer2);
        buttonB.setOnClickListener(answerB);
        Button buttonC = (Button) findViewById(R.id.answer3);
        buttonC.setOnClickListener(answerC);
        Button buttonD = (Button) findViewById(R.id.answer4);
        buttonD.setOnClickListener(answerD);
        //

    }

    public void continueToNextView(View view) {
        Intent intent = new Intent(mContext, NavigationActivity.class);
        startActivity(intent);
    }

    //Selection
    View.OnClickListener answerA = new View.OnClickListener() {
        public void onClick(View v) {
            RelativeLayout selected = (RelativeLayout)findViewById(R.id.areaA);
            selected.setBackgroundResource(R.color.colorTruth);
            ImageView invertedImg = (ImageView)findViewById(R.id.imgLetterA);
            invertedImg.setImageResource(R.drawable.icon_a_active);
            Button btA = (Button) findViewById(R.id.answer1);
            btA.setTextColor(Color.WHITE);
            btnAClicked = true;
        }
    };
    View.OnClickListener answerB = new View.OnClickListener() {
        public void onClick(View v) {
            RelativeLayout selected = (RelativeLayout)findViewById(R.id.areaB);
            selected.setBackgroundResource(R.color.colorTruth);
            ImageView invertedImg = (ImageView)findViewById(R.id.imgLetterB);
            invertedImg.setImageResource(R.drawable.icon_b_active);
            Button btA = (Button) findViewById(R.id.answer2);
            btA.setTextColor(Color.WHITE);
            btnBClicked = true;
        }
    };
    View.OnClickListener answerC = new View.OnClickListener() {
        public void onClick(View v) {
            RelativeLayout selected = (RelativeLayout)findViewById(R.id.areaC);
            selected.setBackgroundResource(R.color.colorTruth);
            ImageView invertedImg = (ImageView)findViewById(R.id.imgLetterC);
            invertedImg.setImageResource(R.drawable.icon_c_active);
            Button btA = (Button) findViewById(R.id.answer3);
            btA.setTextColor(Color.WHITE);
            btnCClicked = true;
        }
    };
    View.OnClickListener answerD = new View.OnClickListener() {
        public void onClick(View v) {
            RelativeLayout selected = (RelativeLayout)findViewById(R.id.areaD);
            selected.setBackgroundResource(R.color.colorTruth);
            ImageView invertedImg = (ImageView)findViewById(R.id.imgLetterD);
            invertedImg.setImageResource(R.drawable.icon_d_active);
            Button btA = (Button) findViewById(R.id.answer4);
            btA.setTextColor(Color.WHITE);
            btnDClicked = true;
        }
    };
    //

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

