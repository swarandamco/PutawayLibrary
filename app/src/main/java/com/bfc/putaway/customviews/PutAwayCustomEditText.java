package com.bfc.putaway.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatEditText;
import com.bfc.putaway.R;
import com.bfc.putaway.util.FontUtils;

public class PutAwayCustomEditText extends AppCompatEditText {
    private Context context;
    public PutAwayCustomEditText(Context context) {
        super(context);
        FontUtils.setFontFace(context, this, FontUtils.FontType.POPPINSREGULAR.toString());
        this.context = context;
    }

    public PutAwayCustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.custom_typeface, 0, 0);
            int fontValue = a.getInt(R.styleable.custom_typeface_custom_font_type, 0);
            a.recycle();
            if (fontValue != 0)
                FontUtils.setFontFace(context, this, FontUtils.FontType.values()[fontValue - 1].toString());
            else
                FontUtils.setFontFace(context, this, FontUtils.FontType.POPPINSREGULAR.toString());
        }
    }

    public void setFontFace() {
        FontUtils.setFontFace(context, this, FontUtils.FontType.POPPINSREGULAR.toString());
    }
}
