package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadDoUKnow;
import hsaugsburg.zirbl001.Datamanagement.LoadTasks.LoadTourChronology;
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.TourModels.ChronologyModel;
import hsaugsburg.zirbl001.Models.TourModels.DoUKnowModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.TopDarkActionbar;
import hsaugsburg.zirbl001.Utils.UniversalImageLoader;

public class DoUKnowActivity extends AppCompatActivity implements TourActivity{
    private Context mContext = DoUKnowActivity.this;

    private static final String TAG = "DoUKnowActivity";

    private int chronologyNumber;
    private int selectedTour;
    private String stationName;
    private ChronologyModel nextChronologyItem = new ChronologyModel();

    private LoadTourChronology loadTourChronology;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    private String serverName;

    public static final String TOUR_VALUES = "tourValuesFile";
    private int currentScore;
    private long startTime;


    //dot menu
    private TopDarkActionbar topDarkActionbar;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_do_uknow);


        chronologyNumber = Integer.parseInt(getIntent().getStringExtra("chronologyNumber"));

        //get global tour values
        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));
        int totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));
        startTime = Long.parseLong(tourValues.getString("startTime", null));
        currentScore = Integer.parseInt(tourValues.getString("currentScore", null));


        //dot menu
        String knowledge = "Wissen";
        topDarkActionbar = new TopDarkActionbar(this, knowledge);

        stationName = getIntent().getStringExtra("stationName");

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        loadTourChronology = new LoadTourChronology(this, this, nextChronologyItem, selectedTour, chronologyNumber);
        loadTourChronology.readChronologyFile();

        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setMax(totalChronologyValue + 1);
        progressBar.setProgress(chronologyNumber + 1);

        initImageLoader();
        setDataView();

    }

    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    public void setDataView() {

        int infoPopupID = Integer.parseInt(getIntent().getStringExtra("infopopupid"));
        DoUKnowModel result = new LoadDoUKnow(this, selectedTour, infoPopupID).readFile();


        TextView doUKnow = (TextView) findViewById(R.id.DoUKnow);
        String resultText = result.getContentText();
        doUKnow.setText(fromHtml(resultText));
        int stringLength = resultText.length();


        ImageView zirblImage = (ImageView) findViewById(R.id.themeZirbl);

        if (result.getPicturePath() != null && !result.getPicturePath().isEmpty() && !result.getPicturePath().equals("null")) {
            ContextWrapper cw = new ContextWrapper(this.getApplicationContext());
            File directory = cw.getDir("zirblImages", Context.MODE_PRIVATE);
            String[] parts = result.getPicturePath().split("\\.");
            String imgPath = selectedTour + "infopopupid" + result.getInfoPopupID() + "." + parts[parts.length - 1];
            File imageFile = new File(directory, imgPath);
            String decodedImgUri = Uri.fromFile(imageFile).toString();
            ImageLoader.getInstance().displayImage(decodedImgUri, zirblImage);
        } else {
            zirblImage.setImageResource(R.drawable.img_zirbl_small_qrcode_r);
        }


        // Scroll View State Change
        RelativeLayout zirbl = (RelativeLayout) findViewById(R.id.zirbl);
        LinearLayout continueArea = (LinearLayout) findViewById(R.id.continueArea);
        RelativeLayout.LayoutParams paramsContinue = (RelativeLayout.LayoutParams) continueArea.getLayoutParams();
        RelativeLayout.LayoutParams paramsZirbl = (RelativeLayout.LayoutParams) zirbl.getLayoutParams();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        if(((double)stringLength)/height <= 0.16) {
            paramsContinue.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            paramsContinue.addRule(RelativeLayout.BELOW, 0);

            paramsZirbl.addRule(RelativeLayout.ABOVE, R.id.continueArea);

            continueArea.setLayoutParams(paramsContinue);
            zirbl.setLayoutParams(paramsZirbl);
        } else {
            paramsZirbl.addRule(RelativeLayout.ABOVE, 0);

            paramsContinue.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, 0);
            paramsContinue.addRule(RelativeLayout.BELOW, R.id.zirbl);

            continueArea.setLayoutParams(paramsContinue);
            zirbl.setLayoutParams(paramsZirbl);
        }

    }


    public void continueToNextView(View view) {
        if (chronologyNumber < 0) {
            this.finish();
        } else {
            loadTourChronology.continueToNextView();
        }
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

    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public String getStationName() {
        return stationName;
    }


    public void showMenu(View view){
        topDarkActionbar.showMenu();
    }

    public void showStats(View view){
        topDarkActionbar.showStats(currentScore, startTime);
    }
    public void quitTour(View view){
        showEndTourDialog();
    }
}
