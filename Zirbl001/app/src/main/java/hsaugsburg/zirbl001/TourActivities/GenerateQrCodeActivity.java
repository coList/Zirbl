package hsaugsburg.zirbl001.TourActivities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.lang.reflect.Field;

import hsaugsburg.zirbl001.NavigationActivities.QrCode.QrActivity;
import hsaugsburg.zirbl001.R;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class GenerateQrCodeActivity extends AppCompatActivity {

    private static final String TAG = "GenerateQrCodeActivity";
    private Context mContext = GenerateQrCodeActivity.this;

    public String qrString;

    public static final String GLOBAL_VALUES = "globalValuesFile";


    //Animation beim Activity wechsel verhindern
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr_code);
        setIntentExtras();




    }


    public void setQrCode() {
        ImageView imageView = (ImageView) findViewById(R.id.qrCode);

        try {
            Bitmap bitmap = encodeAsBitmap(qrString);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }


    public void setIntentExtras(){
        Intent intent = getIntent();
        String school = intent.getStringExtra("school");
        String grade = intent.getStringExtra("className");
        qrString = intent.getStringExtra("qrCode");
        String tourName = intent.getStringExtra("tourName");

        TextView qrClass = (TextView) findViewById(R.id.qrClass);
        TextView qrSchool = (TextView) findViewById(R.id.qrSchool);

        String gradeText = "Klasse " + grade;
        qrClass.setText(gradeText);
        qrSchool.setText(school);

        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(tourName);

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

        setQrCode();
    }


    public void continueToSavedQrCodes(View view) {
        Intent intent = new Intent(this, QrActivity.class);
        intent.putExtra("viewpager_position", 1);
        startActivity(intent);
    }

    Bitmap encodeAsBitmap(String str) throws WriterException {

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;

        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, width, width, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        bitmap.setPixels(pixels, 0, width, 0, 0, w, h);
        return bitmap;
    }
}
