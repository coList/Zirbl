package hsaugsburg.zirbl001.TourActivities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import hsaugsburg.zirbl001.R;

public class ClassRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_registration);

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
}
