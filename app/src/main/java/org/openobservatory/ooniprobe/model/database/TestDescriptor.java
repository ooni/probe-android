package org.openobservatory.ooniprobe.model.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.engine.OONIRunNettest;
import org.openobservatory.ooniprobe.common.AppDatabase;
import org.openobservatory.ooniprobe.domain.NettestConverter;

import java.io.Serializable;
import java.util.List;

@Table(database = AppDatabase.class)
public class TestDescriptor extends BaseModel implements Serializable {
    @PrimaryKey(autoincrement = true)
    private int id;

    @Column
    @Unique(onUniqueConflict = ConflictAction.REPLACE)
    private long runId;

    @Column
    private String name;

    @Column(name = "name_intl")
    private String nameIntl;

    @Column
    private String description;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "description_intl")
    private String descriptionIntl;

    @Column
    private String icon;

    @Column
    private String author;

    @Column
    private boolean archived;

    @Column(typeConverter = NettestConverter.class)
    private List nettests;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getRunId() {
        return runId;
    }

    public void setRunId(long runId) {
        this.runId = runId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNameIntl() {
        return nameIntl;
    }

    public void setNameIntl(String nameIntl) {
        this.nameIntl = nameIntl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescriptionIntl() {
        return descriptionIntl;
    }

    public void setDescriptionIntl(String descriptionIntl) {
        this.descriptionIntl = descriptionIntl;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public List getNettests() {
        return nettests;
    }

    public void setNettests(List nettests) {
        this.nettests = nettests;
    }


    public static final class Builder {
        private int id;
        private long runId;
        private String name;
        private String nameIntl;
        private String description;
        private String shortDescription;
        private String descriptionIntl;
        private String icon;
        private String author;
        private boolean archived;
        private List nettests;

        private Builder() {
        }

        public static Builder aTestDescriptor() {
            return new Builder();
        }

        public Builder withId(int id) {
            this.id = id;
            return this;
        }

        public Builder withRunId(long runId) {
            this.runId = runId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withNameIntl(String nameIntl) {
            this.nameIntl = nameIntl;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }

        public Builder withDescriptionIntl(String descriptionIntl) {
            this.descriptionIntl = descriptionIntl;
            return this;
        }

        public Builder withIcon(String icon) {
            this.icon = icon;
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

        public Builder withNettests(List nettests) {
            this.nettests = nettests;
            return this;
        }

        public TestDescriptor build() {
            TestDescriptor testDescriptor = new TestDescriptor();
            testDescriptor.setId(id);
            testDescriptor.setRunId(runId);
            testDescriptor.setName(name);
            testDescriptor.setNameIntl(nameIntl);
            testDescriptor.setDescription(description);
            testDescriptor.setShortDescription(shortDescription);
            testDescriptor.setDescriptionIntl(descriptionIntl);
            testDescriptor.setIcon(icon);
            testDescriptor.setAuthor(author);
            testDescriptor.setArchived(archived);
            testDescriptor.setNettests(nettests);
            return testDescriptor;
        }
    }
}
