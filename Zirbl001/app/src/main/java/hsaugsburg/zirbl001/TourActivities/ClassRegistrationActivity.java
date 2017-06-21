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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;

import hsaugsburg.zirbl001.Models.TourSelectionModel;
import hsaugsburg.zirbl001.R;

public class ClassRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "ClassRegistrationActivity";
    private Context mContext = ClassRegistrationActivity.this;

    private int tourID;
    private String tourName;
    private String klasse;
    private String school;


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

        setIntentExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Klasse Anmelden");


        Spinner spinnerGrade = (Spinner) findViewById(R.id.spinnerGrade);
        ArrayAdapter<CharSequence> adapterGrade = ArrayAdapter.createFromResource(mContext,
                R.array.gradeInSchool, R.layout.spinner_item);
        adapterGrade.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGrade.setAdapter(adapterGrade);

        Spinner spinnerClass = (Spinner) findViewById(R.id.spinnerClass);
        ArrayAdapter<CharSequence> adapterClass = ArrayAdapter.createFromResource(mContext,
                R.array.classInSchool, R.layout.spinner_item);
        adapterClass.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerClass.setAdapter(adapterClass);
    }

    public void setIntentExtras(){
        Intent intent = getIntent();
        tourID = Integer.parseInt(intent.getStringExtra("tourID"));
        tourName = intent.getStringExtra("tourName");
    }

    public void generateQrCode (View view){

        setInput();

        ImageView speechBubble = (ImageView) findViewById(R.id.registrationWelcome);
        if(klasse != null && !klasse.isEmpty() && school !=null && !school.isEmpty()){
            Intent intent = new Intent(mContext, GenerateQrCodeActivity.class);
            intent.putExtra("tourID", Integer.toString(tourID));
            intent.putExtra("tourName", tourName);
            intent.putExtra("klasse", klasse);
            intent.putExtra("school", school);
            startActivity(intent);
            speechBubble.setImageResource(R.drawable.zirbl_speech_bubble_class);
        } else {
            speechBubble.setImageResource(R.drawable.zirbl_speech_bubble_class_fail);
        }
    }

    public void setInput(){
        Spinner spGrade = (Spinner) findViewById(R.id.spinnerGrade);
        Spinner spKlasse = (Spinner) findViewById(R.id.spinnerClass);
        EditText etSchool = (EditText) findViewById(R.id.school);

        klasse = spGrade.getSelectedItem().toString() + spKlasse.getSelectedItem().toString();
        school = etSchool.getText().toString();

    }

}
