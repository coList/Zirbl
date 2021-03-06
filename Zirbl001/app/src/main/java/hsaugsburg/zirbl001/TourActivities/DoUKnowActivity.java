package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.net.Uri;
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
import android.widget.VideoView;

import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

import hsaugsburg.zirbl001.CMS.LoadTasks.LoadDoUKnow;
import hsaugsburg.zirbl001.CMS.LoadTasks.LoadTourChronology;
import hsaugsburg.zirbl001.Interfaces.TourActivity;
import hsaugsburg.zirbl001.Models.TourModels.ChronologyModel;
import hsaugsburg.zirbl001.Models.TourModels.DoUKnowModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.TopDarkActionbar;
import hsaugsburg.zirbl001.Utils.UniversalImageLoader;

public class DoUKnowActivity extends AppCompatActivity implements TourActivity{
    private Context mContext = DoUKnowActivity.this;

    private int chronologyNumber;
    private String selectedTour;
    private String stationName;
    private String teamName;
    private ChronologyModel nextChronologyItem = new ChronologyModel();

    private LoadTourChronology loadTourChronology;

    public static final String TOUR_VALUES = "tourValuesFile";
    private int currentScore;
    private long startTime;

    private TopDarkActionbar topDarkActionbar;

    private String changeIntoTeamname = "TEAMNAME";

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
        selectedTour =tourValues.getString("tourContentfulID", null);
        int totalChronologyValue = Integer.parseInt(tourValues.getString("totalChronology", null));
        startTime = Long.parseLong(tourValues.getString("startTime", null));
        currentScore = Integer.parseInt(tourValues.getString("currentScore", null));
        teamName = tourValues.getString("teamName", null);

        //dot menu
        String knowledge = "Wissen";
        topDarkActionbar = new TopDarkActionbar(this, knowledge);

        stationName = getIntent().getStringExtra("stationName");

        loadTourChronology = new LoadTourChronology(this, this, selectedTour, chronologyNumber);
        loadTourChronology.loadData();

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
        String infoPopupID = getIntent().getStringExtra("infoPopupContentfulID");
        DoUKnowModel result = new LoadDoUKnow(infoPopupID, selectedTour).loadData();

        TextView doUKnow = (TextView) findViewById(R.id.DoUKnow);
        String resultText = result.getContentText();
        String newResultText = resultText.replaceAll(changeIntoTeamname, teamName);
        doUKnow.setText(fromHtml(newResultText));
        int stringLength = resultText.length();

        ImageView zirblImage = (ImageView) findViewById(R.id.themeZirbl);

        if (result.getPicturePath() != null && !result.getPicturePath().isEmpty() && !result.getPicturePath().equals("null")) {
            ContextWrapper cw = new ContextWrapper(this.getApplicationContext());
            File directory = cw.getDir("zirblImages", Context.MODE_PRIVATE);
            String[] parts = result.getPicturePath().split("\\.");
            String imgPath = selectedTour + "infoPopupId" + result.getContentfulID() + "picture" + "." + parts[parts.length - 1];
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
        if (((double)stringLength)/height <= 0.16) {
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
