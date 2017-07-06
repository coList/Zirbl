package hsaugsburg.zirbl001.NavigationActivities.Search;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.json.JSONException;

import java.lang.reflect.Field;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.JSONStationLocation;
import hsaugsburg.zirbl001.Datamanagement.JSONTourSelection;
import hsaugsburg.zirbl001.Datamanagement.SearchSelectionAdapter;
import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Models.MapModels.StationModel;
import hsaugsburg.zirbl001.Models.TourSelectionModel;
import hsaugsburg.zirbl001.NavigationActivities.TourDetailActivity;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;

import static android.support.constraint.R.id.parent;

public class SearchActivity extends AppCompatActivity implements Callback{

    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM = 1;

    private Context mContext = SearchActivity.this;

    private ListView mListView;
    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;
    private SearchSelectionAdapter adapter;


    //Animation beim Activity wechsel verhindern
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: starting");
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("SUCHE");

        TextView actionbarText = null;
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            actionbarText = (TextView) f.get(toolbar);
            actionbarText.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf"));
            actionbarText.setAllCaps(true);
            actionbarText.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            actionbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        } catch (NoSuchFieldException e) {
        }
        catch (IllegalAccessException e) {
        }

        setupBottomNavigationView();

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);
        new JSONTourSelection(this).execute(serverName + "/api/selectTourSelectionView.php");
        mListView = (ListView) findViewById(R.id.search_list_view);

    }


    public void processData(StationModel result) throws JSONException {
        /*
        double lat =  result.getLatitude();
        double lng = result.getLongitude();

        Log.d(TAG, "lat: " + String.valueOf(lat));
        Log.d(TAG, "long:" + String.valueOf(lng));
        */
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_view_menu_item, menu);
        MenuItem searchViewItem = menu.findItem(R.id.action_search);
        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                //Hier muss die Liste gefiltert werden
                adapter.getFilter().filter(newText);

                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
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


        adapter = new SearchSelectionAdapter(this, result);
        mListView.setAdapter(adapter);
        final List<JSONModel> tourSelectionItems = result;


        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONModel selectedTour = tourSelectionItems.get(position);

                //changeToInfo(((TourSelectionModel)selectedTour).getTourID());

                Intent intent1 = new Intent(mContext, TourDetailActivity.class);
                intent1.putExtra("tourID", Integer.toString(((TourSelectionModel)selectedTour).getTourID()));
                intent1.putExtra("tourName", ((TourSelectionModel)selectedTour).getTourName());
                startActivity(intent1);
            }
        });


    }
}
