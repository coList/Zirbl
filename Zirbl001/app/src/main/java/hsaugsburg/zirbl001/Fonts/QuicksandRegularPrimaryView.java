package hsaugsburg.zirbl001.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;

import hsaugsburg.zirbl001.R;

public class QuicksandRegularPrimaryView extends android.support.v7.widget.AppCompatTextView {
    public QuicksandRegularPrimaryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Regular.ttf"));
        this.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
    }
}
