package hsaugsburg.zirbl001.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;

import hsaugsburg.zirbl001.R;

public class QuicksandBoldPrimaryButton extends android.support.v7.widget.AppCompatButton {
    public QuicksandBoldPrimaryButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Bold.ttf"));
        this.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        this.setAllCaps(false);
    }
}
