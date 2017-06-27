package hsaugsburg.zirbl001.NavigationActivities.QrCode;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import hsaugsburg.zirbl001.R;

//import com.google.android.gms.common.server.converter.StringToIntConverter;

public class ScanBarcodeActivity extends AppCompatActivity{

    private static final String TAG = "ScanBarcodeActivity";

    SurfaceView cameraPreview;
    private Context mContext = ScanBarcodeActivity.this;
    private String errorMessage = "Das Scannen des QR-Codes hat leider nicht funktioniert.";
    private String successMessage = "";

    private int tourID;
    private String tourName;
    private int klassenID;
    private String klasse;
    private String school;
    private String zirblIdent;

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_barcode);

        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);
        createCameraSource();

    }

    public void saveScanInfos(String scanValue){

        //String muss wie folgt ausschauen: ZirblIdent, TourID, Tourname, KlassenID, Klassenname, Schulname

        String[] splited = scanValue.split(";");
        Log.d(TAG, "saveScanInfos: " + scanValue);

        zirblIdent = splited[0];

        if(zirblIdent.equals("qrcodezirbl")){
            tourID = Integer.parseInt(splited[1]);
            tourName = splited[2];
            Log.d(TAG, "saveScanInfos: " + tourName);
            klassenID = Integer.parseInt(splited[3]);
            klasse = splited[4];
            school = splited[5];
        }
    }

    private void generateSuccessMessage(){

        successMessage = "Willkommen bei der ";
        successMessage += tourName;
        successMessage += "-Tour: \n";
        successMessage += "Klasse " + klasse + ", ";
        successMessage += school;

    }

    private void showScanDialogSuccess(){
        generateSuccessMessage();

        this.runOnUiThread(new Runnable() {
            public void run() {
                QrDialog alertSuccess = new QrDialog(mContext, true, "WEITER");
                alertSuccess.showDialog((Activity) mContext, successMessage, "Viel Spaß bei der Tour!", tourID);
            }
        });
    }

    private void showScanDialogFail(){

        this.runOnUiThread(new Runnable() {
            public void run() {
                QrDialog alertFail = new QrDialog(mContext,false,"NOCHMAL");
                alertFail.showDialog((Activity) mContext, errorMessage, "Versuche es erneut!", tourID);
            }
        });
    }


    private void createCameraSource() {

        final BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
        final CameraSource cameraSource = new CameraSource.Builder(this, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600, 1024)
                .build();

        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                if (ActivityCompat.checkSelfPermission(ScanBarcodeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                try {
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if(barcodes.size()>0){

                    saveScanInfos(barcodes.valueAt(0).displayValue);

                    if(zirblIdent.equals("qrcodezirbl")){
                        showScanDialogSuccess();
                    } else {
                        showScanDialogFail();
                    }

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            cameraSource.stop();
                        }
                    });
                }
            }
        });

    }
}
