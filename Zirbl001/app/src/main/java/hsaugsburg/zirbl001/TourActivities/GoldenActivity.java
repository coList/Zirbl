package hsaugsburg.zirbl001.TourActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import hsaugsburg.zirbl001.R;

public class GoldenActivity extends AppCompatActivity {


    private Context mContext = GoldenActivity.this;
    private static final String TAG = "GoldenActivity";


    public static final String TOUR_VALUES = "tourValuesFile";
    private int selectedTour;

    //dot menu
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
        setContentView(R.layout.activity_golden);

        Intent intent = getIntent();

        String titleText = "Glückwunsch";
        title = (TextView) findViewById(R.id.titleActionbar);
        title.setText(titleText);
        dotMenuLayout=(RelativeLayout) this.findViewById(R.id.dotMenu);
        dotMenuLayout.setVisibility(RelativeLayout.GONE);

        TextView goldenPoints = (TextView) findViewById(R.id.goldenPoints);
        goldenPoints.setTextColor(ContextCompat.getColor(this, R.color.colorGold));
        goldenPoints.setText(intent.getStringExtra("score"));

        TextView text = (TextView) findViewById(R.id.goldenText);
        text.setText(fromHtml(intent.getStringExtra("foundText")));



        SharedPreferences tourValues = getSharedPreferences(TOUR_VALUES, 0);
        int currentScore = Integer.parseInt(tourValues.getString("currentScore", null));
        int nutsCollected = Integer.parseInt(tourValues.getString("nutsCollected", null));
        currentScore += Integer.parseInt(intent.getStringExtra("score"));
        selectedTour = Integer.parseInt(tourValues.getString("tourID", null));

        //change currentScore preference value

        SharedPreferences.Editor editor = tourValues.edit();
        editor.putString("currentScore", Integer.toString(currentScore));
        editor.commit();

        int totalAmountsOfNuts = Integer.parseInt(intent.getStringExtra("totalAmountOfNuts"));

        int allNuts = 4;


        for (int i = 0; i < allNuts; i++) {
            String nameImageView = "nut" + (i + 1);
            int imageViewID = getResources().getIdentifier(nameImageView, "id", getPackageName());
            ImageView nutsImages = (ImageView) findViewById(imageViewID);
            if (i >= totalAmountsOfNuts || totalAmountsOfNuts == 1) {
                nutsImages.setVisibility(View.GONE);
            }
        }


        for (int i = 0; i < nutsCollected; i++) {
            String nameImageView = "nut" + (i + 1);
            int imageViewID = getResources().getIdentifier(nameImageView, "id", getPackageName());
            ImageView nutsImages = (ImageView) findViewById(imageViewID);
            nutsImages.setImageResource(R.drawable.ic_zirblnut_golden);
        }



    }

    public void backToNavigation(View view) {

        this.finish();
    }

    private void showEndTourDialog() {
        this.runOnUiThread(new Runnable() {
            public void run() {
                EndTourDialog alertEnd = new EndTourDialog(mContext, selectedTour);
                alertEnd.showDialog((Activity) mContext);
            }
        });
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
