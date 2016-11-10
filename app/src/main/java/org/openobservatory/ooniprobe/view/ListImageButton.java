package org.openobservatory.ooniprobe.view;

/**
 * Created by lorenzo on 27/06/16.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;


/**
 * Button definied to be used in a {@link ListView}.
 */
public class ListImageButton extends ImageButton {

    /**
     * Default contructor.
     * @param context Context.
     * @param attrs Attributes.
     */
    public ListImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        //I force the button not selectable.
        this.setFocusable(false);
    }

    /**
     * Sets the button pressed state.
     * @param pressed Button state.
     */
    @Override
    public void setPressed(boolean pressed) {
        if (pressed && ((View) getParent()).isPressed()) {
            return;
        }
        super.setPressed(pressed);
    }
}

