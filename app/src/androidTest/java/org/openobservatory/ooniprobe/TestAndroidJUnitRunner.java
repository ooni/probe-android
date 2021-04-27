package org.openobservatory.ooniprobe;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

import org.openobservatory.ooniprobe.factory.TestApplication;

public class TestAndroidJUnitRunner extends AndroidJUnitRunner {

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        return super.newApplication(cl, TestApplication.class.getName(), context);
    }
}
