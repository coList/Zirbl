package hsaugsburg.zirbl001.NavigationActivities.Profile;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.JSONOwnStatistics;
import hsaugsburg.zirbl001.Datamanagement.OwnStatisticsAdapter;
import hsaugsburg.zirbl001.Datamanagement.TourSelectionAdapter;
import hsaugsburg.zirbl001.Interfaces.JSONModel;
import hsaugsburg.zirbl001.Models.OwnStatisticsModel;
import hsaugsburg.zirbl001.Models.TourSelectionModel;
import hsaugsburg.zirbl001.NavigationActivities.TourDetailActivity;
import hsaugsburg.zirbl001.R;


public class ProfileOwnFragment extends Fragment {
    private static final String TAG = "ProfileOwnFragment";
    private ListView mListView;

    public static final String GLOBAL_VALUES = "globalValuesFile";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_own, container, false);



        SharedPreferences globalValues = getActivity().getSharedPreferences(GLOBAL_VALUES, 0);
        String serverName = globalValues.getString("serverName", null);
        String username = globalValues.getString("userName", null);
        new JSONOwnStatistics(this, username).execute(serverName + "/api/selectOwnStatisticsView.php");
        mListView = (ListView) view.findViewById(R.id.ownstatistics_list_view);
        return view;
    }

    public void processData(List<OwnStatisticsModel> result) {
        Log.d("ProfileOwnFragment", "processdata");

        if (result != null) {
            OwnStatisticsAdapter adapter = new OwnStatisticsAdapter(getActivity(), result);
            mListView.setAdapter(adapter);

        }

    }

}
