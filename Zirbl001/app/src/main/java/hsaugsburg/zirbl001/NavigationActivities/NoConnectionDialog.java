package hsaugsburg.zirbl001.NavigationActivities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import hsaugsburg.zirbl001.Datamanagement.JSONDownload.JSONTourSelection;
import hsaugsburg.zirbl001.Interfaces.InternetActivity;
import hsaugsburg.zirbl001.R;

public class NoConnectionDialog {
    private final Context context;
    private InternetActivity internetActivity;

    public NoConnectionDialog(InternetActivity internetActivity){
        this.context = (Context)internetActivity;
        this.internetActivity = internetActivity;
    }

    public void showDialog(Activity activity){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_one_button);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        LinearLayout linearLayout = (LinearLayout) dialog.findViewById(R.id.title_container);

        String msg = "Um Zirbl nutzen zu können, benötigst du eine stabile Internetverbindung.";
        TextView text = (TextView) dialog.findViewById(R.id.text_dialog_top);
        text.setText(msg);
        LinearLayout.LayoutParams lastTxtParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Resources r = context.getResources();
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, r.getDisplayMetrics());
        lastTxtParams.setMargins(margin, margin, margin, margin);
        text.setLayoutParams(lastTxtParams);

        TextView title = (TextView) dialog.findViewById(R.id.title);
        title.setText("KEINE INTERNETVERBINDUNG");
        title.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorRed));

        TextView bottomText = (TextView) dialog.findViewById(R.id.text_dialog_bottom);
        bottomText.setVisibility(View.GONE);

        Button dialogButtonTryAgain = (Button) dialog.findViewById(R.id.btn_marked);
        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        dialogButtonTryAgain.setText("ERNEUT VERSUCHEN");
        dialogButtonTryAgain.setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));

        dialogButtonTryAgain.setTypeface(typeface1);
        dialogButtonTryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                internetActivity.tryConnectionAgain();
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }
}