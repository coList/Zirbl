package hsaugsburg.zirbl001.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.lb.auto_fit_textview.AutoResizeTextView;

import hsaugsburg.zirbl001.R;

public class OpenSansBoldPoints extends AutoResizeTextView {
    public OpenSansBoldPoints(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-ExtraBold.ttf"));
        this.setTextColor(ContextCompat.getColor(context, R.color.colorBrown));
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 60);
        this.setAllCaps(true);
    }
}
