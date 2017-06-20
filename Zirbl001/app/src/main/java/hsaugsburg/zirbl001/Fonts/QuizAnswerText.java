package hsaugsburg.zirbl001.Fonts;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.Button;

import hsaugsburg.zirbl001.R;

/**
 * Created by mel on 20.06.17.
 */

public class QuizAnswerText extends Button {
    public QuizAnswerText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Quicksand-Bold.ttf"));
        this.setTextColor(ContextCompat.getColor(context, R.color.colorFlowingText));
    }
}
