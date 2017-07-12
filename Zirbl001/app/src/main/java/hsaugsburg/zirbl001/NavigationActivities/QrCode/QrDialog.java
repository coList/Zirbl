package hsaugsburg.zirbl001.NavigationActivities.QrCode;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;
import com.nostra13.universalimageloader.utils.L;

import hsaugsburg.zirbl001.Datamanagement.DownloadTasks.DownloadJSON;
import hsaugsburg.zirbl001.Interfaces.DownloadActivity;
import hsaugsburg.zirbl001.R;
import hsaugsburg.zirbl001.TourActivities.TourstartActivity;

public class QrDialog implements DownloadActivity {

    private static final String TAG = "QrDialog";

    private final Context context;
    private final boolean success;
    private final String textButtonMarked;

    private int tourID;
    private int classID;

    public static final String GLOBAL_VALUES = "globalValuesFile";
    private String serverName;

    private int downloadTasksCounter = 0;
    private int amountOfDownloadTasks = 9;

    private Activity activity;

    private boolean downloadFinished = false;
    private Button dialogButtonAgain;


    public QrDialog(Context context, boolean success, String textButtonMarked, int classID, int tourID) {
        this.context = context;
        this.success = success;
        this.textButtonMarked = textButtonMarked;
        this.classID = classID;
        this.tourID = tourID;
    }

    public void showDialog(Activity activity, String msg, String msg1, String msg2) {
        this.activity = activity;
        SharedPreferences globalValues = activity.getSharedPreferences(GLOBAL_VALUES, 0);
        serverName = globalValues.getString("serverName", null);

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);

        if (success) {
            dialog.setContentView(R.layout.dialog_one_button);
        } else {
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

        if (msg1 != null && !msg1.isEmpty()) {
            TextView text1 = (TextView) dialog.findViewById(R.id.text_dialog_middle);
            text1.setVisibility(View.VISIBLE);
            text1.setText(fromHtml(msg1));
        }
        TextView text2 = (TextView) dialog.findViewById(R.id.text_dialog_bottom);
        text2.setText(fromHtml(msg2));

        if (success) {
            TextView title = (TextView) dialog.findViewById(R.id.title);
            title.setText("SCAN ERFOLGREICH");
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorTurquoise));
        } else {
            TextView title = (TextView) dialog.findViewById(R.id.title);
            title.setText("SCAN FEHLGESCHLAGEN");
            linearLayout.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
        }


        dialogButtonAgain = (Button) dialog.findViewById(R.id.btn_marked);
        Typeface typeface1 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
        dialogButtonAgain.setText(textButtonMarked);
        if (success) {
            dialogButtonAgain.setTextColor(context.getResources().getColor(R.color.colorTurquoise));
        } else {
            dialogButtonAgain.setTextColor(context.getResources().getColor(R.color.colorRed));
        }
        dialogButtonAgain.setTypeface(typeface1);
        dialogButtonAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (success) {
                    downloadTour();
                    TextView title = (TextView) dialog.findViewById(R.id.title);
                    title.setText("DOWNLOAD");

                    LinearLayout downloadAnimationContainer = (LinearLayout) dialog.findViewById(R.id.download_animation_container);
                    downloadAnimationContainer.setVisibility(View.VISIBLE);

                    LinearLayout textContainerBottom = (LinearLayout) dialog.findViewById(R.id.text_container_bottom);
                    LinearLayout textContainerMiddle = (LinearLayout) dialog.findViewById(R.id.text_container_middle);
                    LinearLayout textContainerTop = (LinearLayout) dialog.findViewById(R.id.text_container_top);
                    LinearLayout buttonArea = (LinearLayout) dialog.findViewById(R.id.answerArea);

                    textContainerBottom.setVisibility(View.GONE);
                    textContainerMiddle.setVisibility(View.GONE);
                    textContainerTop.setVisibility(View.GONE);
                    buttonArea.setVisibility(View.GONE);

                    doDownloadAnimation(dialog);


                } else {
                    Intent intent = new Intent(context, ScanBarcodeActivity.class);
                    context.startActivity(intent);
                }

            }
        });

        if (!success) {
            Button dialogButtonEnd = (Button) dialog.findViewById(R.id.btn_end);
            Typeface typeface2 = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf");
            dialogButtonEnd.setText("BEENDEN");
            dialogButtonEnd.setTypeface(typeface2);
            dialogButtonEnd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, QrActivity.class);
                    context.startActivity(intent);
                }
            });
        }
        dialog.show();
        dialog.getWindow().setAttributes(lp);


    }

    private void doDownloadAnimation(Dialog dialog1) {
        final Dialog dialog = dialog1;
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            int i = 0;

            public void run() {

                String nameDot = "dot" + (i + 1);
                int dotID = activity.getResources().getIdentifier(nameDot, "id", activity.getPackageName());
                ImageView dot = (ImageView) dialog.findViewById(dotID);
                dot.setImageResource(R.drawable.ic_loading_point_black);

                int previous = i - 1;
                if (previous < 0) {
                    previous = 2;
                }
                Log.d(TAG, "dot" + (i + 1));

                String namePreviousDot = "dot" + (previous + 1);
                int previousDotID = activity.getResources().getIdentifier(namePreviousDot, "id", activity.getPackageName());
                ImageView previousDot = (ImageView) dialog.findViewById(previousDotID);
                previousDot.setImageResource(R.drawable.ic_loading_point_color);

                i++;
                if (i > 2) {
                    i = 0;
                }
                if (!downloadFinished) {
                    handler.postDelayed(this, 500);
                } else {
                    Intent intent = new Intent(context, TourstartActivity.class);
                    intent.putExtra("tourID", Integer.toString(tourID));
                    intent.putExtra("classID", Integer.toString(classID));
                    context.startActivity(intent);
                }
            }
        };
        handler.postDelayed(runnable, 500);

    }


    private void downloadTour() {
        new DownloadJSON(activity, this, serverName, tourID, "tourinfopopups", "infopopups").execute(serverName + "/api/selectInfoPopupView.php");
        new DownloadJSON(activity, this, serverName, tourID, "tourletters", "letters").execute(serverName + "/api/selectHangmanView.php");
        new DownloadJSON(activity, this, serverName, tourID, "toursinglechoice", "singlechoice").execute(serverName + "/api/selectSingleChoiceView.php");
        new DownloadJSON(activity, this, serverName, tourID, "tourguessthenumber", "guessthenumber").execute(serverName + "/api/selectGuessTheNumberView.php");
        new DownloadJSON(activity, this, serverName, tourID, "stationlocations", "stations").execute(serverName + "/api/selectStationLocationsView.php");
        new DownloadJSON(activity, this, serverName, tourID, "tourtruefalse", "truefalse").execute(serverName + "/api/selectTrueFalseView.php");
        new DownloadJSON(activity, this, serverName, tourID, "tourchronology", "chronology").execute(serverName + "/api/selectChronologyView.php");
        new DownloadJSON(activity, this, serverName, tourID, "nutlocations", "nuts").execute(serverName + "/api/selectNutLocationsView.php");
        new DownloadJSON(activity, this, serverName, tourID, "tourlocation_infopopups", "location_infopopups").execute(serverName + "/api/selectLocationInfoPopupView.php");

    }

    public void downloadFinished() {
        downloadTasksCounter++;

        if (downloadTasksCounter >= amountOfDownloadTasks) {
            downloadFinished = true;
            dialogButtonAgain.setText(textButtonMarked);
        }

    }

    public static Spanned fromHtml(String html) {
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

}