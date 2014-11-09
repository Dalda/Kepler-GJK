package gjk.kepler;

import android.content.Context;
import android.preference.DialogPreference;
import android.util.AttributeSet;

public class AboutPreference extends DialogPreference{
    public AboutPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        setNegativeButtonText(null);
        setPositiveButtonText(null);
        setDialogTitle(null);
        setDialogLayoutResource(R.layout.about_preference);
    }

}
