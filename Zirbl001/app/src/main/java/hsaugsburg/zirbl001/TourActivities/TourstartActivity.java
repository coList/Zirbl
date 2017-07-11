package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.IOException;
import java.util.ArrayList;

import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadLocationDoUKnow;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTourChronology;
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.Fonts.QuicksandRegularPrimaryEdit;
import hsaugsburg.zirbl001.Models.DoUKnowModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.ObjectSerializer;

public class TourstartActivity extends AppCompatActivity implements TourActivity{

    private static final String TAG = "TourstartActivity";
    private int maxAmountOfParticipants = 10;

    private int selectedTour;
    private int classID;
    private int currentScore = 0;
    private ChronologyModel nextChronologyItem = new ChronologyModel();
    private int chronologyNumber = -1;
    private LoadTourChronology loadTourChronology;
    private String stationName = "Anmeldung";

    private Context mContext = TourstartActivity.this;

    private int count = 0;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;

    public static final String TOUR_VALUES = "tourValuesFile";

    public int getSelectedTour() {
        return selectedTour;
    }

    private TextView title;
    private RelativeLayout dotMenuLayout;
    private boolean dotMenuOpen = false;
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
        title = (TextView) findViewById(R.id.titleActionbar);
        title.setText("Anmeldung");
        dotMenuLayout=(RelativeLayout) this.findViewById(R.id.dotMenu);
        dotMenuLayout.setVisibility(RelativeLayout.GONE);


        QuicksandRegularPrimaryEdit teamField = (QuicksandRegularPrimaryEdit) findViewById(R.id.teamname);
        ViewCompat.setBackgroundTintList(teamField, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorLine)));
        QuicksandRegularPrimaryEdit memberField1 = (QuicksandRegularPrimaryEdit) findViewById(R.id.firstName);
        ViewCompat.setBackgroundTintList(memberField1, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorLine)));
        QuicksandRegularPrimaryEdit memberField2 = (QuicksandRegularPrimaryEdit) findViewById(R.id.secondName);
        ViewCompat.setBackgroundTintList(memberField2, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorLine)));

        selectedTour = Integer.parseInt(getIntent().getStringExtra("tourID"));
        classID = Integer.parseInt(getIntent().getStringExtra("classID"));


        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        //new JSONTourstart(this).execute(serverName + "/api/selectChronologyView.php");

        loadTourChronology = new LoadTourChronology(this, this, nextChronologyItem, selectedTour, chronologyNumber);
        loadTourChronology.readChronologyFile();

        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        SharedPreferences.Editor editor = tourValues.edit();
        editor.putString("tourID", Integer.toString(selectedTour));
        editor.putString("classID", Integer.toString(classID));
        editor.putString("currentScore", Integer.toString(0));
        editor.putString("nutsCollected", Integer.toString(0));
        editor.putString("totalChronology", Integer.toString(loadTourChronology.getLastChronologyValue()));

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
            Log.d(TAG, listIsNutCollected.toString());
            editor.putString("listIsNutCollected", ObjectSerializer.serialize(listIsNutCollected));
            editor.putString("listDoUKnowRead", ObjectSerializer.serialize(listDoUKnowRead));
        } catch (IOException e) {
            e.printStackTrace();
        }

        editor.commit();
    }
    
    public void showMenu(View view){

        ImageView dotIcon = (ImageView) findViewById(R.id.dotIcon);
        TextView menuStats = (TextView) findViewById(R.id.menuStats);
        TextView menuQuit = (TextView) findViewById(R.id.menuQuit);

        if(dotMenuOpen){
            dotMenuLayout.setVisibility(RelativeLayout.GONE);
            dotMenuOpen = false;
            title.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            dotIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent));
        } else {
            dotMenuLayout.setVisibility(RelativeLayout.VISIBLE);
            dotMenuOpen = true;
            title.setTextColor(ContextCompat.getColor(mContext, R.color.colorTurquoise));
            dotIcon.setColorFilter(ContextCompat.getColor(mContext, R.color.colorTurquoise));
            menuQuit.setTextSize(18);
            menuStats.setTextSize(18);
        }
    }
    public void showStats(View view){
        Log.d(TAG, "showStats: Stats");
    }
    public void quitTour(View view){
        showEndTourDialog();
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
                participantField.setHintTextColor(ContextCompat.getColor(mContext, R.color.colorTransparent30));
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
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        SharedPreferences.Editor editor = tourValues.edit();

        EditText teamName = (EditText)findViewById(R.id.teamname);
        editor.putString("teamName", teamName.getText().toString());

        ArrayList<String> participants = new ArrayList<>();
        participants.add(((EditText)findViewById(R.id.firstName)).getText().toString());

        participants.add(((EditText)findViewById(R.id.secondName)).getText().toString());

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

    public void setInput(){
        EditText teamname = (EditText) findViewById(R.id.teamname);
        EditText firstname = (EditText) findViewById(R.id.firstName);

        team = teamname.getText().toString();
        members = firstname.getText().toString();
    }
}
