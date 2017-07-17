package hsaugsburg.zirbl001.TourActivities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.vision.text.Text;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import hsaugsburg.zirbl001.Datamanagement.UploadTasks.InsertIntoClass;
import hsaugsburg.zirbl001.NavigationActivities.HomeActivity;
import hsaugsburg.zirbl001.NavigationActivities.QrCode.QrActivity;
import hsaugsburg.zirbl001.NavigationActivities.QrCode.QrSavedFragment;
import hsaugsburg.zirbl001.R;

import static android.R.attr.fragment;
import static android.R.attr.width;
import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class GenerateQrCodeActivity extends AppCompatActivity {

    private static final String TAG = "GenerateQrCodeActivity";
    private Context mContext = GenerateQrCodeActivity.this;

    public String qrString;

    private int tourID;
    private String tourName;
    private String grade;
    private String school;

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
        Log.d(TAG, "onCreate: starting");
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
        tourID = Integer.parseInt(intent.getStringExtra("tourID"));
        tourName = intent.getStringExtra("tourName");
        school = intent.getStringExtra("school");
        grade = intent.getStringExtra("className");
        qrString = intent.getStringExtra("qrCode");

        TextView qrClass = (TextView) findViewById(R.id.qrClass);
        TextView qrSchool = (TextView) findViewById(R.id.qrSchool);

        qrClass.setText(grade);
        qrSchool.setText(school);

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
