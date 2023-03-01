package org.openobservatory.ooniprobe.common;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * TestProgressRepository used to track the {@link org.openobservatory.ooniprobe.common.service.RunTestService} progress with using {@link LiveData}
 */
@Singleton
public class TestProgressRepository {
    /**
     * Instantiates a new {@link TestProgressRepository}
     */
    @Inject
    TestProgressRepository() {
    }

    private final MutableLiveData<Integer> progressData = new MutableLiveData<>();
    private final MutableLiveData<Double> etaData = new MutableLiveData<>();

    /**
     * Gets progress.
     *
     * @return the progress
     */
    public LiveData<Integer> getProgress() {
        return progressData;
    }

    /**
     * Update progress.
     *
     * @param progress the progress
     */
    public void updateProgress(Integer progress) {
        progressData.setValue(progress);
    }

    /**
     * Gets eta.
     *
     * @return the eta
     */
    public LiveData<Double> getEta() {
        return etaData;
    }

    /**
     * Update eta.
     *
     * @param eta the eta
     */
    public void updateEta(Double eta) {
        etaData.setValue(eta);
    }
}
