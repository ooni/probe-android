package org.openobservatory.ooniprobe.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

public class DialogUtil {
    public static ProgressDialog makeProgressDialog(Context context, String message, boolean cancelable, DialogInterface.OnClickListener cancelListener) {
        ProgressDialog pd = new ProgressDialog(context, org.openobservatory.ooniprobe.R.style.MaterialAlertDialogCustom);
        pd.setTitle(localhost.toolkit.R.string.prgsTitle);
        if (message != null) {
            pd.setMessage(message);
        }
        pd.setCancelable(cancelable);

        if (cancelable) {
            pd.setButton(
                    DialogInterface.BUTTON_NEGATIVE,
                    context.getText(org.openobservatory.ooniprobe.R.string.Modal_Cancel),
                    cancelListener
            );
        }
        return pd;
    }
}
