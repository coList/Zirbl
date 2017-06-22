package hsaugsburg.zirbl001.NavigationActivities.QrCode;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.Utils.BottomNavigationViewHelper;

public class QrActivity extends AppCompatActivity {

    private static final String TAG = "QrActivity";
    private static final int ACTIVITY_NUM = 2;

    private Context mContext = QrActivity.this;

    TextView barcodeResult;

    private int tourID;
    private int klassenID;
    private String klasse;
    private String school;

    private String barcodeValue;

    //Animation beim Activity wechsel verhindern
    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);
        Log.d(TAG, "onCreate: starting");
        Toolbar toolbar = (Toolbar) findViewById(R.id.standard_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("QR-Code Scanner");

        setupBottomNavigationView();
        
        barcodeResult = (TextView) findViewById(R.id.barcode_result);

    }

    public void scanBarcode (View v){
        Intent intent1 = new Intent(QrActivity.this, ScanBarcodeActivity.class);
        startActivityForResult(intent1, 0);
    }


    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);

        //Richtiges Icon hovern
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    public void setBarcodeInfos(){

        String[] splited = barcodeValue.split("\\s+");

        tourID = Integer.parseInt(splited[0]);
        //klassenID = 0;
        klasse = splited[1];
        //fuegt Schulnamen wieder zusammen
        for (int i=2;i<splited.length;i++){
            school = school + " " + splited[i];
        }

    }


    @Override
    public  void onActivityResult (int requestCode, int resultCode, Intent data) {
        if(requestCode==0){
            if(resultCode == CommonStatusCodes.SUCCESS){
                if(data != null) {
                    Barcode barcode = data.getParcelableExtra("barcode");
                    //barcodeResult.setText("Barcode value : "+ barcode.displayValue);


                    barcodeValue = barcode.displayValue;
                    Log.d(TAG, "onActivityResult: " +barcodeValue);


                } else {
                    barcodeResult.setText("Ich nix finden");
                }
            }


        } else {

            super.onActivityResult(requestCode, resultCode, data);
        }


    }
}
