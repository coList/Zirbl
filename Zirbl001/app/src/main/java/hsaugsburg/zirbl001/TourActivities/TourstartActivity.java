package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;

import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadLocationDoUKnow;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTourChronology;
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.TourModels.ChronologyModel;
import hsaugsburg.zirbl001.Fonts.QuicksandRegularPrimaryEdit;
import hsaugsburg.zirbl001.Models.TourModels.DoUKnowModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.ObjectSerializer;
import hsaugsburg.zirbl001.Utils.TopDarkActionbar;

public class TourstartActivity extends AppCompatActivity implements TourActivity{
    private int selectedTour;
    private ChronologyModel nextChronologyItem = new ChronologyModel();
    private LoadTourChronology loadTourChronology;
    private String stationName = "Anmeldung";

    private Context mContext = TourstartActivity.this;

    private int count = 0;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    public static final String TOUR_VALUES = "tourValuesFile";

    private TopDarkActionbar topDarkActionbar;

    private String team;
    private String members;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourstart);
        int classID;

        QuicksandRegularPrimaryEdit teamField = (QuicksandRegularPrimaryEdit) findViewById(R.id.teamname);
        ViewCompat.setBackgroundTintList(teamField, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorLine)));

        QuicksandRegularPrimaryEdit memberField1 = (QuicksandRegularPrimaryEdit) findViewById(R.id.firstName);
        ViewCompat.setBackgroundTintList(memberField1, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorLine)));
        QuicksandRegularPrimaryEdit memberField2 = (QuicksandRegularPrimaryEdit) findViewById(R.id.secondName);
        ViewCompat.setBackgroundTintList(memberField2, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorLine)));

        selectedTour = Integer.parseInt(getIntent().getStringExtra("tourID"));
        classID = Integer.parseInt(getIntent().getStringExtra("classID"));

        int chronologyNumber = -1;
        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        String serverName = globalValues.getString("serverName", null);
        loadTourChronology = new LoadTourChronology(this, this, nextChronologyItem, selectedTour, chronologyNumber);
        loadTourChronology.readChronologyFile();

        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        SharedPreferences.Editor editor = tourValues.edit();
        editor.putString("tourID", Integer.toString(selectedTour));
        editor.putString("classID", Integer.toString(classID));
        editor.putString("currentScore", Integer.toString(0));
        editor.putString("nutsCollected", Integer.toString(0));
        editor.putString("totalChronology", Integer.toString(loadTourChronology.getLastChronologyValue()));

        String titleText = "Anmeldung";
        topDarkActionbar = new TopDarkActionbar(this, titleText);

        ArrayList<DoUKnowModel> doUKnowModels = new LoadLocationDoUKnow(this, selectedTour).readFile();
        try {
            ArrayList<Boolean> listIsNutCollected = new ArrayList<>();
            ArrayList<Boolean> listDoUKnowRead = new ArrayList<>();

            for (int i = 0; i < 4; i++) {
                listIsNutCollected.add(false);
            }

            for (int i = 0; i < doUKnowModels.size(); i++) {
                listDoUKnowRead.add(false);
            }
            editor.putString("listIsNutCollected", ObjectSerializer.serialize(listIsNutCollected));
            editor.putString("listDoUKnowRead", ObjectSerializer.serialize(listDoUKnowRead));
        } catch (IOException e) {
            e.printStackTrace();
        }

        editor.commit();
    }
    
    public void showMenu(View view){
        topDarkActionbar.showMenu();
    }

    public void showStats(View view){
        topDarkActionbar.showStats(0, 0);
    }

    public void quitTour(View view){
        showEndTourDialog();
    }

    public int getSelectedTour() {
        return selectedTour;
    }


    public void addParticipant(View view) {
        EditText firstParticipantText = (EditText)findViewById(R.id.firstName);
        EditText secondParticipantText = (EditText)findViewById(R.id.secondName);
        int maxAmountOfParticipants = 10;


        if (count < maxAmountOfParticipants - 1 && !firstParticipantText.getText().toString().isEmpty() && !secondParticipantText.getText().toString().isEmpty()) {
            EditText previousParticipantText = new EditText(this);
            if (count > 0) {
                previousParticipantText = (EditText) findViewById(count - 1);
            } else {
                previousParticipantText = secondParticipantText;
            }
            String previousParticipant = previousParticipantText.getText().toString();

            if (!previousParticipant.isEmpty()) {
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.userInput);
                LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View participantView = mInflater.inflate(R.layout.list_element_participant, null);
                participantView.setId(count);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                float d = getResources().getDisplayMetrics().density;

                params.leftMargin = (int) (24 * d);
                params.rightMargin = (int) (24 * d);
                participantView.setLayoutParams(params);
                ViewCompat.setBackgroundTintList(participantView, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorLine)));
                linearLayout.addView(participantView);
                count++;
            } else {
                Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
                findViewById(R.id.plusButton).startAnimation(shake);
                Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                vibe.vibrate(100);
            }
        } else {
            Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
            findViewById(R.id.plusButton).startAnimation(shake);
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
        }
    }

    public void goIntoTour(View view) {
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        SharedPreferences.Editor editor = tourValues.edit();

        EditText teamName = (EditText)findViewById(R.id.teamname);
        editor.putString("teamName", teamName.getText().toString());

        ArrayList<String> participants = new ArrayList<>();
        participants.add(((EditText)findViewById(R.id.firstName)).getText().toString());

        EditText secondParticipant = (EditText) findViewById(R.id.secondName);
        if (!secondParticipant.getText().toString().isEmpty()) {
            participants.add(((EditText) findViewById(R.id.secondName)).getText().toString());
        }

        editor.putString("startTime", Long.toString(System.currentTimeMillis()));

        for (int i = 0; i < count; i++) {
            EditText nextParticipant = (EditText) findViewById(i);
            participants.add(nextParticipant.getText().toString());
        }

        try {
            editor.putString("participants", ObjectSerializer.serialize(participants));
        } catch (IOException e) {
            e.printStackTrace();
        }

        editor.commit();

        ImageView speechBubble = (ImageView) findViewById(R.id.registrationWelcome);
        setInput();

        if (team != null && !team.isEmpty() && members != null && !members.isEmpty()) {
            loadTourChronology.continueToNextView();
            Intent intent = new Intent(mContext, ResultActivity.class);
            intent.putExtra("team", team);
            intent.putExtra("members", members);
        } else {
            Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
            findViewById(R.id.continueButton).startAnimation(shake);
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
            speechBubble.setImageResource(R.drawable.img_zirbl_speech_bubble_class_fail);
        }
    }

    public String getStationName() {
        return stationName;
    }

    private void showEndTourDialog(){
        this.runOnUiThread(new Runnable() {
            public void run() {
                EndTourDialog alertEnd = new EndTourDialog(mContext, selectedTour);
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

    public void setInput(){
        EditText teamname = (EditText) findViewById(R.id.teamname);
        EditText firstname = (EditText) findViewById(R.id.firstName);

        team = teamname.getText().toString();
        members = firstname.getText().toString();
    }

}
