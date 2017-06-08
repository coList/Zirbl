package hsaugsburg.zirbl001;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


public class BaseActivity extends AppCompatActivity {

    FragmentPagerAdapter adapterViewPager;

    //Animation bei Activitywechsel verhindern
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        final ViewPager vpPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new BaseActivity.MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

        BottomNavigationViewEx bnve = (BottomNavigationViewEx) findViewById(R.id.bnve);

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                        case R.id.navigation_home:
                            getSupportActionBar().setTitle("Touren");
                            vpPager.setCurrentItem(0);
                            return true;
                        case R.id.navigation_search:
                            getSupportActionBar().setTitle("Suche");
                            vpPager.setCurrentItem(1);
                            return true;
                        case R.id.navigation_qr:
                            getSupportActionBar().setTitle("QR-Code");
                            vpPager.setCurrentItem(2);
                            return true;
                        case R.id.navigation_fav:
                            getSupportActionBar().setTitle("Favoriten");
                            vpPager.setCurrentItem(3);
                            return true;
                        case R.id.navigation_profile:
                            getSupportActionBar().setTitle("Profil");
                            vpPager.setCurrentItem(4);
                            return true;
                    }
                    return true;
                }
        };


        bnve.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        bnve.enableAnimation(false);
        bnve.enableShiftingMode(false);
        bnve.enableItemShiftingMode(false);
        bnve.setTextVisibility(false);


    }


    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 5;

        public MyPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return HomeFragment.newInstance(0, "Search");
                case 1:
                    return SearchFragment.newInstance(1, "Favorit");
                case 2:
                    return QrFragment.newInstance(2, "Categorie");
                case 3:
                    return FavoriteFragment.newInstance(3, "Profile");
                case 4:
                    return ProfileFragment.newInstance(4, "QRCode");
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return "Page " + position;
        }
    }
}