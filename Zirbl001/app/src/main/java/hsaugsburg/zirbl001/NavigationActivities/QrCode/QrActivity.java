package hsaugsburg.zirbl001.NavigationActivities.QrCode;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.lang.reflect.Field;

import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONClasses;
import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONTourFavor;
import hsaugsburg.zirbl001.Interfaces.InternetActivity;
import hsaugsburg.zirbl001.NavigationActivities.NoConnectionDialog;
import hsaugsburg.zirbl001.NavigationActivities.Profile.ProfileClassFragment;
import hsaugsburg.zirbl001.NavigationActivities.Profile.ProfileOwnFragment;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;
import hsaugsburg.zirbl001.Utils.TabSectionPagerAdapter;

public class QrActivity extends AppCompatActivity implements InternetActivity {
    private static final String TAG = "QrActivity";
    private static final int ACTIVITY_NUM = 2;

    private Context mContext = QrActivity.this;
    private int position = 0;
    private TabSectionPagerAdapter adapter;

    public static final String GLOBAL_VALUES = "globalValuesFile";

    //Animation beim Activity wechsel verhindern
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("QR-Code Scanner");

        TextView actionbarText;
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            actionbarText = (TextView) f.get(toolbar);
            actionbarText.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf"));
            actionbarText.setAllCaps(true);
            actionbarText.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            actionbarText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        setupBottomNavigationView();

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            position = extras.getInt("viewpager_position");
        }
        adapter = new TabSectionPagerAdapter(getSupportFragmentManager());
        setupViewPager();

        if (!isOnline()) {
            NoConnectionDialog noConnectionDialog = new NoConnectionDialog(this);
            noConnectionDialog.showDialog(this);
        }
    }

    public void scanBarcode (View v) {
        Intent intent = new Intent(QrActivity.this, ScanBarcodeActivity.class);
        startActivityForResult(intent, 0);
    }

    public boolean isOnline() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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
            String userName = globalValues.getString("userName", null);
            String deviceToken = globalValues.getString("deviceToken", null);
            new JSONClasses((QrSavedFragment) adapter.getItem(1), userName, deviceToken).execute(serverName + "/api2/selectClassesView.php");
        }
    }

    private void setupBottomNavigationView() {
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        //Richtiges Icon hovern
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    // Responsible for adding the 2 tabs: Scannen, Gespeicherte
    private void setupViewPager() {
        adapter.addFragment(new QrScanFragment());
        adapter.addFragment(new QrSavedFragment());

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.getTabAt(0).setText("Scannen");
        tabLayout.getTabAt(1).setText("Gespeicherte");
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

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }
}
