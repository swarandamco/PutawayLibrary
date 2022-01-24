package com.bfc.putaway.util;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.widget.TextView;

import java.util.EnumMap;
import java.util.Map;

/**
 * Created by Swaran on 06-11-2019.
 * This is class is invoked whenever there is need to change font of
 * any textview, button or any view.
 */
public class FontUtils {

    public enum FontType {

        POPPINSREGULAR("fonts/Poppins-Regular.ttf"),
        POPPINSMEDIUM("fonts/Poppins-Medium.ttf"),
        POPPINSSEMIBOLD("fonts/Poppins-SemiBold.ttf"),
        POPPINSBOLD("fonts/Poppins-Bold.ttf");
        private final String path;

        FontType(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }

    /**
     * cache for loaded typefaces
     */
    private static final Map<FontType, Typeface> typefaceCache = new EnumMap<>(
            FontType.class);

    /**
     * Creates typeface and puts it into cache
     */
    public static Typeface getTypeface(Context context, FontType fontType) {
        String fontPath = fontType.getPath();

        if (!typefaceCache.containsKey(fontType)) {
            typefaceCache.put(fontType,
                    Typeface.createFromAsset(context.getAssets(), fontPath));
        }

        return typefaceCache.get(fontType);
    }

    /**
     * method to set the defined font
     *
     * @param context, context required to get type face
     * @param view,    TextView reference
     */
    public static void setFontFace(Context context, View view, String typeface) {
        ((TextView) view).setTypeface(getTypeface(context, FontType.valueOf(typeface)));
    }
}
