package org.openobservatory.ooniprobe.activity.overview;

import android.text.format.DateUtils;
import android.view.View;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.jetbrains.annotations.NotNull;
import org.openobservatory.ooniprobe.R;
import org.openobservatory.ooniprobe.common.PreferenceManager;
import org.openobservatory.ooniprobe.common.ThirdPartyServices;
import org.openobservatory.ooniprobe.model.database.Result;
import org.openobservatory.ooniprobe.test.suite.AbstractSuite;
import org.openobservatory.ooniprobe.test.suite.OONIRunSuite;
import org.openobservatory.ooniprobe.test.suite.WebsitesSuite;
import org.openobservatory.ooniprobe.test.test.AbstractTest;

import java.util.Objects;

import javax.inject.Inject;

public class OverviewViewModel extends ViewModel {
    public MutableLiveData<AbstractSuite> suite = new MutableLiveData<>();
    PreferenceManager preferenceManager;

    @Inject
    public OverviewViewModel(@NotNull PreferenceManager preferenceManager) {
        this.preferenceManager = preferenceManager;
    }

    public void onTestSuiteChanged(AbstractSuite testSuite) {
        suite.setValue(testSuite);
    }

    public int getDataUsage() {
        return (suite.getValue() != null) ? suite.getValue().getDataUsage() : R.string.TestResults_NotAvailable;
    }

    public String getRunTime() {
        try {
            suite.getValue().setTestList((AbstractTest[]) null);
            suite.getValue().getTestList(preferenceManager);
            return suite.getValue().getRuntime(preferenceManager).toString();
        } catch (Exception e) {
            ThirdPartyServices.logException(e);
            return null;
        }
    }

    public String getName() {
        return (suite.getValue() != null) ? suite.getValue().getName() : null;
    }

    public String getLastTime() {
        Result lastResult = Result.getLastResult(getName());
        return lastResult != null ? DateUtils.getRelativeTimeSpanString(lastResult.start_time.getTime()).toString() : null;
    }

    public String getAuthor() {
        return suite.getValue() instanceof OONIRunSuite ? String.format("Author : %s", ((OONIRunSuite) suite.getValue()).getDescriptor().getAuthor()) : null;
    }

    public int getAuthorVisibility() {
        return (getAuthor() != null) ? View.VISIBLE : View.GONE;
    }

    public int getCustomUrlVisibility() {
        return Objects.equals(getName(), WebsitesSuite.NAME) ? View.VISIBLE : View.GONE;
    }
}
