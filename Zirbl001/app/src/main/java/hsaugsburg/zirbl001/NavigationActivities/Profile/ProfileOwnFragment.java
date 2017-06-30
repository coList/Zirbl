package hsaugsburg.zirbl001.NavigationActivities.Profile;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hsaugsburg.zirbl001.R;


public class ProfileOwnFragment extends Fragment {
    private static final String TAG = "ProfileOwnFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_own, container, false);

        return view;
    }

}
