package hsaugsburg.zirbl001.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;
import hsaugsburg.zirbl001.R;



public class InfoBesideIconsText extends android.support.v7.widget.AppCompatTextView {
    public InfoBesideIconsText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Bold.ttf"));
        this.setTextColor(ContextCompat.getColor(context, R.color.colorTurquoise));
    }
}
