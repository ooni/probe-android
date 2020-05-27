package org.openobservatory.engine;

/** OrchestraResults contains the results of speaking with OONI orchestra. */
public interface OrchestraResults {
    /** isGood indicates whether we succeeded. */
    public boolean isGood();

    /** getLogs returns the logs as one or more UTF-8 lines of text. */
    public String getLogs();
}
