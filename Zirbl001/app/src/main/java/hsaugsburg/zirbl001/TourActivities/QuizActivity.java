package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import hsaugsburg.zirbl001.Datamanagement.JSONQuiz;
import hsaugsburg.zirbl001.Models.QuizModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.UniversalImageLoader;

public class QuizActivity extends AppCompatActivity {

    private Context mContext = QuizActivity.this;


    private int amountOfAnswers;
    private int selectedAnswer = -1;

    private int chronologyNumber;

    private String rightAnswer;
    private String answerCorrect;
    private String answerWrong;
    private int score;

    private int currentScore;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));
        currentScore = Integer.parseInt(getIntent().getStringExtra("currentscore"));

        int taskID = Integer.parseInt(getIntent().getStringExtra("taskid"));

        new JSONQuiz(this, taskID).execute("https://zirbl.multimedia.hs-augsburg.de/selectSingleChoiceView.php");

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

        initImageLoader();


    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public void continueToNextView(View view) {
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
            intent.putExtra("score", Integer.toString(score));
            intent.putExtra("chronologyNumber", Integer.toString(chronologyNumber));
            intent.putExtra("currentscore", Integer.toString(currentScore));
            startActivity(intent);
        } else {
            Animation shake = AnimationUtils.loadAnimation(QuizActivity.this, R.anim.shake);
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

            RelativeLayout selected = (RelativeLayout)findViewById(R.id.area1);
            selected.setBackgroundResource(R.color.colorTurquoise);

            ImageView invertedImg = (ImageView)findViewById(R.id.imgLetter1);
            invertedImg.setImageResource(R.drawable.ic_1_active);
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
            invertedImg.setImageResource(R.drawable.ic_2_active);
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
            invertedImg.setImageResource(R.drawable.ic_3_active);
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
            invertedImg.setImageResource(R.drawable.ic_4_active);
            Button btA = (Button) findViewById(R.id.answer4);
            btA.setTextColor(Color.WHITE);
        }
    };


    public void processData (QuizModel result) {
        TextView question = (TextView) findViewById(R.id.questionText);


        ArrayList<String> answers = new ArrayList<>();
        if (result.getPicturePath().equals("null")) {  //is it a question with an image? if not:
            question.setText(fromHtml(result.getQuestion()));
            answers.addAll(Arrays.asList(result.getRightAnswer(), result.getOption2(), result.getOption3(), result.getOption4()));
        } else {  //if it has an image:
            RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.questionImage);
            relativeLayout.setVisibility(View.VISIBLE);
            TextView questionBesideImg = (TextView) findViewById(R.id.besideImgQuestion);
            questionBesideImg.setText(fromHtml(result.getQuestion()));


            String imageURL = result.getPicturePath();
            ImageView questionPicture = (ImageView)findViewById(R.id.behindQuestionImage);

            ImageLoader.getInstance().displayImage(imageURL, questionPicture);

            RelativeLayout area4 = (RelativeLayout) findViewById(R.id.area4);
            area4.setVisibility(View.GONE);

            question.setVisibility(View.GONE);


            answers.addAll(Arrays.asList(result.getRightAnswer(), result.getOption2(), result.getOption3()));
        }

        amountOfAnswers = answers.size();

        //put answer options into layout


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
        score = result.getScore();



    }

    public ArrayList<String> shuffleArray(ArrayList<String> list) {
        Random random = new Random();

        for (int i = list.size()-1; i > 1; i--) {
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
            RelativeLayout relativeLayout = (RelativeLayout)findViewById(relativeLayoutID);
            relativeLayout.setBackgroundResource(0);

            String nameImageView = "imgLetter" + (i + 1);
            int imageViewID = getResources().getIdentifier(nameImageView, "id", getPackageName());
            ImageView imageView = (ImageView)findViewById(imageViewID);
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
}

