package org.openobservatory.ooniprobe.model.database;

import android.content.Context;
import android.graphics.Color;

import com.google.common.collect.Lists;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Where;
import com.raizlabs.android.dbflow.structure.BaseModel;

import org.openobservatory.engine.OONIRunNettest;
import org.openobservatory.ooniprobe.common.AppDatabase;
import org.openobservatory.ooniprobe.common.LocaleUtils;
import org.openobservatory.ooniprobe.common.MapUtility;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.domain.MapConverter;
import org.openobservatory.ooniprobe.domain.NettestConverter;
import org.openobservatory.ooniprobe.test.suite.OONIRunSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;
import org.openobservatory.ooniprobe.test.test.WebConnectivity;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

enum Animations {
    checkMark("anim/checkMark.json"),
    circumvention("anim/circumvention.json"),
    crossMark("anim/crossMark.json"),
    experimental("anim/experimental.json"),
    instant_messaging("anim/instant_messaging.json"),
    middle_boxes("anim/middle_boxes.json"),
    performance("anim/performance.json"),
    websites("anim/websites.json");

    private final String asset;

    Animations(String asset) {
        this.asset = asset;
    }

    @Override
    public String toString() {
        return this.asset;
    }
}

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
    private String color;

    @Column
    private String animation;

    @Column
    private String author;

    @Column
    private boolean archived;

    @Column(name = "auto_run")
    private boolean autoRun;

    @Column(name = "auto_update")
    private boolean autoUpdate;

    @Column(name = "creation_time")
    private Date creationTime;

    @Column(name = "translation_creation_time")
    private Date translationCreationTime;
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

    public String getColor() {
        return color != null ? color : "#495057";
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getAnimation() {
        return animation;
    }

    public void setAnimation(String animation) {
        this.animation = animation;
    }

    public int getParsedColor() {
        return Color.parseColor(getColor());
    }

    @Nullable
    public String getAnimationAsset() {
        try {
            return Animations.valueOf(animation).toString();
        }catch (Exception e){
            e.printStackTrace();
            ThirdPartyServices.logException(e);
            return null;
        }
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

    public boolean isAutoRun() {
        return autoRun;
    }

    public void setAutoRun(boolean autoRun) {
        this.autoRun = autoRun;
    }

    public boolean isAutoUpdate() {
        return autoUpdate;
    }

    public void setAutoUpdate(boolean autoUpdate) {
        this.autoUpdate = autoUpdate;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime;
    }

    public Date getTranslationCreationTime() {
        return translationCreationTime;
    }

    public void setTranslationCreationTime(Date translationCreationTime) {
        this.translationCreationTime = translationCreationTime;
    }

    public List getNettests() {
        return nettests;
    }

    public void setNettests(List nettests) {
        this.nettests = nettests;
    }

    public OONIRunSuite getTestSuite(Context context) {
        List<AbstractTest> tests = Lists.transform(
                (List<OONIRunNettest>)getNettests(),
                nettest -> {
                    AbstractTest test = AbstractTest.getTestByName(nettest.getName());
                    if (nettest.getName().equals(WebConnectivity.NAME)){
                        for (String url : nettest.getInputs())
                            Url.checkExistingUrl(url);
                    }
                    test.setInputs(nettest.getInputs());
                    return test;
                }
        );
        return new OONIRunSuite(
                context,
                this,
                tests.toArray(new AbstractTest[0])
        );
    }

    public static Where<TestDescriptor> selectAllAvailable() {
        return SQLite.select().from(TestDescriptor.class)
                .where(TestDescriptor_Table.archived.eq(false));
    }

    public boolean shouldUpdate(TestDescriptor updatedDescriptor) {
        return updatedDescriptor.creationTime.after(creationTime)
                || updatedDescriptor.translationCreationTime.after(translationCreationTime);
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
        private String color;
        private String animation;
        private String author;
        private boolean archived;
        private boolean autoRun;
        private boolean autoUpdate;
        private Date creationTime;
        private Date translationCreationTime;
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

        public Builder withColor(String color) {
            this.color = color;
            return this;
        }

        public Builder withAnimation(String animation) {
            this.animation = animation;
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

        public Builder withAutoRun(boolean autoRun) {
            this.autoRun = autoRun;
            return this;
        }

        public Builder withAutoUpdate(boolean autoUpdate) {
            this.autoUpdate = autoUpdate;
            return this;
        }

        public Builder withCreationTime(Date creationTime) {
            this.creationTime = creationTime;
            return this;
        }

        public Builder withTranslationCreationTime(Date translationCreationTime) {
            this.translationCreationTime = translationCreationTime;
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
            testDescriptor.setAutoRun(autoRun);
            testDescriptor.setAutoUpdate(autoUpdate);
            testDescriptor.setCreationTime(creationTime);
            testDescriptor.setTranslationCreationTime(translationCreationTime);
            testDescriptor.setNettests(nettests);
            return testDescriptor;
        }
    }
}
