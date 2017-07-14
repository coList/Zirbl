package hsaugsburg.zirbl001.NavigationActivities.QrCode;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.List;

import hsaugsburg.zirbl001.Datamanagement.ClassesAdapter;
import hsaugsburg.zirbl001.Datamanagement.JSONClassStatistics;
import hsaugsburg.zirbl001.Datamanagement.JSONClasses;
import hsaugsburg.zirbl001.Models.ClassesModel;
import hsaugsburg.zirbl001.R;


public class QrSavedFragment extends Fragment {
    private static final String TAG = "QrSavedFragment";


    private ListView mListView;

    public static final String GLOBAL_VALUES = "globalValuesFile";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_qr_saved, container, false);


        SharedPreferences globalValues = getActivity().getSharedPreferences(GLOBAL_VALUES, 0);
        String serverName = globalValues.getString("serverName", null);
        String username = globalValues.getString("userName", null);
        new JSONClasses(this, username).execute(serverName + "/api/selectClassesView.php");
        mListView = (ListView) view.findViewById(R.id.saved_qr_list_view);

        return view;
    }

    public void processData(List<ClassesModel> result) {
        if (result != null) {
            ClassesAdapter adapter = new ClassesAdapter((Context)getActivity(), result);
            mListView.setAdapter(adapter);
        }
    }

}
