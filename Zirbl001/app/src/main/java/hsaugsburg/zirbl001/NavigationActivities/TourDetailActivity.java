package hsaugsburg.zirbl001.NavigationActivities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.DownloadImageTask;
import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Datamanagement.JSONTourDetail;
import hsaugsburg.zirbl001.Models.TourDetailModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.TourActivities.ClassRegistrationActivity;
import hsaugsburg.zirbl001.TourActivities.TourstartActivity;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;


public class TourDetailActivity extends AppCompatActivity implements Callback {

    private static final String TAG = "TourDetailActivity";
    private static final int ACTIVITY_NUM = 0;

    private Context mContext = TourDetailActivity.this;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourdetail);
        Log.d(TAG, "onCreate: starting");
        getSupportActionBar().setTitle("Touren");

        setupBottomNavigationView();

        new JSONTourDetail(this).execute("http://zirbl.multimedia.hs-augsburg.de/selectTourDetailsView.php");

        ImageButton button = (ImageButton)findViewById(R.id.go);
        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(mContext, TourstartActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    public void classRegistration(View view) {
        Intent intent = new Intent(mContext, ClassRegistrationActivity.class);
        startActivity(intent);
    }


    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        //Richtiges Icon hovern
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    public void processData(List<JSONModel> result) {
        Intent intent = getIntent();
        int tourID = Integer.parseInt(intent.getStringExtra("tourID"));

       // ((BaseActivity) getActivity()).setActionBarTitle(((TourDetailModel) result.get(tourID)).getTourName());


        TextView duration = (TextView) findViewById(R.id.durationText);
        duration.setText(Integer.toString(((TourDetailModel) result.get(tourID)).getDuration()) + " min");

        TextView distance = (TextView) findViewById(R.id.distanceText);
        double dist = ((TourDetailModel) result.get(tourID)).getDistance() / 1000.0;
        distance.setText(Double.toString(dist) + " km");

        TextView difficultyName = (TextView)findViewById(R.id.difficultyText);
        difficultyName.setText(((TourDetailModel) result.get(tourID)).getDifficultyName());

        TextView description = (TextView)findViewById(R.id.description);
        description.setText(((TourDetailModel) result.get(tourID)).getDescription());

        String mainPictureURL = ((TourDetailModel)result.get(tourID)).getMainPicture();
        new DownloadImageTask((ImageView) findViewById(R.id.image)).execute(mainPictureURL);
    }
}
