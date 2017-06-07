package hsaugsburg.zirbl001;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class InfoActivity extends AppCompatActivity {

    public static int clickedIconOnTabbar;



    public void startTour(View view) {
        Intent startTour = new Intent(getApplicationContext(), TourstartActivity.class);
        startActivity(startTour);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        getSupportActionBar().setTitle("Information");

        final TextView watch = (TextView) findViewById(R.id.watchTexttest);
        watch.setText("5 h");

        BottomNavigationViewEx bnve = (BottomNavigationViewEx) findViewById(R.id.bnve2);

        BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        Intent backHome = new Intent(getApplicationContext(), BaseActivity.class);
                        startActivity(backHome);
                        clickedIconOnTabbar = R.id.navigation_home;
                        return true;
                    case R.id.navigation_search:
                        Intent backSearch = new Intent(getApplicationContext(), BaseActivity.class);
                        startActivity(backSearch);
                        clickedIconOnTabbar = R.id.navigation_search;
                        return true;
                    case R.id.navigation_qr:
                        Intent backQR = new Intent(getApplicationContext(), BaseActivity.class);
                        startActivity(backQR);
                        clickedIconOnTabbar = R.id.navigation_qr;
                        return true;
                    case R.id.navigation_fav:
                        Intent backFav = new Intent(getApplicationContext(), BaseActivity.class);
                        startActivity(backFav);
                        clickedIconOnTabbar = R.id.navigation_fav;
                        return true;
                    case R.id.navigation_profile:
                        Intent backProfile = new Intent(getApplicationContext(), BaseActivity.class);
                        startActivity(backProfile);
                        clickedIconOnTabbar = R.id.navigation_profile;
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
}
