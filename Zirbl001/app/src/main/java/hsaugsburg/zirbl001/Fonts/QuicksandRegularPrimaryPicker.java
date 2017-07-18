package hsaugsburg.zirbl001.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class QuicksandRegularPrimaryPicker extends MaterialNumberPicker {

    Typeface type;

    public QuicksandRegularPrimaryPicker(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    @Override
    public void addView(View child) {
        super.addView(child);
        updateView(child);
    }

    @Override
    public void addView(View child, int index,
                        android.view.ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        type = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Quicksand-Regular.ttf");
        updateView(child);
    }

    @Override
    public void addView(View child, android.view.ViewGroup.LayoutParams params) {
        super.addView(child, params);

        type = Typeface.createFromAsset(getContext().getAssets(),
                "fonts/Quicksand-Regular.ttf");
        updateView(child);
    }

    private void updateView(View view) {

        if (view instanceof EditText) {
            ((EditText) view).setTypeface(type);
        }

    }
}
