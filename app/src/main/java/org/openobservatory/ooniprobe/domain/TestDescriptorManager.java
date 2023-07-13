package org.openobservatory.ooniprobe.domain;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.openobservatory.ooniprobe.model.database.TestDescriptor;

import java.util.List;

public class TestDescriptorManager {
    public static boolean save(TestDescriptor descriptor) {
        return descriptor.save();
    }

    public static List<TestDescriptor> getAll() {
        return SQLite.select().from(TestDescriptor.class).queryList();
    }
}
