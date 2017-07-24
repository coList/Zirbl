package hsaugsburg.zirbl001.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;

import hsaugsburg.zirbl001.R;

public class OpenSansBoldPrimaryButton extends android.support.v7.widget.AppCompatButton {

    private Context context;
    private AttributeSet attrs;
    private int defStyle;

    public OpenSansBoldPrimaryButton(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public OpenSansBoldPrimaryButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        this.attrs=attrs;
        init();
    }

    public OpenSansBoldPrimaryButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context=context;
        this.attrs=attrs;
        this.defStyle=defStyle;
        init();
    }

    private void init() {
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Bold.ttf"));
        this.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        this.setAllCaps(true);
    }
}
