package hsaugsburg.zirbl001.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import hsaugsburg.zirbl001.R;


public class TitleInText extends android.support.v7.widget.AppCompatTextView {
    public TitleInText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Bold.ttf"));
        this.setTextColor(ContextCompat.getColor(context, R.color.colorTurquoise));
        this.setAllCaps(true);
    }
}
