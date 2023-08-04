package org.openobservatory.ooniprobe.common;

import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.Migration;
import com.raizlabs.android.dbflow.sql.SQLiteType;
import com.raizlabs.android.dbflow.sql.migration.AlterTableMigration;

import org.openobservatory.ooniprobe.model.database.Measurement;
import org.openobservatory.ooniprobe.model.database.Result;

@Database(name = AppDatabase.NAME, version = AppDatabase.VERSION, foreignKeyConstraintsEnforced = true)
public class AppDatabase {
    public static final String NAME = "v2";
    public static final int VERSION = 4;

    @Migration(version = 2, database = AppDatabase.class)
    public static class Migration2 extends AlterTableMigration<Result> {

        public Migration2(Class<Result> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "failure_msg");
        }

    }

    @Migration(version = 3, database = AppDatabase.class)
    public static class Migration3 extends AlterTableMigration<Measurement> {

        public Migration3(Class<Measurement> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addColumn(SQLiteType.TEXT, "rerun_network");
        }

    }

    @Migration(version = 4, database = AppDatabase.class)
    public static class Migration4 extends AlterTableMigration<Result> {

        public Migration4(Class<Result> table) {
            super(table);
        }

        @Override
        public void onPreMigrate() {
            addForeignKeyColumn(SQLiteType.INTEGER, "descriptor_runId", "TestDescriptor (`runId`)");
        }

    }

}
