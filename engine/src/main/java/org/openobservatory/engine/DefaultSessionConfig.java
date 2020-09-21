package org.openobservatory.engine;

import android.content.Context;

import oonimkall.SessionConfig;

public class DefaultSessionConfig {
    //Would this class be needed?
    public static SessionConfig getDefaultSessionConfig(Context ctx,
                                                 String softwareName,
                                                 String softwareVersion) {
        SessionConfig config = new SessionConfig();
        config.setLogger(null); // TODO(bassosimone): implement
        config.setSoftwareName(softwareName);
        config.setSoftwareVersion(softwareVersion);
        config.setVerbose(true);
        try {
            config.setAssetsDir(Engine.getAssetsDir(ctx));
            config.setStateDir(Engine.getStateDir(ctx));
            config.setTempDir(Engine.getTempDir(ctx));
        }
        catch (Exception e){
            //TODO
        }
        return config;
    }

}
