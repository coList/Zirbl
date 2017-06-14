package hsaugsburg.zirbl001.NavigationActivities.Profile;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class ProfileSectionPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "ProfileSectionPagerAdapter";

    private final List<Fragment> mFragmentList = new ArrayList<>();

    public ProfileSectionPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }


    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    public void addFragment(Fragment fragment){
        mFragmentList.add(fragment);
    }
}
