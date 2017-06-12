package hsaugsburg.zirbl001;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;


public class BaseBackPressedListener implements OnBackPressedListener {
    private final FragmentActivity activity;

    public BaseBackPressedListener(FragmentActivity activity) {
        this.activity = activity;

    }

    @Override
    public void doBack() {

        FragmentManager fm = activity.getSupportFragmentManager();
        fm.popBackStack();

        activity.getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        activity.getSupportFragmentManager().popBackStackImmediate();
        Log.d("Back", "Hello");
    }
}
