package hsaugsburg.zirbl001.NavigationActivities.Profile;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Models.OwnStatisticsModel;
import hsaugsburg.zirbl001.R;


public class ProfileClassFragment extends Fragment {
    private static final String TAG = "ProfileClassFragment";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_class, container, false);

        return view;
    }

}
