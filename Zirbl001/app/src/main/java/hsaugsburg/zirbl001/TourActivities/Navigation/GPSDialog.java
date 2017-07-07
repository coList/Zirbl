package hsaugsburg.zirbl001.TourActivities.Navigation;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import hsaugsburg.zirbl001.NavigationActivities.QrCode.QrActivity;
import hsaugsburg.zirbl001.NavigationActivities.QrCode.ScanBarcodeActivity;
import hsaugsburg.zirbl001.R;

public class GPSDialog {

    private static final String TAG = "GPSDialog";
    private final Context context;
    private Activity activity;

    private String title = "GPS aktivieren";
    private String message = "Schalte dein GPS ein, um die Navigation nutzen zu können.";

    public GPSDialog(Context context) {
        this.context = context;
    }

    public void showDialog() {
        this.activity = (Activity) context;

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        dialog.setContentView(R.layout.dialog_two_buttons);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.title_container);
        TextView titleText = (TextView) dialog.findViewById(R.id.title);
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        TextView text = (TextView) dialog.findViewById(R.id.text_dialog_top);
        text.setText(message);

        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setText(this.title);
        linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorTurquoise));


        Button dialogButtonEnable = (Button) dialog.findViewById(R.id.btn_marked);
        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        dialogButtonEnable.setText("AKTIVIEREN");
        dialogButtonEnable.setTextColor(context.getResources().getColor(R.color.colorTurquoise));
        dialogButtonEnable.setTypeface(typeface1);

        dialogButtonEnable.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                activity.startActivity(intent);
            }
        });

        Button dialogButtonEnd = (Button) dialog.findViewById(R.id.btn_end);
        Typeface typeface2 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        dialogButtonEnd.setText("ABBRECHEN");
        dialogButtonEnd.setTypeface(typeface2);

        dialogButtonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

            }
        });

        //checkGPS(dialog);
        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }

    public void checkGPS(Dialog dialog) {

        LocationManager locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            dialog.cancel();
        }
    }
}
