package hsaugsburg.zirbl001.TourActivities;

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

import hsaugsburg.zirbl001.NavigationActivities.HomeActivity;
import hsaugsburg.zirbl001.NavigationActivities.QrCode.QrActivity;
import hsaugsburg.zirbl001.R;

public class EndTourDialog {

    private static final String TAG = "EndTourDialog";
    private static final String MSG = "Möchstest du die Tour wirklich beenden?";
    private static final String MSG2 = "Dein Spielstand wird dabei nicht gespeichert.";

    private final Context context;


    public EndTourDialog(Context context){
        this.context = context;
    }

    public void showDialog(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_two_buttons);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.title_container);
        linearLayout.setVisibility(View.GONE);


        TextView text = (TextView) dialog.findViewById(R.id.text_dialog_top);
        text.setText(MSG);
        TextView text2 = (TextView) dialog.findViewById(R.id.text_dialog_bottom);
        text2.setText(MSG2);


        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setText("SCAN ERFOLGREICH");
        linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorTurquoise));


        Button dialogButtonAgain = (Button) dialog.findViewById(R.id.btn_marked);
        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        dialogButtonAgain.setText("NEIN");
        dialogButtonAgain.setTextColor(context.getResources().getColor(R.color.colorTurquoise));
        Log.d(TAG, "showDialog: ich bin türkis");

        dialogButtonAgain.setTypeface(typeface1);
        dialogButtonAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        Button dialogButtonEnd = (Button) dialog.findViewById(R.id.btn_end);
        Typeface typeface2 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        dialogButtonEnd.setText("JA");
        dialogButtonEnd.setTypeface(typeface2);
        dialogButtonEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (context, HomeActivity.class);
                context.startActivity(intent);
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }

}