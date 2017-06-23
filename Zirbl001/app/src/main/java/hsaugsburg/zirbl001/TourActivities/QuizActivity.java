package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

import hsaugsburg.zirbl001.Datamanagement.JSONQuiz;
import hsaugsburg.zirbl001.Models.QuizModel;
import hsaugsburg.zirbl001.R;

public class QuizActivity extends AppCompatActivity {

    private Context mContext = QuizActivity.this;


    private int amountOfAnswers = 4;
    private int selectedAnswer = -1;



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

            selectedAnswer = 1;
            resetAnswers();

            RelativeLayout selected = (RelativeLayout)findViewById(R.id.area1);
            selected.setBackgroundResource(R.color.colorTurquoise);

            ImageView invertedImg = (ImageView)findViewById(R.id.imgLetter1);
            invertedImg.setImageResource(R.drawable.icon_a_active);
            Button btA = (Button) findViewById(R.id.answer1);
            btA.setTextColor(Color.WHITE);
        }
    };
    View.OnClickListener answerB = new View.OnClickListener() {
        public void onClick(View v) {

            selectedAnswer = 2;
            resetAnswers();

            RelativeLayout selected = (RelativeLayout)findViewById(R.id.area2);
            selected.setBackgroundResource(R.color.colorTurquoise);
            ImageView invertedImg = (ImageView)findViewById(R.id.imgLetter2);
            invertedImg.setImageResource(R.drawable.icon_b_active);
            Button btA = (Button) findViewById(R.id.answer2);
            btA.setTextColor(Color.WHITE);

        }
    };
    View.OnClickListener answerC = new View.OnClickListener() {
        public void onClick(View v) {

            selectedAnswer = 3;
            resetAnswers();

            RelativeLayout selected = (RelativeLayout)findViewById(R.id.area3);
            selected.setBackgroundResource(R.color.colorTurquoise);

            ImageView invertedImg = (ImageView)findViewById(R.id.imgLetter3);
            invertedImg.setImageResource(R.drawable.icon_c_active);
            Button btA = (Button) findViewById(R.id.answer3);
            btA.setTextColor(Color.WHITE);
        }
    };
    View.OnClickListener answerD = new View.OnClickListener() {
        public void onClick(View v) {

            selectedAnswer = 4;
            resetAnswers();

            RelativeLayout selected = (RelativeLayout)findViewById(R.id.area4);
            selected.setBackgroundResource(R.color.colorTurquoise);

            ImageView invertedImg = (ImageView)findViewById(R.id.imgLetter4);
            invertedImg.setImageResource(R.drawable.icon_d_active);
            Button btA = (Button) findViewById(R.id.answer4);
            btA.setTextColor(Color.WHITE);
        }
    };


    public void processData (QuizModel result) {

        TextView question = (TextView) findViewById(R.id.questionText);
        if (result.getPicturePath().equals("null")) {  //is it a question with an image? if not:
            question.setText(result.getQuestion());
        } else {  //if it has an image:
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.questionImage);
            relativeLayout.setVisibility(View.VISIBLE);
            TextView questionBesideImg = (TextView) findViewById(R.id.besideImgQuestion);
            questionBesideImg.setText(result.getQuestion());
            amountOfAnswers = 3;

            RelativeLayout area4 = (RelativeLayout) findViewById(R.id.area4);
            area4.setVisibility(View.GONE);

            question.setVisibility(View.GONE);
        }


        //put answer options into layout

        String[] answers = {result.getRightAnswer(), result.getOption2(), result.getOption3(), result.getOption4()};
        answers = shuffleArray(answers);

        for (int i = 0; i < answers.length; i++) {

            String name = "answer" + (i + 1);
            int id = getResources().getIdentifier(name, "id", getPackageName());
            TextView answer = (TextView) findViewById(id);
            answer.setText(answers[i]);
        }

        final String rightAnswer = result.getRightAnswer();
        final String answerCorrect = result.getAnswerCorrect();
        final String answerWrong = result.getAnswerWrong();

        ImageButton continueButton = (ImageButton)findViewById(R.id.continueButton);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedAnswer >= 1) {
                    String name = "answer" + selectedAnswer;
                    int id = getResources().getIdentifier(name, "id", getPackageName());
                    Button selectedButton = (Button) findViewById(id);
                    String userAnswer = selectedButton.getText().toString();

                    Intent intent = new Intent(mContext, PointsActivity.class);
                    intent.putExtra("isSlider", "false");
                    intent.putExtra("userAnswer", userAnswer);
                    intent.putExtra("solution", rightAnswer);
                    intent.putExtra("answerCorrect", answerCorrect);
                    intent.putExtra("answerWrong", answerWrong);
                    startActivity(intent);
                }
            }
        });
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

    //unselect all answers
    private void resetAnswers() {
        for (int i = 0; i < amountOfAnswers; i++) {
            String nameRelativeLayout = "area" + (i + 1);
            int relativeLayoutID = getResources().getIdentifier(nameRelativeLayout, "id", getPackageName());
            RelativeLayout relativeLayout = (RelativeLayout)findViewById(relativeLayoutID);
            relativeLayout.setBackgroundResource(0);

            String nameImageView = "imgLetter" + (i + 1);
            int imageViewID = getResources().getIdentifier(nameImageView, "id", getPackageName());
            ImageView imageView = (ImageView)findViewById(imageViewID);
            String nameDrawable = "icon_" + (i + 1) + "_normal";
            int imageDrawable = getResources().getIdentifier(nameDrawable, "drawable", getPackageName());
            imageView.setImageResource(imageDrawable);

            String nameButton = "answer" + (i + 1);
            int buttonID = getResources().getIdentifier(nameButton, "id", getPackageName());
            Button button = (Button) findViewById(buttonID);
            int colorId = ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark);
            button.setTextColor(colorId);
        }
    }
}

