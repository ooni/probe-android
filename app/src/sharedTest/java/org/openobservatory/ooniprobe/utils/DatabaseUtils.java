package org.openobservatory.ooniprobe.utils;

import com.raizlabs.android.dbflow.sql.language.Delete;

import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Url;

public class DatabaseUtils {

    public static void resetDatabase() {
        Delete.tables(Measurement.class, Url.class, Result.class, Network.class);
    }

}
