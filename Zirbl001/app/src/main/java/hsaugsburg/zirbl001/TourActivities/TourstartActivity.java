package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTourChronology;
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.ChronologyModel;
import hsaugsburg.zirbl001.Fonts.QuicksandRegularPrimaryEdit;
import hsaugsburg.zirbl001.R;

public class TourstartActivity extends AppCompatActivity implements TourActivity{

    private static final String TAG = "TourstartActivity";
    private int maxAmountOfParticipants = 10;

    private int selectedTour;
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
        QuicksandRegularPrimaryEdit memberField = (QuicksandRegularPrimaryEdit) findViewById(R.id.firstName);
        ViewCompat.setBackgroundTintList(memberField, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorLine)));

        selectedTour = Integer.parseInt(getIntent().getStringExtra("tourID"));


        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        //new JSONTourstart(this).execute(serverName + "/api/selectChronologyView.php");

        loadTourChronology = new LoadTourChronology(this, this, nextChronologyItem, selectedTour, chronologyNumber);
        loadTourChronology.readChronologyFile();

        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        SharedPreferences.Editor editor = tourValues.edit();
        editor.putString("tourID", Integer.toString(selectedTour));
        editor.putString("currentScore", Integer.toString(0));
        editor.putString("nutsCollected", Integer.toString(0));
        editor.putString("totalChronology", Integer.toString(loadTourChronology.getLastChronologyValue()));
        Log.d("TourStartActivity", Integer.toString(loadTourChronology.getLastChronologyValue()));

        editor.commit();

        ImageLoader.getInstance().destroy();

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheInMemory(false)
                .considerExifParams(true)
                .imageScaleType(ImageScaleType.EXACTLY_STRETCHED) //filled width
                .build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPoolSize(1)
                .threadPriority(Thread.MAX_PRIORITY)
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(defaultOptions)
                .diskCacheExtraOptions(480, 320, null)
                //.memoryCache(new WeakMemoryCache())
                .build();
        ImageLoader.getInstance().init(config);
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
        Log.d("Tourstart", "goIntoTour");

        ImageView speechBubble = (ImageView) findViewById(R.id.registrationWelcome);
        EditText teamNameText = (EditText) findViewById(R.id.teamname);
        String teamName = teamNameText.getText().toString();

        EditText participantText = (EditText) findViewById(R.id.firstName);
        String participant = participantText.getText().toString();

        if (teamName != null && !teamName.isEmpty() && participant != null && !participant.isEmpty()) {
            loadTourChronology.continueToNextView();
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
}
