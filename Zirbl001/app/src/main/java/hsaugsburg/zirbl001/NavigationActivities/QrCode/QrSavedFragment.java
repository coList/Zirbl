package hsaugsburg.zirbl001.NavigationActivities.QrCode;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hsaugsburg.zirbl001.R;


public class QrSavedFragment extends Fragment {
    private static final String TAG = "QrSavedFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_saved, container, false);

        return view;
    }

}
