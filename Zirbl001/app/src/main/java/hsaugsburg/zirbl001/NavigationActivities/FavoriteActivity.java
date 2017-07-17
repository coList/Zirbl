package hsaugsburg.zirbl001.NavigationActivities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Field;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.JSONTourFavor;
import hsaugsburg.zirbl001.Datamanagement.JSONTourSelection;
import hsaugsburg.zirbl001.Datamanagement.SearchSelectionAdapter;
import hsaugsburg.zirbl001.Datamanagement.TourFavorAdapter;
import hsaugsburg.zirbl001.Datamanagement.TourSelectionAdapter;
import hsaugsburg.zirbl001.Interfaces.Callback;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Models.TourFavorModel;
import hsaugsburg.zirbl001.Models.TourSelectionModel;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;

public class FavoriteActivity extends AppCompatActivity implements Callback {

    private static final String TAG = "FavoriteActivity";
    private static final int ACTIVITY_NUM = 3;

    private Context mContext = FavoriteActivity.this;
    private ListView mListView;
    private TourFavorAdapter adapter;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;
    String userName;

    private ImageLoader imageLoader;

    //Animation beim Activity wechsel verhindern
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        Log.d(TAG, "onCreate: starting");
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Favoriten");

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

        SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);
        userName = globalValues.getString("userName", null);
        Log.d(TAG, "onCreate: "+serverName + "/api/selectFavoritesView.php?username="+userName);
        new JSONTourFavor(this).execute(serverName + "/api/selectFavoritesView.php?username="+userName);
        mListView = (ListView) findViewById(R.id.home_list_view);

        setupBottomNavigationView();
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

    @Override
    public void processData(List<JSONModel> result) {
        if (result != null) {

            adapter = new TourFavorAdapter(this, result, imageLoader);
            mListView.setAdapter(adapter);
            final List<JSONModel> tourFavorItems = result;

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Log.d(TAG, "onItemClick: " + Integer.toString(position));
                    JSONModel selectedTour = (JSONModel) adapter.getItem(position);

                    Intent intent1 = new Intent(mContext, TourDetailActivity.class);
                    intent1.putExtra("tourID", Integer.toString(((TourFavorModel) selectedTour).getTourID()));
                    intent1.putExtra("tourName", ((TourFavorModel) selectedTour).getTourName());
                    Log.d(TAG, "onItemClick: " + ((TourFavorModel) selectedTour).getTourName());
                    startActivity(intent1);
                }
            });

        }else {
            RelativeLayout rl = (RelativeLayout) this.findViewById(R.id.noFavs);
            rl.setVisibility(View.VISIBLE);
            Log.d(TAG, "processData: result ist nicht befüllt");
        }

    }
}
