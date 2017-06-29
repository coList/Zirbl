package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import org.w3c.dom.Text;

import hsaugsburg.zirbl001.Datamanagement.JSONTourstart;
import hsaugsburg.zirbl001.Fonts.QuicksandBoldPrimaryButton;
import hsaugsburg.zirbl001.Fonts.QuicksandBoldPrimaryView;
import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.NavigationActivities.QrCode.QrDialog;
import hsaugsburg.zirbl001.Fonts.QuicksandRegularPrimaryEdit;

import hsaugsburg.zirbl001.R;

public class TourstartActivity extends AppCompatActivity {

    private static final String TAG = "TourstartActivity";
    private int maxAmountOfParticipants = 10;

    private int selectedTour;
    private int currentScore = 0;
    ChronologyModel nextChronologyItem = new ChronologyModel();

    private Context mContext = TourstartActivity.this;

    private int count = 0;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;

    public static final String TOUR_VALUES = "tourValuesFile";

    public int getSelectedTour() {
        return selectedTour;
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourstart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Anmeldung");

        QuicksandRegularPrimaryEdit teamField = (QuicksandRegularPrimaryEdit) findViewById(R.id.teamname);
        ViewCompat.setBackgroundTintList(teamField, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorLine)));
        QuicksandRegularPrimaryEdit memberField = (QuicksandRegularPrimaryEdit) findViewById(R.id.firstName);
        ViewCompat.setBackgroundTintList(memberField, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorLine)));

        selectedTour = Integer.parseInt(getIntent().getStringExtra("tourID"));

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        new JSONTourstart(this).execute(serverName + "/selectChronologyView.php");


    }

    public void processData (ChronologyModel result, int lastChronologyValue) {
        nextChronologyItem = result;

        //set global tour values
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        SharedPreferences.Editor editor = tourValues.edit();
        editor.putString("tourID", Integer.toString(selectedTour));
        editor.putString("totalChronology", Integer.toString(lastChronologyValue));

        editor.commit();

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(lastChronologyValue + 1);
        progressBar.setProgress(0);

    }

    public void addParticipant(View view) {
        if (count < maxAmountOfParticipants - 1) {
            EditText previousParticipantText = new EditText(this);
            if (count > 0) {
                previousParticipantText = (EditText) findViewById(count - 1);
            } else {
                previousParticipantText = (EditText) findViewById(R.id.firstName);
            }
            String previousParticipant = previousParticipantText.getText().toString();

            if (previousParticipant != null && !previousParticipant.isEmpty()) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.userInput);
                QuicksandRegularPrimaryEdit participantField = new QuicksandRegularPrimaryEdit(mContext);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                float d = getResources().getDisplayMetrics().density;

                params.leftMargin = (int) (24 * d);
                params.rightMargin = (int) (24 * d);

                //TODO: set textCursorDrawable
                participantField.setId(count);
                participantField.setHint("Gruppenmitglied");
                participantField.setHintTextColor(ContextCompat.getColor(mContext, R.color.colorTransparent));
                participantField.setCursorVisible(true);
                ViewCompat.setBackgroundTintList(participantField, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorLine)));
                participantField.setEms(10);
                participantField.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                participantField.setLayoutParams(params);

                linearLayout.addView(participantField);
                count++;
            } else {
                Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
                findViewById(R.id.plusButton).startAnimation(shake);
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
            }

        }
    }

    public void goIntoTour(View view) {

        ImageView speechBubble = (ImageView) findViewById(R.id.registrationWelcome);
        EditText teamNameText = (EditText) findViewById(R.id.teamname);
        String teamName = teamNameText.getText().toString();

        EditText participantText = (EditText) findViewById(R.id.firstName);
        String participant = participantText.getText().toString();

        if (teamName != null && !teamName.isEmpty() && participant != null && !participant.isEmpty()) {
            if (nextChronologyItem.getInfoPopupID() != null) {
                Intent intent = new Intent(mContext, DoUKnowActivity.class);
                intent.putExtra("chronologyNumber", Integer.toString(0));
                intent.putExtra("currentscore", Integer.toString(currentScore));
                intent.putExtra("stationName", "Start");
                intent.putExtra("infopopupid", Integer.toString(nextChronologyItem.getInfoPopupID()));
                startActivity(intent);
            }
        } else {
            Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
            findViewById(R.id.continueButton).startAnimation(shake);
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
            speechBubble.setImageResource(R.drawable.img_zirbl_speech_bubble_class_fail);
        }


        //Intent intent = new Intent(mContext, NavigationActivity.class);
        //startActivity(intent);
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
}
