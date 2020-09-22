package org.openobservatory.engine;

import java.util.ArrayList;

public class ArrayLogger extends AndroidLogger {
    public ArrayList<String> logs;

    public ArrayLogger(){
        logs = new ArrayList<>();
    }

    @Override
    public void debug(String message) {
        super.debug(message);
        logs.add(message);
    }

    @Override
    public void info(String message) {
        super.info(message);
        logs.add(message);
    }

    @Override
    public void warn(String message) {
        super.warn(message);
        logs.add(message);
    }
}
