package hsaugsburg.zirbl001.NavigationActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.List;

import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Datamanagement.JSONTourSelection;
import hsaugsburg.zirbl001.Models.TourSelectionModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Datamanagement.TourSelectionAdapter;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;

public class HomeActivity extends AppCompatActivity implements Callback {

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;

    private Context mContext = HomeActivity.this;
    private ListView mListView;

    //Animation beim Activity wechsel verhindern
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: starting");
        getSupportActionBar().setTitle("Touren");

        setupBottomNavigationView();

        new JSONTourSelection(this).execute("http://zirbl.multimedia.hs-augsburg.de/selectTourSelectionView.php");
        mListView = (ListView) findViewById(R.id.home_list_view);
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


        TourSelectionAdapter adapter = new TourSelectionAdapter(this, result);
        mListView.setAdapter(adapter);
        final List<JSONModel> tourSelectionItems = result;

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONModel selectedTour = tourSelectionItems.get(position);

                //changeToInfo(((TourSelectionModel)selectedTour).getTourID());

                Intent intent1 = new Intent(mContext, TourDetailActivity.class);
                intent1.putExtra("tourID", Integer.toString(((TourSelectionModel)selectedTour).getTourID()));
                startActivity(intent1);
            }
        });



    }
}
