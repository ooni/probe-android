package org.openobservatory.ooniprobe.model;

import java.sql.Date;

public class Result {
    String name;
    Date startTime;
    float duration;
    long dataUsageDown;
    long dataUsageUp;
    String ip;
    String asn;
    String asnName;
    String country;
    String networkName;
    String networkType;
    String summary;
    Summary summaryObj;
    boolean viewed;
    boolean done;
}
