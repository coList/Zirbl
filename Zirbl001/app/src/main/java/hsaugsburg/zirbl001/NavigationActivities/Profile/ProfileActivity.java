package hsaugsburg.zirbl001.NavigationActivities.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.lang.reflect.Field;

import hsaugsburg.zirbl001.Datamanagement.DownloadTasks.DownloadIsTourFavorised;
import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONClassStatistics;
import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONOwnStatistics;
import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONTourDetail;
import hsaugsburg.zirbl001.Interfaces.InternetActivity;
import hsaugsburg.zirbl001.NavigationActivities.ImpressumActivity;
import hsaugsburg.zirbl001.NavigationActivities.NoConnectionDialog;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;
import hsaugsburg.zirbl001.Utils.TabSectionPagerAdapter;

public class ProfileActivity extends AppCompatActivity implements InternetActivity {

    private static final String TAG = "ProfileActivity";
    private static final int ACTIVITY_NUM = 4;

    private Context mContext = ProfileActivity.this;

    public static final String GLOBAL_VALUES = "globalValuesFile";

    private TabSectionPagerAdapter adapter;

    //Animation beim Activity wechsel verhindern
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Mein Bereich");

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

        adapter = new TabSectionPagerAdapter((getSupportFragmentManager()));
        setupViewPager();


        if (!isOnline()) {
            NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
            noConnectionDialog.showDialog(this);
        }


    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void tryConnectionAgain() {
        if (!isOnline()) {
            NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
            noConnectionDialog.showDialog(this);
        } else {
            SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
            String serverName = globalValues.getString("serverName", null);
            String username = globalValues.getString("userName", null);


            RelativeLayout relativeLayoutOwnStatistics = (RelativeLayout) findViewById(R.id.noOwnStats);
            relativeLayoutOwnStatistics.setVisibility(View.GONE);
            new JSONOwnStatistics((ProfileOwnFragment) adapter.getItem(0), username).execute(serverName + "/api/selectOwnStatisticsView.php");


            RelativeLayout relativeLayoutClassStatistics = (RelativeLayout) findViewById(R.id.noClassStats);
            relativeLayoutClassStatistics.setVisibility(View.GONE);
            new JSONClassStatistics((ProfileClassFragment)adapter.getItem(1), username).execute(serverName + "/api/selectClassStatisticsView.php");
        }

    }

    // Responsible for adding the 2 tabs: Eigene Statistik, Klassen Statistik
    private void setupViewPager(){
        adapter.addFragment(new ProfileOwnFragment());
        adapter.addFragment(new ProfileClassFragment());

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Eigene Statistik");
        tabLayout.getTabAt(1).setText("Klassenstatistik");
        changeTabsFont();

    }

    private void changeTabsFont() {

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        Typeface mTypeface = Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf");

        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(mTypeface, Typeface.BOLD);
                }
            }
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_impressum:
                Intent intent1 = new Intent(mContext, ImpressumActivity.class);
                startActivity(intent1);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
