package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Field;

import hsaugsburg.zirbl001.R;

public class ClassRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "ClassRegistrationActivity";
    private Context mContext = ClassRegistrationActivity.this;

    private int tourID;
    private String tourName;
    private String className;
    private String school;

    public final String[] valuesClassnumber= {"a","b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l"};
    public final String[] valuesGrade= {"5", "6", "7", "8", "9", "10", "11", "12", "13"};


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

        // ActionBar Font...zz nur auf dieser Seite
        TextView yourTextView = null;
        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            yourTextView = (TextView) f.get(toolbar);
            yourTextView.setTypeface(Typeface.createFromAsset(mContext.getAssets(), "fonts/OpenSans-Bold.ttf"));
            yourTextView.setAllCaps(true);
            yourTextView.setTextColor(ContextCompat.getColor(mContext, R.color.colorAccent));
            yourTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            } catch (NoSuchFieldException e) {
        }
        catch (IllegalAccessException e) {
        }
        //

        NumberPicker npClassnumber = (NumberPicker) findViewById(R.id.classletter);
        NumberPicker npGrade = (NumberPicker) findViewById(R.id.grade);
        TextView tvInfo = (TextView) findViewById(R.id.qrCodeInfo);

        tvInfo.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        npClassnumber.setMinValue(1);
        npGrade.setMinValue(1);

        npClassnumber.setMaxValue(valuesClassnumber.length);
        npGrade.setMaxValue(valuesGrade.length);

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
        if(className != null && !className.isEmpty() && school !=null && !school.isEmpty()){
            Intent intent = new Intent(mContext, GenerateQrCodeActivity.class);
            intent.putExtra("tourID", Integer.toString(tourID));
            intent.putExtra("tourName", tourName);
            intent.putExtra("className", className);
            intent.putExtra("school", school);
            startActivity(intent);
            speechBubble.setImageResource(R.drawable.img_zirbl_speech_bubble_class);
        } else {
            Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
            findViewById(R.id.continueButton).startAnimation(shake);
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
            speechBubble.setImageResource(R.drawable.img_zirbl_speech_bubble_class_fail);
        }
    }

    public void setInput(){
        NumberPicker npGrade = (NumberPicker)findViewById(R.id.grade);
        NumberPicker npClass = (NumberPicker)findViewById(R.id.classletter);
        Log.d(TAG, "Classletter: " + valuesClassnumber[npClass.getValue()-1]);
        EditText etSchool = (EditText) findViewById(R.id.school);

        className = valuesGrade[npGrade.getValue()-1] + valuesClassnumber[npClass.getValue()-1];
        school = etSchool.getText().toString();
        Log.d(TAG, "setInput: " + className);

    }

}
