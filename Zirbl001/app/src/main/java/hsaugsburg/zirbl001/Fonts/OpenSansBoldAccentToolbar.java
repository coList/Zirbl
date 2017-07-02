package hsaugsburg.zirbl001.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;

import hsaugsburg.zirbl001.R;

public class OpenSansBoldAccentToolbar extends android.support.v7.widget.AppCompatTextView {
    public OpenSansBoldAccentToolbar(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf"));
        this.setTextColor(ContextCompat.getColor(context, R.color.colorAccent));
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        this.setAllCaps(true);
    }
}
