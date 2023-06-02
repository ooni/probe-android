package org.openobservatory.ooniprobe.model.database;

import static org.openobservatory.ooniprobe.domain.TestDescriptorManager.populateDescriptorData;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;
import org.openobservatory.ooniprobe.common.AppDatabase;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Table(database = AppDatabase.class)
public class TestDescriptor extends BaseModel implements Serializable {
    @PrimaryKey(autoincrement = true)
    private int id;
    @Column
    private String name;
    @Column(name = "short_description")
    private String shortDescription;
    @Column
    private String description;
    @Column
    private String icon;
    @Column
    private String dataUsage;
    @Column
    private String author;
    @Column
    private boolean archived;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDataUsage() {
        return dataUsage;
    }

    public void setDataUsage(String dataUsage) {
        this.dataUsage = dataUsage;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public static List<TestDescriptor> getLastResult() {
        if (SQLite.select().from(TestDescriptor.class).count()<=0){
            populateDescriptorData(FlowManager.getWritableDatabaseForTable(TestDescriptor.class));
        }
        return SQLite.select().from(TestDescriptor.class).queryList();
    }

    public static void saveAll(Collection<TestDescriptor> descriptors) {
        final DatabaseDefinition database = FlowManager.getDatabase(AppDatabase.class);
        database.executeTransaction(databaseWrapper ->
                Objects.requireNonNull(database.getModelAdapterForTable(TestDescriptor.class)).saveAll(descriptors));
    }

    public String getRuntime() {
        return "TODO";
    }

    public boolean isEnabled() {
        return true;
    }


    public static final class Builder {
        private int id;
        private String name;
        private String shortDescription;
        private String description;
        private String icon;
        private String dataUsage;
        private String author;
        private boolean archived;

        private Builder() {
        }

        public static Builder aTestDescriptor() {
            return new Builder();
        }

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withIcon(String icon) {
            this.icon = icon;
            return this;
        }

        public Builder withDataUsage(String dataUsage) {
            this.dataUsage = dataUsage;
            return this;
        }

        public Builder withAuthor(String author) {
            this.author = author;
            return this;
        }

        public Builder withArchived(boolean archived) {
            this.archived = archived;
            return this;
        }

        public TestDescriptor build() {
            TestDescriptor testDescriptor = new TestDescriptor();
            testDescriptor.setId(id);
            testDescriptor.setName(name);
            testDescriptor.setShortDescription(shortDescription);
            testDescriptor.setDescription(description);
            testDescriptor.setIcon(icon);
            testDescriptor.setDataUsage(dataUsage);
            testDescriptor.setAuthor(author);
            testDescriptor.setArchived(archived);
            return testDescriptor;
        }
    }
}
