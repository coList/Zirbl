package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import hsaugsburg.zirbl001.Fonts.QuicksandBoldPrimaryButton;
import hsaugsburg.zirbl001.Fonts.QuicksandBoldPrimaryView;
import hsaugsburg.zirbl001.R;

public class TourstartActivity extends AppCompatActivity {
    private int maxAmountOfParticipants = 10;

    private Context mContext = TourstartActivity.this;

    private int count = 0;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tourstart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Anmeldung");

        ImageButton addParticipant = (ImageButton) findViewById(R.id.plusButton);
        addParticipant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (count < maxAmountOfParticipants - 1) {
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.userInput);
                    EditText participantField = new EditText(getApplicationContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                    float d = getResources().getDisplayMetrics().density;

                    params.leftMargin = (int) (24 * d);
                    params.rightMargin = (int) (24 * d);

                    //TODO: set inputType, set backgroundTint instead of backgroundcolor, set textCursorDrawable
                    int colorId = ContextCompat.getColor(getApplicationContext(), R.color.colorTransparent);
                    participantField.setTextColor(colorId);
                    participantField.setHint("Gruppenmitglied");
                    participantField.setCursorVisible(true);
                    participantField.setBackgroundColor(0);
                    participantField.setEms(10);
                    participantField.setLayoutParams(params);
                    linearLayout.addView(participantField);
                    count++;
                }
            }
        });
    }

    public void goIntoTour(View view) {
        Intent intent = new Intent(mContext, NavigationActivity.class);
        startActivity(intent);
    }
}
