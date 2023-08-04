package org.openobservatory.ooniprobe.model.database;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.ooniprobe.common.AppDatabase;
import org.openobservatory.ooniprobe.common.LocaleUtils;
import org.openobservatory.ooniprobe.common.MapUtility;
import org.openobservatory.ooniprobe.domain.MapConverter;
import org.openobservatory.ooniprobe.domain.NettestConverter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

@Table(database = AppDatabase.class)
public class TestDescriptor extends BaseModel implements Serializable {
    @PrimaryKey()
    private long runId;

    @Column
    private String name;

    @Column(name = "name_intl", typeConverter = MapConverter.class)
    private HashMap nameIntl;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "short_description_intl", typeConverter = MapConverter.class)
    private HashMap shortDescriptionIntl;

    @Column
    private String description;

    @Column(name = "description_intl", typeConverter = MapConverter.class)
    private HashMap descriptionIntl;

    @Column
    private String icon;

    @Column
    private String author;

    @Column
    private boolean archived;

    @Column(typeConverter = NettestConverter.class)
    private List nettests;

    public long getRunId() {
        return runId;
    }

    public void setRunId(long runId) {
        this.runId = runId;
    }

    public String getName() {
        return MapUtility.getOrDefaultCompat(nameIntl, LocaleUtils.sLocale.getLanguage(), name).toString();
    }

    public void setName(String name) {
        this.name = name;
    }

    public HashMap getNameIntl() {
        return nameIntl;
    }

    public void setNameIntl(HashMap nameIntl) {
        this.nameIntl = nameIntl;
    }

    public String getShortDescription() {
        return MapUtility.getOrDefaultCompat(shortDescriptionIntl, LocaleUtils.sLocale.getLanguage(), shortDescription).toString();
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public HashMap getShortDescriptionIntl() {
        return shortDescriptionIntl;
    }

    public void setShortDescriptionIntl(HashMap shortDescriptionIntl) {
        this.shortDescriptionIntl = shortDescriptionIntl;
    }

    public String getDescription() {
        return MapUtility.getOrDefaultCompat(descriptionIntl, LocaleUtils.sLocale.getLanguage(), description).toString();
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public HashMap getDescriptionIntl() {
        return descriptionIntl;
    }

    public void setDescriptionIntl(HashMap descriptionIntl) {
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
        private long runId;
        private String name;
        private HashMap nameIntl;
        private String shortDescription;
        private HashMap shortDescriptionIntl;
        private String description;
        private HashMap descriptionIntl;
        private String icon;
        private String author;
        private boolean archived;
        private List nettests;

        private Builder() {
        }

        public static Builder aTestDescriptor() {
            return new Builder();
        }

        public Builder withRunId(long runId) {
            this.runId = runId;
            return this;
        }

        public Builder withName(String name) {
            this.name = name;
            return this;
        }

        public Builder withNameIntl(HashMap nameIntl) {
            this.nameIntl = nameIntl;
            return this;
        }

        public Builder withShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
            return this;
        }

        public Builder withShortDescriptionIntl(HashMap shortDescriptionIntl) {
            this.shortDescriptionIntl = shortDescriptionIntl;
            return this;
        }

        public Builder withDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder withDescriptionIntl(HashMap descriptionIntl) {
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
            testDescriptor.setRunId(runId);
            testDescriptor.setName(name);
            testDescriptor.setNameIntl(nameIntl);
            testDescriptor.setShortDescription(shortDescription);
            testDescriptor.setShortDescriptionIntl(shortDescriptionIntl);
            testDescriptor.setDescription(description);
            testDescriptor.setDescriptionIntl(descriptionIntl);
            testDescriptor.setIcon(icon);
            testDescriptor.setAuthor(author);
            testDescriptor.setArchived(archived);
            testDescriptor.setNettests(nettests);
            return testDescriptor;
        }
    }
}
