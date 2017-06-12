package hsaugsburg.zirbl001;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class TourstartActivity extends AppCompatActivity {

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourstart);
    }
}
