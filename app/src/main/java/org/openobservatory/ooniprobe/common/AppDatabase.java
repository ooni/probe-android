package org.openobservatory.ooniprobe.common;

import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;
import com.raizlabs.android.dbflow.sql.migration.BaseMigration;
import com.raizlabs.android.dbflow.structure.database.DatabaseWrapper;

import org.openobservatory.ooniprobe.model.database.Result;

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION, foreignKeyConstraintsEnforced = true)
public class AppDatabase {
    public static final String NAME = "v2";
    public static final int VERSION = 2;

    @Migration(version = 2, database = AppDatabase.class)
    public static class Migration2 extends AlterTableMigration<Result> {

        public Migration2() {
            super(Result.class);
            Log.v("Creation", "Creation()");
        }

        public Migration2(Class<Result> table) {
            super(table);
            Log.v("Creation2", "Creation()");
        }

        @Override
        public void onPreMigrate() {
            Log.v("Creation", "onPreMigrate()");
            addColumn(SQLiteType.TEXT, "failure_msg");
        }

        /*
        @Override
        public void migrate(DatabaseWrapper database) {
           /* SQLite.update(Result.class)
                    .set(Result_table.status.eq("Invalid"))
                    .where(Employee_Table.job.eq("Laid Off"))
                    .execute(database); // required inside a migration to pass the wrapper

        }*/
    }

}
