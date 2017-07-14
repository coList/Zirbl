package hsaugsburg.zirbl001.NavigationActivities.Profile;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.ClassStatisticsAdapter;
import hsaugsburg.zirbl001.Datamanagement.JSONClassStatistics;
import hsaugsburg.zirbl001.Datamanagement.JSONOwnStatistics;
import hsaugsburg.zirbl001.Models.ClassStatisticsModel;
import hsaugsburg.zirbl001.Models.ClassesStatModel;
import hsaugsburg.zirbl001.Models.OwnStatisticsModel;
import hsaugsburg.zirbl001.R;


public class ProfileClassFragment extends Fragment {
    private static final String TAG = "ProfileClassFragment";

    private ListView mListView;

    public static final String GLOBAL_VALUES = "globalValuesFile";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_class, container, false);


        SharedPreferences globalValues = getActivity().getSharedPreferences(GLOBAL_VALUES, 0);
        String serverName = globalValues.getString("serverName", null);
        String username = globalValues.getString("userName", null);
        new JSONClassStatistics(this, username).execute(serverName + "/api/selectClassStatisticsView.php");
        mListView = (ListView) view.findViewById(R.id.classstatistics_list_view);
        return view;
    }

    public void processData(List<ClassesStatModel> result) {
        if (result != null) {
           ClassStatisticsAdapter adapter = new ClassStatisticsAdapter((Context)getActivity(), result);
           mListView.setAdapter(adapter);
        }
    }



}
