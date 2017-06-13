package hsaugsburg.zirbl001.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import hsaugsburg.zirbl001.R;

public class TourTitle extends android.support.v7.widget.AppCompatTextView {
    public TourTitle(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf"));
        this.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        this.setAllCaps(true);
    }
}
