package hsaugsburg.zirbl001.NavigationActivities.QrCode;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import hsaugsburg.zirbl001.Datamanagement.DownloadTasks.DownloadJSON;
import hsaugsburg.zirbl001.Interfaces.DownloadActivity;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.TourActivities.TourstartActivity;

public class QrDialog implements DownloadActivity{

    private static final String TAG = "QrDialog";

    private final Context context;
    private final boolean success;
    private final String textButtonMarked;

    private int tourID;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    String serverName;

    private int downloadTasksCounter = 0;
    private int amountOfDownloadTasks = 7;

    Activity activity;

    boolean downloadFinished = false;
    private String textButtonMarkedDownload = "DOWNLOAD";
    Button dialogButtonAgain;


    public QrDialog(Context context, boolean success, String textButtonMarked){
        this.context = context;
        this.success = success;
        this.textButtonMarked = textButtonMarked;
    }

    public void showDialog(Activity activity, String msg, String msg2, final int tourID){
        this.activity  =activity;
        SharedPreferences globalValues = activity.getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

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
        TextView titleText = (TextView) dialog.findViewById(R.id.title);
        titleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);


        TextView text = (TextView) dialog.findViewById(R.id.text_dialog_top);
        text.setText(fromHtml(msg));
        TextView text2 = (TextView) dialog.findViewById(R.id.text_dialog_bottom);
        text2.setText(fromHtml(msg2));

        if(success){
            TextView title = (TextView) dialog.findViewById(R.id.title);
            title.setText("SCAN ERFOLGREICH");
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorTurquoise));
        }else{
            TextView title = (TextView) dialog.findViewById(R.id.title);
            title.setText("SCAN FEHLGESCHLAGEN");
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
        }


        dialogButtonAgain = (Button) dialog.findViewById(R.id.btn_marked);
        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        //dialogButtonAgain.setText(textButtonMarked);
        dialogButtonAgain.setText(textButtonMarkedDownload);
        if(success){
            dialogButtonAgain.setTextColor(context.getResources().getColor(R.color.colorTurquoise));
        }else {
            dialogButtonAgain.setTextColor(context.getResources().getColor(R.color.colorRed));
        }
        dialogButtonAgain.setTypeface(typeface1);
        dialogButtonAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(success){
                    if (!downloadFinished) {
                        downloadTour();
                    } else {
                        Intent intent = new Intent (context, TourstartActivity.class);
                        intent.putExtra("tourID", Integer.toString(tourID));
                        context.startActivity(intent);
                    }

                }else{
                    Intent intent = new Intent (context, ScanBarcodeActivity.class);
                    context.startActivity(intent);
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


    public void downloadTour() {
        new DownloadJSON(activity, this, serverName, tourID, "tourinfopopups", "infopopups").execute(serverName + "/api/selectInfoPopupView.php");
        new DownloadJSON(activity, this, serverName, tourID, "tourletters", "letters").execute(serverName + "/api/selectHangmanView.php");
        new DownloadJSON(activity, this, serverName, tourID, "toursinglechoice", "singlechoice").execute(serverName + "/api/selectSingleChoiceView.php");
        new DownloadJSON(activity, this, serverName, tourID, "tourguessthenumber", "guessthenumber").execute(serverName + "/api/selectGuessTheNumberView.php");
        new DownloadJSON(activity, this, serverName, tourID, "stationlocations", "stations").execute(serverName + "/api/selectStationLocationsView.php");
        new DownloadJSON(activity, this, serverName, tourID, "tourtruefalse", "truefalse").execute(serverName + "/api/selectTrueFalseView.php");
        new DownloadJSON(activity, this, serverName, tourID, "tourchronology", "chronology").execute(serverName + "/api/selectChronologyView.php");

    }

    public void downloadFinished() {
        downloadTasksCounter++;

        if (downloadTasksCounter >= amountOfDownloadTasks) {
            downloadFinished = true;
            dialogButtonAgain.setText(textButtonMarked);
        }

    }

    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

}