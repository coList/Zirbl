package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import hsaugsburg.zirbl001.Models.TourSelectionModel;
import hsaugsburg.zirbl001.R;

public class ClassRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "ClassRegistrationActivity";
    private Context mContext = ClassRegistrationActivity.this;


    //Animation beim Activity wechsel verhindern
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_registration);
        Log.d(TAG, "onCreate: starting");

        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Klasse Anmelden");

        Spinner spinnerGrade = (Spinner) findViewById(R.id.spinnerGrade);
        ArrayAdapter<CharSequence> adapterGrade = ArrayAdapter.createFromResource(this,
                R.array.gradeInSchool, android.R.layout.simple_spinner_item);
        adapterGrade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGrade.setAdapter(adapterGrade);

        Spinner spinnerClass = (Spinner) findViewById(R.id.spinnerClass);
        ArrayAdapter<CharSequence> adapterClass = ArrayAdapter.createFromResource(this,
                R.array.classInSchool, android.R.layout.simple_spinner_item);
        adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerClass.setAdapter(adapterClass);
    }

    public void generateQrCode (View view){

        Intent intent = new Intent(mContext, GenerateQrCodeActivity.class);
        startActivity(intent);

    }
}
