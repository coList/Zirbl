package hsaugsburg.zirbl001.NavigationActivities.QrCode;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.TourActivities.TourstartActivity;

public class QrDialog {

    private static final String TAG = "QrDialog";

    private final Context context;
    private final boolean success;
    private final String textButtonMarked;

    private int tourID;


    public QrDialog(Context context, boolean success, String textButtonMarked){
        this.context = context;
        this.success = success;
        this.textButtonMarked = textButtonMarked;
    }

    public void showDialog(Activity activity, String msg, String msg2, final int tourID){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        if(success){
            dialog.setContentView(R.layout.dialog_one_button);
        }else{
            dialog.setContentView(R.layout.dialog_two_buttons);
        }


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.title_container);


        TextView text = (TextView) dialog.findViewById(R.id.text_dialog_top);
        text.setText(msg);
        TextView text2 = (TextView) dialog.findViewById(R.id.text_dialog_bottom);
        text2.setText(msg2);

        if(success){
            TextView title = (TextView) dialog.findViewById(R.id.title);
            title.setText("SCAN ERFOLGREICH");
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorTurquoise));
        }else{
            TextView title = (TextView) dialog.findViewById(R.id.title);
            title.setText("SCAN FEHLGESCHLAGEN");
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
        }


        Button dialogButtonAgain = (Button) dialog.findViewById(R.id.btn_marked);
        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        dialogButtonAgain.setText(textButtonMarked);
        if(success){
            dialogButtonAgain.setTextColor(context.getResources().getColor(R.color.colorTurquoise));
            Log.d(TAG, "showDialog: ich bin türkis");
        }else {
            dialogButtonAgain.setTextColor(context.getResources().getColor(R.color.colorRed));
            Log.d(TAG, "showDialog: ich bin rot");
        }
        dialogButtonAgain.setTypeface(typeface1);
        dialogButtonAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(success){
                    Intent intent = new Intent (context, TourstartActivity.class);
                    intent.putExtra("tourID", Integer.toString(tourID));
                    context.startActivity(intent);
                    Log.d(TAG, "onClick: starte die Tour");
                }else{
                    Intent intent = new Intent (context, ScanBarcodeActivity.class);
                    context.startActivity(intent);
                    Log.d(TAG, "onClick: versuch es erneut");
                }

            }
        });

        if(!success){
            Button dialogButtonEnd = (Button) dialog.findViewById(R.id.btn_end);
            Typeface typeface2 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
            dialogButtonEnd.setText("BEENDEN");
            dialogButtonEnd.setTypeface(typeface2);
            dialogButtonEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (context, QrActivity.class);
                    context.startActivity(intent);
                }
            });
        }
        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }

}