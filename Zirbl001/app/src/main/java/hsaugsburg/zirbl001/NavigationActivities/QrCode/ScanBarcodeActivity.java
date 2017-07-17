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

public class ScanBarcodeActivity extends AppCompatActivity{

    private static final String TAG = "ScanBarcodeActivity";

    SurfaceView cameraPreview;
    private Context mContext = ScanBarcodeActivity.this;
    private String errorMessage = "Das Scannen des QR-Codes hat leider nicht funktioniert.";
    private String successMessage = "";
    String downloadMessage = "Lade dir die <b>Tour</b> jetzt herunter, um <b>Wartezeiten</b> während der Tour zu <b>vermeiden</b>.";

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

        String[] splited = scanValue.split(";");

        zirblIdent = splited[0];

        if(zirblIdent.equals("qrcodezirbl")){
            tourID = Integer.parseInt(splited[1]);
            tourName = splited[2];
            klasse = splited[3];
            school = splited[4];
            klassenID = Integer.parseInt(splited[5]);
        }
    }

    private void generateSuccessMessage(){

        successMessage = "Willkommen bei der ";
        successMessage += tourName;
        successMessage += "-Tour: <br />";
        successMessage += "<b>Klasse " + klasse + ", ";
        successMessage += school +"</b>";

    }

    private void showScanDialogSuccess(){
        generateSuccessMessage();

        this.runOnUiThread(new Runnable() {
            public void run() {
                QrDialog alertSuccess = new QrDialog(mContext, true, "DOWNLOAD", klassenID, tourID);
                alertSuccess.showDialog((Activity) mContext, successMessage, downloadMessage, "Viel Spaß bei der Tour!");
            }
        });
    }

    private void showScanDialogFail(){

        this.runOnUiThread(new Runnable() {
            public void run() {
                QrDialog alertFail = new QrDialog(mContext,false,"NOCHMAL", klassenID, tourID);
                alertFail.showDialog((Activity) mContext, errorMessage, null, "Versuche es erneut!");
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
