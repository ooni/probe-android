package org.openobservatory.ooniprobe.utils;

import com.raizlabs.android.dbflow.sql.language.Delete;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Network;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.model.database.Result_Table;
import org.openobservatory.ooniprobe.model.database.Url;

public class DatabaseUtils {

    public static void resetDatabase() {
        Delete.tables(Measurement.class, Url.class, Result.class, Network.class);
    }

    public static Result findResult(int id) {
        return SQLite.select().from(Result.class).where(Result_Table.id.eq(id)).querySingle();
    }
}
