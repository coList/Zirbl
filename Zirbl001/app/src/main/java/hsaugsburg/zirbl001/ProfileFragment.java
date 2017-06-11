package hsaugsburg.zirbl001;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class ProfileFragment extends Fragment {

    private String title;
    private int page;

    public static ProfileFragment newInstance(int page, String title) {
        ProfileFragment profileFavorite = new ProfileFragment();
        Bundle args = new Bundle();
        args.putInt("someInt", page);
        args.putString("someTitle", title);
        profileFavorite.setArguments(args);
        Log.d("Test", "ProfileFragmentnewInstance");
        return profileFavorite;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        page = getArguments().getInt("someInt", 0);
        title = getArguments().getString("someTitle");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("Test", "Profil");
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}