package org.openobservatory.netprobe.view;

/**
 * Created by lorenzo on 27/06/16.
 */

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;


/**
 * Un bottone definito per essere utilizzato in una {@link ListView}.
 */
public class ListImageButton extends ImageButton {

    /**
     * Costruttore di default, inizializza il bottone in base ad un contesto e ad un insieme di attributi.
     * @param context Contesto.
     * @param attrs Attributi della vista.
     */
    public ListImageButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        //Rendo forzatamente il bottone non selezionabile.
        this.setFocusable(false);
    }

    /**
     * Imposta lo stato di pressione del bottone.
     * @param pressed Stato di pressione del bottone.
     */
    @Override
    public void setPressed(boolean pressed) {
        //Se il contenitore del bottone è premuto, allora non imposto anche il bottone a premuto
        if (pressed && ((View) getParent()).isPressed()) {
            return;
        }
        //Se il contenitore non è premuto, allora è stato toccato il bottone. Procedo normalmente.
        super.setPressed(pressed);
    }
}

