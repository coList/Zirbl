package hsaugsburg.zirbl001;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class BaseActivity extends AppCompatActivity {

    //Animation bei Activitywechsel verhindern
    @Override
    public void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    //Wechseln der Activity bei klick in Tabbar
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    Intent i_tabbar = new Intent(getApplicationContext(), Tabbar.class);
                    startActivity(i_tabbar);
                    return true;
                case R.id.navigation_search:
                    Intent i_search = new Intent(getApplicationContext(), Search.class);
                    startActivity(i_search);
                    return true;
                case R.id.navigation_qr:
                    Intent i_qr = new Intent(getApplicationContext(), Qr.class);
                    startActivity(i_qr);
                    return true;
                case R.id.navigation_fav:
                    Intent i_fav = new Intent(getApplicationContext(), Favorite.class);
                    startActivity(i_fav);
                    return true;
                case R.id.navigation_profile:
                    Intent i_profile = new Intent(getApplicationContext(), Profile.class);
                    startActivity(i_profile);
                    return true;
            }
            return true;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);


        //Tabbar aus externer Library erstellen
        BottomNavigationViewEx bnve = (BottomNavigationViewEx) findViewById(R.id.bnve);
        bnve.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        //Alle Animationen und Text ausschalten
        bnve.enableAnimation(false);
        bnve.enableShiftingMode(false);
        bnve.enableItemShiftingMode(false);
        bnve.setTextVisibility(false);


    }
}