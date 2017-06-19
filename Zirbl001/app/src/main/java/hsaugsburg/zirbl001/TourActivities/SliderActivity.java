package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import hsaugsburg.zirbl001.R;

public class SliderActivity extends AppCompatActivity {

    private Context mContext = SliderActivity.this;

    private static SeekBar slider;
    private static TextView sliderCount;
    private static int sliderMax = 2000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slider);
    }

    public void continueToNextView(View view) {
        Intent intent = new Intent(mContext, NavigationActivity. class);
        startActivity(intent);
    }

    /*/Hat mal funktioniert...

    public void sliderSelection(){
        slider = (SeekBar)findViewById(R.id.slider);
        sliderCount = (TextView)findViewById(R.id.sliderCount);
        sliderCount.setText(" " + slider.getProgress());
        slider.setMax(sliderMax);

        slider.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {

                    int progress_value;

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        progress_value = progress;
                        sliderCount.setText(" " + progress);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                        sliderCount.setText(" " + progress_value);
                    }
                }
        );

    }
    /*/
}
