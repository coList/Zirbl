package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import hsaugsburg.zirbl001.Models.TourSelectionModel;
import hsaugsburg.zirbl001.R;

public class ClassRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "ClassRegistrationActivity";
    private Context mContext = ClassRegistrationActivity.this;

    private int tourID;
    private String tourName;
    private String klasse;
    private String school;


    //Animation beim Activity Wechsel verhindern
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

        NumberPicker npClassnumber = (NumberPicker) findViewById(R.id.classletter);
        NumberPicker npGrade = (NumberPicker) findViewById(R.id.grade);
        TextView tvInfo = (TextView) findViewById(R.id.qrCodeInfo);

        tvInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        final String[] valuesClassnumber= {"a","b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"};
        final String[] valuesGrade= {"5", "6", "7", "8", "9", "10", "11", "12", "13"};

        npClassnumber.setMinValue(0);
        npGrade.setMinValue(0);

        npClassnumber.setMaxValue(valuesClassnumber.length-1);
        npGrade.setMaxValue(valuesGrade.length-1);

        npClassnumber.setDisplayedValues(valuesClassnumber);
        npGrade.setDisplayedValues(valuesGrade);

        npClassnumber.setWrapSelectorWheel(true);
        npGrade.setWrapSelectorWheel(true);
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
        NumberPicker npGrade = (NumberPicker)findViewById(R.id.grade);
        NumberPicker npClass = (NumberPicker)findViewById(R.id.classletter);
        EditText etSchool = (EditText) findViewById(R.id.school);

        klasse = " " + npGrade.getValue() + npClass.getValue();
        school = etSchool.getText().toString();

    }

}
