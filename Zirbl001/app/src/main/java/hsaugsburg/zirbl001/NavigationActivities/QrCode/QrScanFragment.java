package hsaugsburg.zirbl001.NavigationActivities.QrCode;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hsaugsburg.zirbl001.R;


public class QrScanFragment extends Fragment {
    private static final String TAG = "ProfileOwnFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_scan, container, false);

        TextView noScans =  (TextView) view.findViewById(R.id.scanText);
        noScans.setText(fromHtml("<b>Scanne</b> den QR-Code deiner Lehrerin oder deines Lehrers ab."));

        return view;
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
