package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Vibrator;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.lang.reflect.Field;

import hsaugsburg.zirbl001.Datamanagement.UploadTasks.InsertIntoClass;
import hsaugsburg.zirbl001.Fonts.QuicksandRegularPrimaryEdit;
import hsaugsburg.zirbl001.R;

public class ClassRegistrationActivity extends AppCompatActivity {

    private static final String TAG = "ClassRegistrationActivity";
    private Context mContext = ClassRegistrationActivity.this;


    public static final String GLOBAL_VALUES = "globalValuesFile";

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

        setIntentExtras();

        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Klasse anmelden");

        QuicksandRegularPrimaryEdit schoolField = (QuicksandRegularPrimaryEdit) findViewById(R.id.school);
        ViewCompat.setBackgroundTintList(schoolField, ColorStateList.valueOf(ContextCompat.getColor(mContext, R.color.colorLine)));


        // ActionBar Font...zz nur auf dieser Seite
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
        //

        NumberPicker npClassnumber = (NumberPicker) findViewById(R.id.classletter);
        NumberPicker npGrade = (NumberPicker) findViewById(R.id.grade);

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
            String qrString = generateString();

            SharedPreferences globalValues = getSharedPreferences(GLOBAL_VALUES, 0);
            String serverName = globalValues.getString("serverName", null);
            String userName = globalValues.getString("userName", null);
            String deviceToken = globalValues.getString("deviceToken", null);

            new InsertIntoClass(userName, deviceToken, tourID, className, school, qrString, serverName, this).execute();


        } else {
            Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
            findViewById(R.id.createButton).startAnimation(shake);
            Vibrator vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibe.vibrate(100);
            speechBubble.setImageResource(R.drawable.img_zirbl_speech_bubble_class_fail);
        }
    }

    public void setQrCode(String result) {
        Intent intent = new Intent(mContext, GenerateQrCodeActivity.class);
        intent.putExtra("tourID", Integer.toString(tourID));
        intent.putExtra("tourName", tourName);
        intent.putExtra("className", className);
        intent.putExtra("school", school);
        intent.putExtra("qrCode", result);
        startActivity(intent);
        ImageView speechBubble = (ImageView) findViewById(R.id.registrationWelcome);
        speechBubble.setImageResource(R.drawable.img_zirbl_speech_bubble_class);
    }


    public String generateString(){
        return "qrcodezirbl" + ";" + tourID + ";" + tourName + ";" + className + ";" + school;
    }

    public void setInput(){
        NumberPicker npGrade = (NumberPicker)findViewById(R.id.grade);
        NumberPicker npClass = (NumberPicker)findViewById(R.id.classletter);
        EditText etSchool = (EditText) findViewById(R.id.school);

        className = valuesGrade[npGrade.getValue()-1] + valuesClassnumber[npClass.getValue()-1].toString();
        school = etSchool.getText().toString();

    }

}
